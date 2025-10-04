package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.domain.*;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskRequest;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskRequest;
import br.com.alura.AluraFake.task.dto.OptionRequest;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Course findCourseAndValidateCommonRules(Long courseId, String statement) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Cannot add tasks to a course that is not in BUILDING status.");
        }

        if (taskRepository.existsByCourseAndStatement(course, statement)) {
            throw new IllegalArgumentException("A task with the same statement already exists in this course.");
        }

        return course;
    }

    @Transactional
    public Task createSingleChoiceTask(NewSingleChoiceTaskRequest dto) {

        Course course = findCourseAndValidateCommonRules(dto.courseId(), dto.statement());

        int newOrder = dto.order();
        int totalTasks = course.getTasks().size();
        if (newOrder > totalTasks + 1) {
            throw new IllegalArgumentException("Invalid task order. The order must be sequential. Next available order is " + (totalTasks + 1));
        }
        if (newOrder <= totalTasks) {
            List<Task> tasksToShift = taskRepository.findByCourseAndOrderGreaterThanEqualOrderByOrder(course, newOrder);
            tasksToShift.forEach(task -> task.setOrder(task.getOrder() + 1));
            taskRepository.saveAll(tasksToShift);
        }

        long correctOptionsCount = dto.options().stream().filter(OptionRequest::isCorrect).count();
        if (correctOptionsCount != 1) {
            throw new IllegalArgumentException("Single choice task must have exactly one correct option.");
        }


        Set<String> distinctOptions = dto.options().stream()
                .map(OptionRequest::option)
                .collect(Collectors.toSet());
        if (distinctOptions.size() < dto.options().size()) {
            throw new IllegalArgumentException("Options cannot have duplicate texts.");
        }


        if (distinctOptions.contains(dto.statement())) {
            throw new IllegalArgumentException("Options cannot be the same as the statement.");
        }

        SingleChoiceTask newTask = new SingleChoiceTask();
        newTask.setCourse(course);
        newTask.setStatement(dto.statement());
        newTask.setOrder(newOrder);

        List<Option> options = dto.options().stream().map(optDto -> {
            Option option = new Option(optDto.option(), optDto.isCorrect(), newTask);
            return option;
        }).toList();

        newTask.setOptions(options);

        return taskRepository.save(newTask); // Graças ao Cascade, as opções também serão salvas.
    }

    @Transactional
    public Task createMultipleChoiceTask(NewMultipleChoiceTaskRequest dto) {

        Course course = findCourseAndValidateCommonRules(dto.courseId(), dto.statement());

        int newOrder = dto.order();
        int totalTasks = course.getTasks().size();
        if (newOrder > totalTasks + 1) {
            throw new IllegalArgumentException("Invalid task order. The order must be sequential. Next available order is " + (totalTasks + 1));
        }
        if (newOrder <= totalTasks) {
            List<Task> tasksToShift = taskRepository.findByCourseAndOrderGreaterThanEqualOrderByOrder(course, newOrder);
            tasksToShift.forEach(task -> task.setOrder(task.getOrder() + 1));
            taskRepository.saveAll(tasksToShift);
        }

        long correctOptionsCount = dto.options().stream().filter(OptionRequest::isCorrect).count();
        long incorrectOptionsCount = dto.options().size() - correctOptionsCount;

        if (correctOptionsCount < 2 || incorrectOptionsCount < 1) {
            throw new IllegalArgumentException("Multiple choice task must have at least two correct options and at least one incorrect option.");
        }

        Set<String> distinctOptions = dto.options().stream()
                .map(OptionRequest::option)
                .collect(Collectors.toSet());
        if (distinctOptions.size() < dto.options().size()) {
            throw new IllegalArgumentException("Options cannot have duplicate texts.");
        }
        if (distinctOptions.contains(dto.statement())) {
            throw new IllegalArgumentException("Options cannot be the same as the statement.");
        }

        MultipleChoiceTask newTask = new MultipleChoiceTask();
        newTask.setCourse(course);
        newTask.setStatement(dto.statement());
        newTask.setOrder(newOrder);

        List<Option> options = dto.options().stream().map(optDto ->
                new Option(optDto.option(), optDto.isCorrect(), newTask)
        ).toList();

        newTask.setOptions(options);

        return taskRepository.save(newTask);
    }
}