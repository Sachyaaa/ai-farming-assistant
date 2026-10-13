package com.sachin.ai_farming_assistant.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.CreateLogStreamRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourceAlreadyExistsException;

public class CloudWatchLogbackAppender extends AppenderBase<ILoggingEvent> {

    private static final String LOG_GROUP = "/farmassist/dev/application";
    private static final String LOG_STREAM = "farmassist-spring-boot";

    private CloudWatchLogsClient cloudWatchLogsClient;

    @Override
    public void start() {
        try {
            cloudWatchLogsClient = CloudWatchLogsClient.builder()
                    .region(Region.AP_SOUTHEAST_2)
                    .build();

            try {
                cloudWatchLogsClient.createLogStream(
                        CreateLogStreamRequest.builder()
                                .logGroupName(LOG_GROUP)
                                .logStreamName(LOG_STREAM)
                                .build()
                );
            } catch (ResourceAlreadyExistsException ignored) {
                // Reuse the existing stream.
            }

            super.start();
        } catch (Exception exception) {
            addError("Unable to initialize CloudWatch logging.", exception);
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (cloudWatchLogsClient == null) {
            return;
        }

        try {
            String message = String.format(
                    "%s [%s] %s - %s",
                    new java.util.Date(event.getTimeStamp()),
                    event.getLevel(),
                    event.getLoggerName(),
                    event.getFormattedMessage()
            );

            InputLogEvent logEvent = InputLogEvent.builder()
                    .timestamp(event.getTimeStamp())
                    .message(message)
                    .build();

            cloudWatchLogsClient.putLogEvents(
                    PutLogEventsRequest.builder()
                            .logGroupName(LOG_GROUP)
                            .logStreamName(LOG_STREAM)
                            .logEvents(logEvent)
                            .build()
            );

        } catch (Exception exception) {
            // Do not log this failure through the same appender.
            addError("Failed to send an application log to CloudWatch.");
        }
    }

    @Override
    public void stop() {
        super.stop();

        if (cloudWatchLogsClient != null) {
            cloudWatchLogsClient.close();
        }
    }
}
