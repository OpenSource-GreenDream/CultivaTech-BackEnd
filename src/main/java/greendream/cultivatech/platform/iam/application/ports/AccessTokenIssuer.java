package greendream.cultivatech.platform.iam.application.ports;

import greendream.cultivatech.platform.iam.application.results.IssuedAccessToken;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;

/**
 * Application port for issuing access tokens.
 *
 * <p>The contract deliberately avoids exposing any token-library type so that the
 * application layer remains independent from the JWT implementation.</p>
 */
public interface AccessTokenIssuer {

    /**
     * Issues a new access token for the given user.
     *
     * @param userId immutable user identity used as the token subject
     * @return signed access token and its lifetime
     */
    IssuedAccessToken issue(UserId userId);
}
