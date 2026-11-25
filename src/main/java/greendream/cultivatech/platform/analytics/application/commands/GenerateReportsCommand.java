package greendream.cultivatech.platform.analytics.application.commands;
import java.util.Date;
public record GenerateReportsCommand(Long deviceId, Date from, Date to) { }
