package br.com.alura.AluraFake.user.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.user.dto.InstructorReportDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve gerar relatorio com sucesso para um instrutor valido")
    void generateInstructorReport_should_return_report_for_valid_instructor() {
        long instructorId = 1L;
        var instructor = new User("Instrutor", "email@email.com", Role.INSTRUCTOR);

        var course1 = new Course("Curso Java", "Desc", instructor);
        course1.setStatus(Status.PUBLISHED); // Um curso publicado
        var course2 = new Course("Curso Docker", "Desc", instructor);
        course2.setStatus(Status.BUILDING); // Um curso nao publicado

        when(userRepository.findById(instructorId)).thenReturn(Optional.of(instructor));
        when(courseRepository.findByInstructor(instructor)).thenReturn(List.of(course1, course2));

        InstructorReportDTO report = userService.generateInstructorReport(instructorId);

        assertThat(report).isNotNull();
        assertThat(report.courses()).hasSize(2);
        assertThat(report.totalPublishedCourses()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve lancar EntityNotFoundException para usuario inexistente")
    void generateInstructorReport_should_throw_exception_for_non_existent_user() {
        long nonExistentId = 99L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.generateInstructorReport(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found with id: " + nonExistentId);
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException para usuario que nao eh instrutor")
    void generateInstructorReport_should_throw_exception_for_non_instructor_user() {
        long studentId = 2L;
        var student = new User("Estudante", "student@email.com", Role.STUDENT);

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> userService.generateInstructorReport(studentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with id " + studentId + " is not an instructor.");
    }
}