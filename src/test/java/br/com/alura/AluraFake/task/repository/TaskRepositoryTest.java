package br.com.alura.AluraFake.task.repository;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.domain.OpenTextTask;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Course course;

    @BeforeEach
    void setUp() {
        User instructor = new User("Instrutor", "instrutor@email.com", Role.INSTRUCTOR);
        userRepository.save(instructor);
        course = new Course("Curso Teste", "Descricao", instructor);
        courseRepository.save(course);
    }

    @Test
    @DisplayName("existsByCourseAndStatement deve retornar true quando enunciado existe no curso")
    void existsByCourseAndStatement_shouldReturnTrue_whenStatementExists() {
        var task = new OpenTextTask();
        task.setStatement("Enunciado existente");
        task.setOrder(1);
        task.setCourse(course);
        taskRepository.save(task);

        boolean result = taskRepository.existsByCourseAndStatement(course, "Enunciado existente");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByCourseAndStatement deve retornar false quando enunciado nao existe no curso")
    void existsByCourseAndStatement_shouldReturnFalse_whenStatementDoesNotExist() {
        boolean result = taskRepository.existsByCourseAndStatement(course, "Enunciado inexistente");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("findByCourseAndOrderGreaterThanEqualOrderByOrder deve retornar tasks ordenadas")
    void findByCourseAndOrderGreaterThanEqualOrderByOrder_shouldReturnOrderedTasks() {
        var task1 = new OpenTextTask();
        task1.setStatement("Task 1");
        task1.setOrder(1);
        task1.setCourse(course);

        var task2 = new OpenTextTask();
        task2.setStatement("Task 2");
        task2.setOrder(2);
        task2.setCourse(course);

        var task3 = new OpenTextTask();
        task3.setStatement("Task 3");
        task3.setOrder(3);
        task3.setCourse(course);

        taskRepository.saveAll(List.of(task1, task2, task3));

        List<br.com.alura.AluraFake.task.domain.Task> result = taskRepository.findByCourseAndOrderGreaterThanEqualOrderByOrder(course, 2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getOrder()).isEqualTo(2);
        assertThat(result.get(1).getOrder()).isEqualTo(3);
        assertThat(result).extracting(br.com.alura.AluraFake.task.domain.Task::getStatement).containsExactly("Task 2", "Task 3");
    }
}