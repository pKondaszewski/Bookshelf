package com.globallogic.bookshelf.config;

import com.globallogic.bookshelf.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {LogSchedulingConfig.class})
@ExtendWith(SpringExtension.class)
class LogSchedulingConfigTest {

    @Autowired
    private LogSchedulingConfig logSchedulingConfig;

    @MockBean
    private LogService logService;

    @Test
    void configureTasksTest() {
        ScheduledTaskRegistrar scheduledTaskRegistrar = new ScheduledTaskRegistrar();
        logSchedulingConfig.configureTasks(scheduledTaskRegistrar);

        assertTrue(scheduledTaskRegistrar.hasTasks());
    }
}

