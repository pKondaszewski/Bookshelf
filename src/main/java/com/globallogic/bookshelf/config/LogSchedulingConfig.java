package com.globallogic.bookshelf.config;

import com.globallogic.bookshelf.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class LogSchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private LogService logService;

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                () -> logService.handleFile(),
                context -> {
                    Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
                    Instant nextExecutionTime = lastCompletionTime.orElseGet(Date::new).toInstant()
                                    .plusSeconds(logService.getIntervalTime() * 60);
                    return Date.from(nextExecutionTime);
                }
        );
    }
}