package br.com.alura.AluraFake.course.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.task.domain.Task;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Course must be in BUILDING status to be published.");
        }

        List<Task> tasks = course.getTasks();
        if (tasks.isEmpty()) {
            throw new IllegalStateException("Course must have at least one task to be published.");
        }

        int totalTasks = tasks.size();
        Set<Integer> orders = tasks.stream()
                .map(Task::getOrder)
                .collect(Collectors.toSet());

        for (int i = 1; i <= totalTasks; i++) {
            if (!orders.contains(i)) {
                throw new IllegalStateException("Task sequence is not continuous. Missing order: " + i);
            }
        }

        Set<Type> foundTypes = tasks.stream()
                .map(Task::getType)
                .collect(Collectors.toSet());

        Set<Type> allTypes = EnumSet.allOf(Type.class);

        if (!foundTypes.containsAll(allTypes)) {
            throw new IllegalStateException("Course must contain at least one task of each type to be published.");
        }

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());

        courseRepository.save(course);
    }
}