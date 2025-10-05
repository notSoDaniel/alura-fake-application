package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.domain.OpenTextTask;
import br.com.alura.AluraFake.task.domain.SingleChoiceTask;
import br.com.alura.AluraFake.task.domain.Task;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskRequest;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.OptionRequest;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private TaskService taskService;

    private Course course;

    @BeforeEach
    void setUp() {
        User instructor = new User("Instrutor Teste", "instrutor@teste.com", Role.INSTRUCTOR);
        course = new Course("Java Completo", "Curso de Java", instructor);
        course.setTasks(new ArrayList<>());
    }

    @Test
    @DisplayName("Deve criar atividade e reordenar as existentes")
    void createOpenTextTask_whenOrderExists_shouldShiftExistingTasks() {
        Task taskA = new OpenTextTask();
        taskA.setOrder(1);
        Task taskB = new OpenTextTask();
        taskB.setOrder(2);
        List<Task> existingTasks = new ArrayList<>(List.of(taskA, taskB));
        course.setTasks(existingTasks);
        var request = new NewOpenTextTaskRequest(1L, "GC?", 1);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseAndOrderGreaterThanEqualOrderByOrder(course, 1)).thenReturn(existingTasks);
        when(taskRepository.save(any(OpenTextTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task newTask = taskService.createOpenTextTask(request);

        assertNotNull(newTask);
        assertEquals(1, newTask.getOrder());
        assertEquals(2, taskA.getOrder());
        assertEquals(3, taskB.getOrder());
        verify(taskRepository).saveAll(existingTasks);
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar adicionar task com enunciado duplicado")
    void createOpenTextTask_whenStatementExists_shouldThrowException() {
        var request = new NewOpenTextTaskRequest(1L, "Repetido", 1);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByCourseAndStatement(course, "Repetido")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> taskService.createOpenTextTask(request));
    }

    @Test
    @DisplayName("Deve criar SingleChoiceTask com sucesso")
    void createSingleChoiceTask_withValidData_shouldSucceed() {
        var options = List.of(
                new OptionRequest("A", true),
                new OptionRequest("B", false)
        );
        var request = new NewSingleChoiceTaskRequest(1L, "Qual a correta?", 1, options);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.save(any(SingleChoiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.createSingleChoiceTask(request);

        assertNotNull(result);
        assertTrue(result instanceof SingleChoiceTask);
        assertEquals(1, ((SingleChoiceTask) result).getOptions().stream().filter(o -> o.isCorrect()).count());
    }

    @Test
    @DisplayName("Deve lancar excecao para SingleChoiceTask com mais de uma opcao correta")
    void createSingleChoiceTask_withMultipleCorrectOptions_shouldThrowException() {
        var options = List.of(
                new OptionRequest("A", true),
                new OptionRequest("B", true)
        );
        var request = new NewSingleChoiceTaskRequest(1L, "Qual a correta?", 1, options);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> taskService.createSingleChoiceTask(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Single choice task must have exactly one correct option.");
    }
}