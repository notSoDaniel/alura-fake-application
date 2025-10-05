package br.com.alura.AluraFake.course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseListItemDTOTest {

    @Test
    @DisplayName("Deve mapear corretamente uma entidade Course para o CourseListItemDTO")
    void constructor__should_map_fields_from_course_entity() {
        Course mockCourse = mock(Course.class);

        when(mockCourse.getId()).thenReturn(101L);
        when(mockCourse.getTitle()).thenReturn("Introdução a Docker");
        when(mockCourse.getDescription()).thenReturn("Aprenda containers.");
        when(mockCourse.getStatus()).thenReturn(Status.BUILDING);

        CourseListItemDTO dto = new CourseListItemDTO(mockCourse);

        assertThat(dto.getId()).isEqualTo(101L);
        assertThat(dto.getTitle()).isEqualTo("Introdução a Docker");
        assertThat(dto.getDescription()).isEqualTo("Aprenda containers.");
        assertThat(dto.getStatus()).isEqualTo(Status.BUILDING);
    }
}