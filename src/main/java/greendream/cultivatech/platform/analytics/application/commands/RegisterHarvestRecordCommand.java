package greendream.cultivatech.platform.analytics.application.commands;
import java.util.Date;
public record RegisterHarvestRecordCommand(Long fieldId,String cropType,String season,double harvestedAreaM2,double harvestedQuantityTons,Date harvestedAt) { }
