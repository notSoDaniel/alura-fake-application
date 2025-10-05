package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserListItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class UserListItemDTOTest {

    @Test
    @DisplayName("Deve mapear corretamente uma entidade User para o UserListItemDTO")
    void constructor__should_map_fields_from_user_entity() {
        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("Joao Aluno");
        when(mockUser.getEmail()).thenReturn("joao@email.com");
        when(mockUser.getRole()).thenReturn(Role.STUDENT);

        UserListItemDTO dto = new UserListItemDTO(mockUser);

        assertThat(dto.getName()).isEqualTo("Joao Aluno");
        assertThat(dto.getEmail()).isEqualTo("joao@email.com");
        assertThat(dto.getRole()).isEqualTo(Role.STUDENT);
    }
}