package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.course.dto.CourseReportItemDTO;
import java.util.List;

public record InstructorReportDTO(
        List<CourseReportItemDTO> courses,
        long totalPublishedCourses
) {
}