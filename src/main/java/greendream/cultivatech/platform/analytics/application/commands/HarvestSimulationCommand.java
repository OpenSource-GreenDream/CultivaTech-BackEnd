package greendream.cultivatech.platform.analytics.application.commands;

import java.util.Date;

public record HarvestSimulationCommand(
        Long fieldId,
        Long productId,
        String cropType,
        String projectionPeriod,
        Date observationFrom,
        Date observationTo
) {
}
