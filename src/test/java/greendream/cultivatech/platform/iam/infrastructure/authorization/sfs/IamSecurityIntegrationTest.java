package greendream.cultivatech.platform.iam.infrastructure.authorization.sfs;

import com.jayway.jsonpath.JsonPath;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.iam.infrastructure.tokens.jwt.JwtProperties;
import greendream.cultivatech.platform.iam.infrastructure.tokens.jwt.JwtTokenService;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IamSecurityIntegrationTest {

    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String FARMER_EMAIL = "farmer@example.com";
    private static final String INACTIVE_EMAIL = "inactive@example.com";
    private static final String PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordHashing passwordHashing;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private JwtProperties jwtProperties;

    @BeforeEach
    void cleanDatabase() {
        profileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void signInReturnsBearerTokenForValidCredentials() throws Exception {
        User farmer = createUser(FARMER_EMAIL, "FARMER", true);

        MvcResult result = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody(FARMER_EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(not(emptyString())))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(1800))
                .andReturn();

        String accessToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        assertNotNull(accessToken);
        org.junit.jupiter.api.Assertions.assertEquals(
                farmer.id().orElseThrow().value(),
                jwtTokenService.extractUserId(accessToken));
    }

    @Test
    void signInRejectsWrongPassword() throws Exception {
        createUser(FARMER_EMAIL, "FARMER", true);

        signInExpectingUnauthorized(FARMER_EMAIL, "wrong-password");
    }

    @Test
    void signInRejectsUnknownUser() throws Exception {
        signInExpectingUnauthorized("unknown@example.com", PASSWORD);
    }

    @Test
    void signInRejectsInactiveUser() throws Exception {
        createUser(INACTIVE_EMAIL, "FARMER", false);

        signInExpectingUnauthorized(INACTIVE_EMAIL, PASSWORD);
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void protectedEndpointWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithExpiredTokenReturns401() throws Exception {
        User admin = createUser(ADMIN_EMAIL, "ADMIN", true);
        Clock pastClock = Clock.fixed(Instant.now().minus(Duration.ofHours(2)), ZoneOffset.UTC);
        JwtTokenService expiredTokenIssuer = new JwtTokenService(jwtProperties, pastClock);
        String expiredToken = expiredTokenIssuer.issue(admin.id().orElseThrow()).accessToken();

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithFarmerTokenReturns403() throws Exception {
        createUser(FARMER_EMAIL, "FARMER", true);
        String token = signIn(FARMER_EMAIL, PASSWORD);

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void protectedEndpointWithAdminTokenReturns200() throws Exception {
        createUser(ADMIN_EMAIL, "ADMIN", true);
        String token = signIn(ADMIN_EMAIL, PASSWORD);

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void roleChangesAreReflectedOnSubsequentRequests() throws Exception {
        User farmer = createUser(FARMER_EMAIL, "FARMER", true);
        String token = signIn(FARMER_EMAIL, PASSWORD);

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

        Role adminRole = roleRepository.findByName(new RoleName("ADMIN")).orElseThrow();
        User updated = userRepository.findById(farmer.id().orElseThrow()).orElseThrow();
        updated.assignRole(adminRole.id().orElseThrow());
        userRepository.update(updated);

        mockMvc.perform(get("/api/v1/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void signUpRemainsPublic() throws Exception {
        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new-farmer@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new-farmer@example.com"));
    }

    @Test
    void nonSelectedEndpointsKeepTheirPreviousOpenBehaviour() throws Exception {
        User farmer = createUser(FARMER_EMAIL, "FARMER", true);

        mockMvc.perform(get("/api/v1/inventories"))
                .andExpect(status().isOk());

        // This route belongs to the Profile bounded context and must not be swallowed by
        // the IAM /api/v1/users/* authorization rule.
        mockMvc.perform(get("/api/v1/users/{userId}/profile", farmer.id().orElseThrow().value()))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiDocsArePublicAndDefineTheBearerScheme() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.bearerFormat").value("JWT"))
                .andExpect(content().string(containsString("bearerAuth")));
    }

    @Test
    void swaggerUiIsPublic() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void administrationOperationsAreDocumentedAsSecured() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/users'].get.security[0].bearerAuth").exists())
                .andExpect(jsonPath("$.paths['/api/v1/roles'].get.security[0].bearerAuth").exists());
    }

    @Test
    void signInOperationIsDocumentedAsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/authentication/sign-in'].post.security").doesNotExist());
    }

    private User createUser(String email, String roleName, boolean active) {
        Role role = roleRepository.findByName(new RoleName(roleName)).orElseThrow();
        User user = User.register(new EmailAddress(email), passwordHashing.hash(PASSWORD));
        user.assignRole(role.id().orElseThrow());
        if (!active) {
            user.deactivate();
        }
        return userRepository.save(user);
    }

    private String signIn(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private void signInExpectingUnauthorized(String email, String password) throws Exception {
        mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInBody(email, password)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    private String signInBody(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
    }
}
