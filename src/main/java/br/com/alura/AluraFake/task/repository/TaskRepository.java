package br.com.alura.AluraFake.task.repository;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByCourseAndStatement(Course course, String statement);

    List<Task> findByCourseAndOrderGreaterThanEqualOrderByOrder(Course course, int order);
}