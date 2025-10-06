package br.com.alura.AluraFake.user.service;

import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Deve retornar UserDetails quando usuario eh encontrado pelo email")
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        String email = "paulo@email.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = authenticationService.loadUserByUsername(email);

        assertThat(userDetails).isEqualTo(user);
    }

    @Test
    @DisplayName("Deve lancar UsernameNotFoundException quando usuario nao eh encontrado")
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        String email = "naoexiste@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + email);
    }
}