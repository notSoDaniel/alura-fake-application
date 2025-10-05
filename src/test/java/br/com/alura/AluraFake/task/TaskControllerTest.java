package br.com.alura.AluraFake.task.controller;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser; // Importar
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    private Course course;
    private User instructor;

    @BeforeEach
    void setUp() {
        instructor = new User("John Doe", "john.doe@email.com", Role.INSTRUCTOR, "hashed_password");
        userRepository.save(instructor);
        course = new Course("API com Spring", "Curso de Spring", instructor);
        courseRepository.save(course);
    }

    @Test
    @DisplayName("Deve criar uma OpenTextTask e retornar status 201")
    @WithMockUser(roles = "INSTRUCTOR")
    void createOpenTextTask_withValidData_shouldReturnCreated() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "courseId", course.getId(),
                "statement", "O que eh uma API REST?",
                "order", 1
        );
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve criar uma SingleChoiceTask e retornar status 201")
    @WithMockUser(roles = "INSTRUCTOR")
    void createSingleChoiceTask_withValidData_shouldReturnCreated() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "courseId", course.getId(),
                "statement", "Qual nao eh primitivo?",
                "order", 1,
                "options", List.of(
                        Map.of("option", "integer", "isCorrect", false),
                        Map.of("option", "String", "isCorrect", true)
                )
        );
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Nao deve criar task com enunciado curto e retornar status 400")
    @WithMockUser(roles = "INSTRUCTOR")
    void createTask_withInvalidData_shouldReturnBadRequest() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "courseId", course.getId(),
                "statement", "Oi",
                "order", 1
        );
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andExpect(status().isBadRequest());
    }
}