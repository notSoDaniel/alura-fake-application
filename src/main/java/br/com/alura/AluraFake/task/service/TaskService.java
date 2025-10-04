package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.domain.OpenTextTask;
import br.com.alura.AluraFake.task.domain.Task;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskRequest;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Task createOpenTextTask(NewOpenTextTaskRequest dto) {

        Course course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + dto.courseId()));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Cannot add tasks to a course that is not in BUILDING status.");
        }

        if (taskRepository.existsByCourseAndStatement(course, dto.statement())) {
            throw new IllegalArgumentException("A task with the same statement already exists in this course.");
        }

        int newOrder = dto.order();
        int totalTasks = course.getTasks().size();

        if (newOrder > totalTasks + 1) {
            throw new IllegalArgumentException("Invalid task order. The order must be sequential. Next available order is " + (totalTasks + 1));
        }

        if (newOrder <= totalTasks) {

            List<Task> tasksToShift = taskRepository.findByCourseAndOrderGreaterThanEqualOrderByOrder(course, newOrder);

            for (Task task : tasksToShift) {
                task.setOrder(task.getOrder() + 1);
            }

            taskRepository.saveAll(tasksToShift);
        }

        OpenTextTask newTask = new OpenTextTask();
        newTask.setCourse(course);
        newTask.setStatement(dto.statement());
        newTask.setOrder(newOrder);

        return taskRepository.save(newTask);
    }
}