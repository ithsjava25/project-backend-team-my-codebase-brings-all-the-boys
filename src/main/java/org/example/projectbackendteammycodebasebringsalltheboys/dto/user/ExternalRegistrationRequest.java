package org.example.projectbackendteammycodebasebringsalltheboys.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalRegistrationRequest extends RegistrationRequest {

  @NotBlank(message = "Confirm password is required")
  private String confirmPassword;
}
