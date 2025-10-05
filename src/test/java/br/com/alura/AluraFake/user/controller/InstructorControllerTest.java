package br.com.alura.AluraFake.user.controller;

import br.com.alura.AluraFake.infra.security.TokenService;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.user.dto.InstructorReportDTO;
import br.com.alura.AluraFake.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve retornar 200 OK e o relatorio quando o usuario eh um instrutor valido")
    @WithMockUser(roles = "INSTRUCTOR")
    void getInstructorReport_whenUserIsValidInstructor_shouldReturnReport() throws Exception {
        Long instructorId = 1L;
        InstructorReportDTO fakeReport = new InstructorReportDTO(Collections.emptyList(), 0);
        when(userService.generateInstructorReport(instructorId)).thenReturn(fakeReport);

        mockMvc.perform(get("/instructor/{id}/courses", instructorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPublishedCourses").value(0));
    }

    @Test
    @DisplayName("Deve retornar 404 quando o UserService lanca EntityNotFoundException")
    @WithMockUser(roles = "INSTRUCTOR")
    void getInstructorReport_whenUserNotFound_shouldReturnNotFound() throws Exception {
        Long nonExistentId = 99L;
        when(userService.generateInstructorReport(nonExistentId))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/instructor/{id}/courses", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 400 quando o UserService lanca IllegalArgumentException (usuario nao eh instrutor)")
    @WithMockUser(roles = "INSTRUCTOR")
    void getInstructorReport_whenUserIsNotAnInstructor_shouldReturnBadRequest() throws Exception {
        Long studentId = 2L;
        String errorMessage = "User with id 2 is not an instructor.";
        when(userService.generateInstructorReport(studentId))
                .thenThrow(new IllegalArgumentException(errorMessage));

        mockMvc.perform(get("/instructor/{id}/courses", studentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("businessRule"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}