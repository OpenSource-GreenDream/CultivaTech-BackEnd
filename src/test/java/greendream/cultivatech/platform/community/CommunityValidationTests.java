package greendream.cultivatech.platform.community;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
import greendream.cultivatech.platform.community.application.services.CommunityService;
import greendream.cultivatech.platform.community.domain.model.aggregates.Comment;
import greendream.cultivatech.platform.community.domain.model.aggregates.CommunityProfile;
import greendream.cultivatech.platform.community.domain.repositories.CommentRepository;
import greendream.cultivatech.platform.community.domain.repositories.CommunityProfileRepository;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.services.IamUserPrincipal;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldSize;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Latitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Longitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.SoilType;

import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.domain.exceptions.ForbiddenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class CommunityValidationTests {

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordHashing passwordHashing;
    @Autowired private ProfileRepository profileRepository;
    @Autowired private FieldRepository fieldRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CommunityProfileRepository communityProfileRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private CommunityService communityService;

    private Long paquitoUserId;
    private Long patoUserId;
    private Long pataUserId;
    private Long paquitoProfileId;
    private Long patoProfileId;
    private Long pataProfileId;

    @BeforeEach
    void setup() {
        commentRepository.deleteAll();
        communityProfileRepository.deleteAll();
        // fields/products not cleaned by repos deleteAll? FieldRepository extends ICrudService
        try { fieldRepository.deleteAll(); } catch (Exception ignored) {}
        profileRepository.deleteAll();
        userRepository.deleteAll();

        paquitoUserId = createUser("paquito@example.com", "FARMER");
        patoUserId = createUser("pato@example.com", "FARMER");
        pataUserId = createUser("pata@example.com", "FARMER");

        paquitoProfileId = createProfile(paquitoUserId);
        patoProfileId = createProfile(patoUserId);
        pataProfileId = createProfile(pataUserId);

        communityService.createProfile(paquitoProfileId, "Paquito", 0, "bio", 1);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private Long createUser(String email, String roleName) {
        Role role = roleRepository.findByName(new RoleName(roleName)).orElseThrow();
        User user = User.register(new EmailAddress(email), passwordHashing.hash("password123"));
        user.assignRole(role.id().orElseThrow());
        return userRepository.save(user).id().orElseThrow().value();
    }

    private Long createProfile(Long userId) {
        greendream.cultivatech.platform.profile.application.commands.RegisterProfileCommand cmd =
                new greendream.cultivatech.platform.profile.application.commands.RegisterProfileCommand(
                        userId, "Fundo", "+51000000000", 0.0, 0.0);
        // use repository directly
        Profile profile = Profile.register(
                new greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId(userId),
                new greendream.cultivatech.platform.profile.domain.model.valueobjects.FundoName("Fundo"),
                new greendream.cultivatech.platform.profile.domain.model.valueobjects.ContactPhone("+51000000000"),
                new greendream.cultivatech.platform.profile.domain.model.valueobjects.MoistureThreshold(0.0),
                new greendream.cultivatech.platform.profile.domain.model.valueobjects.TemperatureThreshold(0.0));
        return profileRepository.save(profile).id().orElseThrow().value();
    }

    private void authAs(Long userId, String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new IamUserPrincipal(userId, email, true, List.of()), null, List.of()));
    }

    @Test
    void ctLifecycle() {
        authAs(patoUserId, "pato@example.com");
        // CT-01 already 0.00
        assertEquals(new BigDecimal("0.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        // CT-02
        communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "bueno", 4);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        authAs(pataUserId, "pata@example.com");
        // CT-03
        communityService.createComment(pataProfileId, "PROFILE", paquitoProfileId, null, null, "ok", 3);
        assertEquals(new BigDecimal("3.50"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        authAs(patoUserId, "pato@example.com");
        // CT-04 modify rating 4->5
        Long patoCommentId = commentRepository.findByTargetProfileId(paquitoProfileId).stream()
                .filter(c -> c.getAuthorProfileId().equals(patoProfileId)).findFirst().orElseThrow().getId();
        communityService.updateComment(patoCommentId, "bueno", 5);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        // CT-05 content only
        communityService.updateComment(patoCommentId, "excelente", 5);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        // CT-06 FIELD comment rating 1
        Long fieldId = createField(paquitoProfileId);
        authAs(patoUserId, "pato@example.com");
        communityService.createComment(patoProfileId, "FIELD", paquitoProfileId, fieldId, null, "terreno", 1);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        // CT-07 PRODUCT comment rating 2
        Long productId = createProduct(paquitoProfileId, fieldId);
        communityService.createComment(patoProfileId, "PRODUCT", paquitoProfileId, null, productId, "producto", 2);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        // CT-08 delete FIELD
        Long fieldCommentId = commentRepository.findByFieldId(fieldId).get(0).getId();
        communityService.deleteComment(fieldCommentId);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        // CT-09 delete PRODUCT
        Long productCommentId = commentRepository.findByProductId(productId).get(0).getId();
        communityService.deleteComment(productCommentId);
        assertEquals(new BigDecimal("4.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        authAs(pataUserId, "pata@example.com");
        // CT-10 pata deletes profile comment
        Long pataCommentId = commentRepository.findByTargetProfileId(paquitoProfileId).stream()
                .filter(c -> c.getAuthorProfileId().equals(pataProfileId)).findFirst().orElseThrow().getId();
        communityService.deleteComment(pataCommentId);
        assertEquals(new BigDecimal("5.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());

        authAs(patoUserId, "pato@example.com");
        // CT-11 pato deletes profile comment
        communityService.deleteComment(patoCommentId);
        assertEquals(new BigDecimal("0.00"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());
    }

    @Test
    void halfUpRounding() {
        authAs(patoUserId, "pato@example.com");
        communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "a", 4);
        authAs(pataUserId, "pata@example.com");
        communityService.createComment(pataProfileId, "PROFILE", paquitoProfileId, null, null, "b", 4);
        // third user
        Long terceroUserId = createUser("tercero@example.com", "FARMER");
        Long terceroProfileId = createProfile(terceroUserId);
        authAs(terceroUserId, "tercero@example.com");
        communityService.createComment(terceroProfileId, "PROFILE", paquitoProfileId, null, null, "c", 3);
        assertEquals(new BigDecimal("3.67"), communityProfileRepository.findByProfileId(paquitoProfileId).orElseThrow().getReputationScore());
    }

    @Test
    void reputationIntegrity() {
        // RS-01 create with reputation 5 ignored
        communityService.createProfile(patoProfileId, "Pato", 5, "bio", 1);
        assertEquals(new BigDecimal("0.00"), communityProfileRepository.findByProfileId(patoProfileId).orElseThrow().getReputationScore());

        // RS-02 update with reputation 5 ignored
        CommunityProfile p = communityProfileRepository.findByProfileId(patoProfileId).orElseThrow();
        communityService.updateProfile(p.getId(), "Pato2", 5, "bio2", 1);
        assertEquals(new BigDecimal("0.00"), communityProfileRepository.findByProfileId(patoProfileId).orElseThrow().getReputationScore());

        // RS-03 update nickname/bio keeps reputation
        assertEquals("Pato2", communityProfileRepository.findByProfileId(patoProfileId).orElseThrow().getNickname());
    }

    @Test
    void authorization() {
        authAs(patoUserId, "pato@example.com");
        communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "bueno", 4);
        Long commentId = commentRepository.findByTargetProfileId(paquitoProfileId).get(0).getId();

        // UC-02 another user -> 403
        authAs(pataUserId, "pata@example.com");
        assertThrows(ForbiddenException.class, () -> communityService.updateComment(commentId, "x", 2));

        // UC-07 delete by other -> 403
        assertThrows(ForbiddenException.class, () -> communityService.deleteComment(commentId));

        // UC-08 unauthenticated -> 401
        SecurityContextHolder.clearContext();
        assertThrows(greendream.cultivatech.platform.shared.domain.exceptions.UnauthorizedException.class,
                () -> communityService.updateComment(commentId, "x", 2));

        // UC-03 nonexistent -> 404
        authAs(patoUserId, "pato@example.com");
        assertThrows(greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException.class,
                () -> communityService.updateComment(999999L, "x", 2));
    }

    @Test
    void invalidRatingsAndContent() {
        authAs(patoUserId, "pato@example.com");
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "x", 0));
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "x", 6));
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "   ", 4));
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(patoProfileId, "PROFILE", paquitoProfileId, null, null, "", 4));
        // self-rating
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(paquitoProfileId, "PROFILE", paquitoProfileId, null, null, "x", 4));
    }

    @Test
    void spoofingAuthor() {
        authAs(patoUserId, "pato@example.com");
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(pataProfileId, "PROFILE", paquitoProfileId, null, null, "x", 4));
    }

    @Test
    void inconsistentTargets() {
        authAs(patoUserId, "pato@example.com");
        Long fieldId = createField(paquitoProfileId);
        Long productId = createProduct(paquitoProfileId, fieldId);
        // FIELD without fieldId
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(patoProfileId, "FIELD", paquitoProfileId, null, null, "x", 1));
        // PRODUCT without productId
        assertThrows(IllegalArgumentException.class, () ->
                communityService.createComment(patoProfileId, "PRODUCT", paquitoProfileId, null, null, "x", 2));
        // nonexistent profile target
        assertThrows(greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException.class, () ->
                communityService.createComment(patoProfileId, "PROFILE", 999999L, null, null, "x", 4));
    }

    private Long createField(Long profileId) {
        Field field = Field.create(
                new ProfileId(profileId),
                new FieldName("Field"),
                new FieldSize(10.0),
                new SoilType("CLAY"),
                new Latitude(1.0),
                new Longitude(2.0));
        Field saved = fieldRepository.save(field);
        return saved.id().orElseThrow().value();
    }

    private Long createProduct(Long profileId, Long fieldId) {
        Product product = productRepository.save(new Product(null, System.nanoTime(), fieldId, "Prod", "desc",
                new BigDecimal("10.00"), "type", "http://img"));
        return product.id();
    }
}