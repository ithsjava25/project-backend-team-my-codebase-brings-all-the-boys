package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.SchoolClass;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.SchoolClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchoolClassServiceTest {

  @Mock private SchoolClassRepository schoolClassRepository;

  private SchoolClassService schoolClassService;

  @BeforeEach
  void setUp() {
    schoolClassService = new SchoolClassService(schoolClassRepository);
  }

  @Test
  @DisplayName("createSchoolClass saves class if name is unique")
  void createSchoolClass_uniqueName_savesClass() {
    String name = "Class A";
    when(schoolClassRepository.findByName(name)).thenReturn(Optional.empty());
    when(schoolClassRepository.save(any(SchoolClass.class))).thenAnswer(inv -> inv.getArgument(0));

    SchoolClass result = schoolClassService.createSchoolClass(name, "Desc");

    assertThat(result.getName()).isEqualTo(name);
    verify(schoolClassRepository).save(any(SchoolClass.class));
  }

  @Test
  @DisplayName("createSchoolClass throws IllegalStateException if name exists")
  void createSchoolClass_duplicateName_throwsException() {
    String name = "Class A";
    when(schoolClassRepository.findByName(name)).thenReturn(Optional.of(new SchoolClass()));

    assertThatThrownBy(() -> schoolClassService.createSchoolClass(name, "Desc"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already exists");

    verify(schoolClassRepository, never()).save(any());
  }

  @Test
  @DisplayName("getSchoolClassById returns class from repository")
  void getSchoolClassById_delegatesToRepository() {
    UUID id = UUID.randomUUID();
    schoolClassService.getSchoolClassById(id);
    verify(schoolClassRepository).findById(id);
  }
}
