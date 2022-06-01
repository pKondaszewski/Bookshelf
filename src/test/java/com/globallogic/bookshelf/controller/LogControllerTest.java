package com.globallogic.bookshelf.controller;

import com.globallogic.bookshelf.service.LogService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {LogController.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class LogControllerTest {

    @MockBean
    private static LogService logService;

    @InjectMocks
    private LogController logController;

    private static MockMvc mockMvc;
    private static Integer minute;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(logController).build();
    }

    @BeforeAll
    public static void setModel() {
        minute = 10;
    }

    @Test
    void updateIntervalTimeSuccessTest() throws Exception {
        mockMvc
                .perform(put("/log/updateIntervalTime")
                        .param("minute", String.valueOf(minute)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Log interval time value updated with value = %s", minute)));
    }
}
