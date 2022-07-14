package com.globallogic.shelfuser.service;

import com.globallogic.shelfuser.entity.ShelfUser;
import com.globallogic.shelfuser.exeptions.BookshelfResourceNotFoundException;
import com.globallogic.shelfuser.repository.ShelfUserRepository;
import com.globallogic.shelfuser.utils.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ShelfUserService.class})
@ExtendWith(MockitoExtension.class)
class ShelfUserServiceTest {
    @Mock
    private static ShelfUserRepository shelfUserRepository;

    @InjectMocks
    private static ShelfUserService shelfUserService;

    private static ShelfUser shelfUser;

    @BeforeAll
    public static void initVariables() {
        shelfUserService = new ShelfUserService(shelfUserRepository);
        shelfUser = new ShelfUser(null, "Adam", "Kot", "ACTIVE");

    }


    @Test
    public void userDeleteTest() {

        Optional<ShelfUser> ofResult = Optional.of(shelfUser);
        doNothing().when(shelfUserRepository).deleteById(1);
        when(shelfUserRepository.findById(1)).thenReturn(ofResult);
        shelfUserService.userDelete(1);
        verify(shelfUserRepository).findById(1);
        verify(shelfUserRepository).deleteById(1);
    }


    @Test
    public void userDeleteBookshelfResourceNotFoundExceptionTest() {

        Optional<ShelfUser> ofResult = Optional.of(shelfUser);
        doThrow(new BookshelfResourceNotFoundException("User with id=1 doesn't exist")).when(shelfUserRepository)
                .deleteById(1);
        when(shelfUserRepository.findById(1)).thenReturn(ofResult);
        assertThrows(BookshelfResourceNotFoundException.class, () -> shelfUserService.userDelete(1));
        verify(shelfUserRepository).findById(1);
        verify(shelfUserRepository).deleteById(1);
    }


    @Test
    void userChangeStatusTest() {
        shelfUser.setStatus("INACTIVE");
        when(shelfUserRepository.findByFirstnameAndLastname("Adam", "Kot")).thenReturn(Optional.of(shelfUser));
        shelfUserService.userChangeStatus("Adam", "Kot", Status.ACTIVE);
        verify(shelfUserRepository).findByFirstnameAndLastname("Adam", "Kot");
        verify(shelfUserRepository).save(shelfUser);
    }


    @Test
    void userStatusGetTest() {

        when(shelfUserRepository.findByFirstnameAndLastname("Adam","Kot")).thenReturn(Optional.of(shelfUser));
        assertEquals("ACTIVE", shelfUserService.userStatusGet("Adam", "Kot"));
        verify(shelfUserRepository).findByFirstnameAndLastname("Adam", "Kot");
    }


    @Test
    void testUserStatusGet2() {
        when(shelfUserRepository.findByFirstnameAndLastname((String) any(), (String) any()))
                .thenThrow(new BookshelfResourceNotFoundException(String.format("User %s %s doesn't exist", shelfUser.getFirstname(), shelfUser.getLastname())));
        assertThrows(BookshelfResourceNotFoundException.class, () -> shelfUserService.userStatusGet("Adam", "Kot"));
        verify(shelfUserRepository).findByFirstnameAndLastname((String) any(), (String) any());
    }


    @Test
    public void userActiveAddTest() {
        shelfUser.setStatus("ACTIVE");
        when(shelfUserRepository.save(shelfUser)).thenReturn(shelfUser);
        shelfUserService.userAdd("Adam", "Kot", Status.ACTIVE);
        verify(shelfUserRepository).save(shelfUser);

    }


    @Test
    public void userInactiveAddTest() {
        shelfUser.setStatus("INACTIVE");
        when(shelfUserRepository.save(shelfUser)).thenReturn(shelfUser);
        shelfUserService.userAdd("Adam", "Kot", Status.INACTIVE);
        verify(shelfUserRepository).save(shelfUser);
    }


    @Test
    public void userNotPresentAddTest() {
        shelfUser.setStatus("NOT_PRESENT");
        when(shelfUserRepository.save(shelfUser)).thenReturn(shelfUser);
        shelfUserService.userAdd("Adam", "Kot", Status.NOT_PRESENT);
        verify(shelfUserRepository).save(shelfUser);
    }


}

