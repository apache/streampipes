package org.apache.streampipes.service.core.taskScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

import java.util.Map;

import org.apache.streampipes.model.datalake.RetentionInterval;

@Component
public abstract class TaskScheduler {
    protected static final String SCHEDULING_INTERVAL_ENV_VAR = "BF_WEEKLY_REPORT_SCHEDULING_INTERVAL";
    protected static final String SCHEDULING_INTERVAL_DEFAULT = "0 0 14 * * TUE"; // weekly Tuesday 14:00

    private static final Logger LOG = LoggerFactory.getLogger(TaskScheduler.class);

    protected ScheduledFuture<?> scheduledFuture;
    protected ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    public void init() {
        // Default fallback scheduling (env or default cron)
        var schedulingInterval = getSchedulingInterval();
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduleCronTask(schedulingInterval);
    }

    @PreDestroy
    public void destroy() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (scheduler != null) {
            scheduler.destroy();
        }
    }

    abstract void scheduleCronTask(String cronExpression);

    protected String getSchedulingInterval() {
        var env = System.getenv(SCHEDULING_INTERVAL_ENV_VAR);
        if (env != null && !env.isBlank()) {
            LOG.info("Configured weekly report with interval {}", env);
            return env;
        } else {
            LOG.info(
                "No environment variable {} found, using default settings {}",
                SCHEDULING_INTERVAL_ENV_VAR,
                SCHEDULING_INTERVAL_DEFAULT
            );
            return SCHEDULING_INTERVAL_DEFAULT;
        }
    }

    protected String mapIntervalToCron(RetentionInterval interval) {
        return switch (interval) {
            case DAILY   -> "0 0 0 * * *";        // every day at midnight
            case WEEKLY  -> "0 0 0 * * MON";      // every Monday at midnight
            case MONTHLY -> "0 0 0 1 * *";        // first day of each month
        };
    }

    public void rebuildSchedule(RetentionInterval interval) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        var cron = mapIntervalToCron(interval);
        LOG.info("Rebuilding scheduler with interval: {} → cron: {}", interval, cron);
        scheduleCronTask(cron);
    }
}

