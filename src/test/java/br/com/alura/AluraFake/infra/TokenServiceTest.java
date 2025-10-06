package br.com.alura.AluraFake.infra.security;

import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "alura-fake-secret-key-para-gerar-tokens-jwt-com-seguranca");
    }

    @Test
    @DisplayName("Deve gerar um token JWT valido e extrair o subject corretamente")
    void generateToken_shouldCreateValidTokenAndExtractSubject() {
        User user = new User();
        user.setEmail("teste@email.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        String token = tokenService.generateToken(authentication);

        String subject = tokenService.getSubject(token);
        assertThat(token).isNotNull().isNotBlank();
        assertThat(subject).isEqualTo("teste@email.com");
    }
}