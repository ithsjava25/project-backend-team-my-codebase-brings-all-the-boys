package org.example.projectbackendteammycodebasebringsalltheboys.dto.casefile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaseRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
}
