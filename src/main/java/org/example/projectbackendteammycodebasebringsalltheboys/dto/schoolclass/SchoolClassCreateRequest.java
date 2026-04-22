package org.example.projectbackendteammycodebasebringsalltheboys.dto.schoolclass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SchoolClassCreateRequest {
  @NotBlank(message = "Klassnamn får inte vara tomt")
  @Size(max = 50, message = "Klassnamn får vara högst 50 tecken")
  private String name;

  @Size(max = 255, message = "Beskrivning får vara högst 255 tecken")
  private String description;
}
