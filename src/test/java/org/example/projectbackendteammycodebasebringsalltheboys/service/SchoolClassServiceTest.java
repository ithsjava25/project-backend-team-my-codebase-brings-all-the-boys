package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass.SchoolClassCreateRequest;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchoolClassServiceTest {

  @Mock private SchoolClassRepository schoolClassRepository;
  @Mock private DtoMapper dtoMapper;
  @Mock private ClassEnrollmentService enrollmentService;

  private SchoolClassService schoolClassService;

  @BeforeEach
  void setUp() {
    schoolClassService =
        new SchoolClassService(schoolClassRepository, dtoMapper, enrollmentService);
  }

  @Test
  @DisplayName("createSchoolClass saves and returns new class")
  void createSchoolClass_savesClass() {
    SchoolClassCreateRequest request = new SchoolClassCreateRequest();
    request.setName("TE21A");
    request.setDescription("Tech class");

    SchoolClass sc = new SchoolClass();
    sc.setName("TE21A");
    sc.setDescription("Tech class");

    when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(sc);

    SchoolClass result = schoolClassService.createSchoolClass(request);

    ArgumentCaptor<SchoolClass> captor = ArgumentCaptor.forClass(SchoolClass.class);
    verify(schoolClassRepository).save(captor.capture());

    SchoolClass captured = captor.getValue();
    assertThat(captured.getName()).isEqualTo("TE21A");
    assertThat(captured.getDescription()).isEqualTo("Tech class");
    assertThat(result.getName()).isEqualTo("TE21A");
  }

  @Test
  @DisplayName("getSchoolClassById returns optional class")
  void getSchoolClassById_returnsOptional() {
    UUID id = UUID.randomUUID();
    SchoolClass sc = new SchoolClass();
    when(schoolClassRepository.findById(id)).thenReturn(Optional.of(sc));

    Optional<SchoolClass> result = schoolClassService.getSchoolClassById(id);

    assertThat(result).isPresent();
    verify(schoolClassRepository).findById(id);
  }
}
