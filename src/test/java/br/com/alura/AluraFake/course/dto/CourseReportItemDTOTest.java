package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.dto.CourseReportItemDTO;
import br.com.alura.AluraFake.task.domain.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseReportItemDTOTest {

    @Test
    void constructor__should_map_all_fields_from_course_entity() {
        Course course = mock(Course.class);
        List<Task> taskList = mock(List.class);

        LocalDateTime now = LocalDateTime.now();


        when(course.getId()).thenReturn(1L);
        when(course.getTitle()).thenReturn("API com Spring Boot");
        when(course.getStatus()).thenReturn(Status.PUBLISHED);
        when(course.getPublishedAt()).thenReturn(now);
        when(course.getTasks()).thenReturn(taskList);
        when(taskList.size()).thenReturn(15);

        CourseReportItemDTO dto = new CourseReportItemDTO(course);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.title()).isEqualTo("API com Spring Boot");
        assertThat(dto.status()).isEqualTo(Status.PUBLISHED);
        assertThat(dto.publishedAt()).isEqualTo(now);
        assertThat(dto.activityCount()).isEqualTo(15);
    }
}