package com.in28minutes.microservices.currencyconversionservice.auth;

import com.in28minutes.microservices.currencyconversionservice.security.ApplicationUserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationUserServiceTest {

    private ApplicationUserService underTest;
    @Mock
    private FakeApplicationUserDaoService fakeApplicationUserDaoService;

    @BeforeEach
    void setUp() {
        underTest=new ApplicationUserService(fakeApplicationUserDaoService);
    }



    @Test
    @Disabled
    void itShouldCheckIfUserExistByUsername() {
        //given
        ApplicationUser applicationUser=new ApplicationUser(
                "imran",
                "password",
                ApplicationUserRole.STUDENT.getAuthority(),
                true,
                true,
                true
                ,true
        );

        String s="imran";
        UserDetails userDetails=underTest.loadUserByUsername(s);

        // when
        ArgumentCaptor<String> applicationUserArgumentCaptor=ArgumentCaptor.forClass(String.class);

        //then
        verify(fakeApplicationUserDaoService).selectApplicationUserName(applicationUserArgumentCaptor.capture());

        String userArgumentCaptorValue=applicationUserArgumentCaptor.getValue();

        assertThat(userArgumentCaptorValue).isEqualTo(s);
    }


    @Test
    void itShouldCheckIfUserExistByUsernameException() {
        String s="imran";
        assertThatThrownBy(()->underTest.loadUserByUsername(s)).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("user not found :" + s);

        UserDetails UserDetails = null;
        given(underTest.loadUserByUsername(s)).willReturn(UserDetails);

    }
}