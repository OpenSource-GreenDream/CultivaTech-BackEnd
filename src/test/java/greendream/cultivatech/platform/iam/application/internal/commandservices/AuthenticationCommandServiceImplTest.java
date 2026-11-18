package greendream.cultivatech.platform.iam.application.internal.commandservices;

import greendream.cultivatech.platform.iam.application.commands.SignInCommand;
import greendream.cultivatech.platform.iam.application.ports.AccessTokenIssuer;
import greendream.cultivatech.platform.iam.application.ports.ProfileRegistration;
import greendream.cultivatech.platform.iam.application.results.IssuedAccessToken;
import greendream.cultivatech.platform.iam.application.results.SignInError;
import greendream.cultivatech.platform.iam.application.results.SignInResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationCommandServiceImplTest {

    private static final String EMAIL = "farmer@example.com";
    private static final String PASSWORD = "password123";
    private static final String INVALID_CREDENTIALS = "error.authentication.invalid-credentials";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordHashing passwordHashing;
    @Mock
    private ProfileRegistration profileRegistration;
    @Mock
    private AccessTokenIssuer accessTokenIssuer;

    private AuthenticationCommandServiceImpl authenticationCommandService;
    private EmailAddress email;
    private PasswordHash passwordHash;

    @BeforeEach
    void setUp() {
        authenticationCommandService = new AuthenticationCommandServiceImpl(
                userRepository,
                roleRepository,
                passwordHashing,
                profileRegistration,
                accessTokenIssuer);
        email = new EmailAddress(EMAIL);
        passwordHash = new PasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
    }

    @Test
    void signInReturnsTokenForValidCredentials() {
        User user = user(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHashing.matches(PASSWORD, passwordHash)).thenReturn(true);
        when(accessTokenIssuer.issue(new UserId(7L)))
                .thenReturn(new IssuedAccessToken("signed.jwt.token", 1800L));

        Result<SignInResult, SignInError> result =
                authenticationCommandService.signIn(new SignInCommand(EMAIL, PASSWORD));

        assertTrue(result.isSuccess());
        SignInResult signInResult = result.success().orElseThrow();
        assertEquals("signed.jwt.token", signInResult.accessToken());
        assertEquals("Bearer", signInResult.tokenType());
        assertEquals(1800L, signInResult.expiresIn());
    }

    @Test
    void signInRejectsWrongPassword() {
        User user = user(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHashing.matches(PASSWORD, passwordHash)).thenReturn(false);

        assertInvalidCredentials(authenticationCommandService.signIn(new SignInCommand(EMAIL, PASSWORD)));
        verify(accessTokenIssuer, never()).issue(any());
    }

    @Test
    void signInRejectsUnknownUserWithGenericError() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordHashing.matches(eq(PASSWORD), any(PasswordHash.class))).thenReturn(false);

        assertInvalidCredentials(authenticationCommandService.signIn(new SignInCommand(EMAIL, PASSWORD)));
        verify(accessTokenIssuer, never()).issue(any());
    }

    @Test
    void signInRejectsInactiveUser() {
        User user = user(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordHashing.matches(PASSWORD, passwordHash)).thenReturn(true);

        assertInvalidCredentials(authenticationCommandService.signIn(new SignInCommand(EMAIL, PASSWORD)));
        verify(accessTokenIssuer, never()).issue(any());
    }

    @Test
    void signInRejectsNullCommand() {
        Result<SignInResult, SignInError> result = authenticationCommandService.signIn(null);

        assertTrue(result.isFailure());
        assertEquals("error.command.required", result.failure().orElseThrow().messageKey());
    }

    private User user(boolean active) {
        User user = User.reconstitute(
                new UserId(7L),
                email,
                passwordHash,
                Set.of(new RoleId(2L)),
                active);
        if (!active) {
            user.deactivate();
        }
        return user;
    }

    private void assertInvalidCredentials(Result<SignInResult, SignInError> result) {
        assertTrue(result.isFailure());
        assertEquals(INVALID_CREDENTIALS, result.failure().orElseThrow().messageKey());
    }
}
