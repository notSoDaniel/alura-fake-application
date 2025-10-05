package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve retornar apenas os cursos de um instrutor especifico")
    void findByInstructor__should_return_only_courses_from_a_specific_instructor() {
        User instructorA = new User("Paulo Silveira", "paulo@alura.com", Role.INSTRUCTOR);
        User instructorB = new User("Guilherme Silveira", "gui@alura.com", Role.INSTRUCTOR);
        userRepository.saveAll(List.of(instructorA, instructorB));

        Course courseJava = new Course("Java Completo", "Curso de Java", instructorA);
        Course courseSpring = new Course("Spring Boot", "Curso de Spring", instructorA);
        Course courseNode = new Course("NodeJS", "Curso de NodeJS", instructorB);
        courseRepository.saveAll(List.of(courseJava, courseSpring, courseNode));

        List<Course> coursesFromInstructorA = courseRepository.findByInstructor(instructorA);

        assertThat(coursesFromInstructorA).isNotNull();
        assertThat(coursesFromInstructorA).hasSize(2);
        assertThat(coursesFromInstructorA)
                .extracting(Course::getTitle)
                .containsExactlyInAnyOrder("Java Completo", "Spring Boot");
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia se o instrutor nao tiver cursos")
    void findByInstructor__should_return_empty_list_when_instructor_has_no_courses() {
        User instructorSemCursos = new User("Instrutor Novo", "novo@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructorSemCursos);

        List<Course> result = courseRepository.findByInstructor(instructorSemCursos);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}