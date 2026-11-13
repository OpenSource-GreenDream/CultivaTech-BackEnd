package greendream.cultivatech.platform.iam.application.commandservices;

import greendream.cultivatech.platform.iam.application.commands.SignInCommand;
import greendream.cultivatech.platform.iam.application.commands.SignUpCommand;
import greendream.cultivatech.platform.iam.application.results.SignInError;
import greendream.cultivatech.platform.iam.application.results.SignInResult;
import greendream.cultivatech.platform.iam.application.results.SignUpError;
import greendream.cultivatech.platform.iam.application.results.SignUpResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for authentication command operations.
 */
public interface AuthenticationCommandService {

    /**
     * Registers a new user.
     *
     * @param command sign-up command
     * @return successful sign-up result or an application error
     */
    Result<SignUpResult, SignUpError> signUp(SignUpCommand command);

    /**
     * Authenticates a user and issues a stateless access token.
     *
     * @param command sign-in command
     * @return successful sign-in result with the token, or an application error
     */
    Result<SignInResult, SignInError> signIn(SignInCommand command);
}
