package br.com.alura.AluraFake.user;

import br.com.alura.AluraFake.infra.security.TokenService;
import br.com.alura.AluraFake.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newUser__should_return_bad_request_when_email_is_blank() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        mockMvc.perform(post("/user/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newUser__should_return_bad_request_when_email_is_invalid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        mockMvc.perform(post("/user/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("email"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newUser__should_return_bad_request_when_email_already_exists() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio.bugorin@alura.com.br");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(true);

        mockMvc.perform(post("/user/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("email"))
                .andExpect(jsonPath("$.message").value("Email já cadastrado no sistema"));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void newUser__should_return_created_when_user_request_is_valid() throws Exception {
        NewUserDTO newUserDTO = new NewUserDTO();
        newUserDTO.setEmail("caio.bugorin@alura.com.br");
        newUserDTO.setName("Caio Bugorin");
        newUserDTO.setRole(Role.STUDENT);

        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(false);

        mockMvc.perform(post("/user/new")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void listAllUsers__should_list_all_users() throws Exception {
        User user1 = new User("User 1", "user1@test.com",Role.STUDENT);
        User user2 = new User("User 2", "user2@test.com",Role.STUDENT);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[1].name").value("User 2"));
    }

}