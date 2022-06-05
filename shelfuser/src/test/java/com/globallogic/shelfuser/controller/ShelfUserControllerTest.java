package com.globallogic.shelfuser.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.globallogic.shelfuser.exeptions.BookshelfConflictException;
import com.globallogic.shelfuser.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.shelfuser.repository.ShelfUserRepository;
import com.globallogic.shelfuser.service.ShelfUserService;
import com.globallogic.shelfuser.utils.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {ShelfUserController.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ShelfUserControllerTest {
    @InjectMocks
    private ShelfUserController shelfUserController;

    @MockBean
    private ShelfUserRepository shelfUserRepository;

    @MockBean
    private ShelfUserService shelfUserService;

    private static MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(shelfUserController).build();
    }

    @Test
    void getUserStatusTest() throws Exception {
        when(shelfUserService.userStatusGet(any(), any())).thenReturn("PRESENT");

        mockMvc
                .perform(get("/shelfUser/userStatus").param("firstName", "Adam")
                        .param("lastName", "Kot"))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(content().string("PRESENT"));
    }


    @Test
    void userDeleteTest() throws Exception {

        mockMvc
                .perform(delete("/shelfUser/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(content().string("User with id=1 delete"));
    }


    @Test
    void userDeleteBookshelfConflictExceptionTest() throws Exception {
        doThrow(new BookshelfConflictException("User with id=1 doesn't exist")).when(this.shelfUserService)
                .userDelete((Integer) any());
        mockMvc
                .perform(delete("/shelfUser/{id}", 1))
                .andExpect(status().is(409));
    }


}

