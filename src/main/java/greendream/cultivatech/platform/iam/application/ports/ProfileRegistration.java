package greendream.cultivatech.platform.iam.application.ports;

import greendream.cultivatech.platform.iam.application.commands.ProfileRegistrationCommand;
import greendream.cultivatech.platform.iam.application.results.ProfileRegistrationError;
import greendream.cultivatech.platform.iam.application.results.ProfileRegistrationResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Outbound port for registering the profile of a user.
 * This port keeps the IAM application layer independent of the Profile
 * bounded context. The implementation is an infrastructure adapter that
 * translates between IAM and Profile application models.
 */
public interface ProfileRegistration {

    Result<ProfileRegistrationResult, ProfileRegistrationError> register(ProfileRegistrationCommand command);
}
