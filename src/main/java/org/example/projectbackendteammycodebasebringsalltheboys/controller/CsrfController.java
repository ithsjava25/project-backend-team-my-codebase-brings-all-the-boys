package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

  @GetMapping("/api/csrf-token")
  public ResponseEntity<Void> csrfToken() {
    return ResponseEntity.noContent().build();
  }
}
