package br.com.alura.AluraFake.course.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.domain.MultipleChoiceTask;
import br.com.alura.AluraFake.task.domain.OpenTextTask;
import br.com.alura.AluraFake.task.domain.SingleChoiceTask;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        User instructor = new User("Victor", "victudanieu@gmail.com", Role.INSTRUCTOR);
        course = new Course("Java Básico", "Curso de Java", instructor);
    }

    @Test
    @DisplayName("Deve publicar o curso com sucesso quando todas as regras sao atendidas")
    void publishCourse__should_publish_course_when_all_rules_are_met() {
        var task1 = new OpenTextTask();
        task1.setOrder(1);
        var task2 = new SingleChoiceTask();
        task2.setOrder(2);
        var task3 = new MultipleChoiceTask();
        task3.setOrder(3);

        course.setTasks(List.of(task1, task2, task3));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.publishCourse(1L);

        verify(courseRepository).save(course);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(courseCaptor.capture());
        Course savedCourse = courseCaptor.getValue();

        assertThat(savedCourse.getStatus()).isEqualTo(Status.PUBLISHED);
        assertThat(savedCourse.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar publicar curso com ordem de atividades nao continua")
    void publishCourse__should_throw_exception_when_task_order_is_not_continuous() {
        var task1 = new OpenTextTask();
        task1.setOrder(1);
        var task3 = new MultipleChoiceTask();
        task3.setOrder(3);

        course.setTasks(List.of(task1, task3));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        var exception = assertThrows(IllegalStateException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertThat(exception.getMessage()).isEqualTo("Task sequence is not continuous. Missing order: 2");
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar publicar curso que nao esta com status BUILDING")
    void publishCourse__should_throw_exception_when_course_status_is_not_building() {
        course.setStatus(Status.PUBLISHED);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(IllegalStateException.class, () -> {
            courseService.publishCourse(1L);
        });
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar publicar curso que nao existe")
    void publishCourse__should_throw_exception_when_course_does_not_exist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            courseService.publishCourse(99L);
        });
    }
}