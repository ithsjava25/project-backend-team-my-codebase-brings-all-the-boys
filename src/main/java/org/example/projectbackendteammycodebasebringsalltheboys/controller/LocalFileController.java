package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.LocalStorageService;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files/local")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileController {

  private final StorageService storageService;

  @PutMapping("/{s3Key:.+}")
  public ResponseEntity<Void> uploadFile(
      @PathVariable String s3Key,
      @RequestHeader("Content-Type") String contentType, // available for future validation
      InputStream body) {

    ((LocalStorageService) storageService).saveWithKey(s3Key, body);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{s3Key:.+}")
  public ResponseEntity<byte[]> downloadFile(@PathVariable String s3Key) throws IOException {
    try (InputStream is = storageService.downloadFile(s3Key)) {
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(is.readAllBytes());
    }
  }
}
