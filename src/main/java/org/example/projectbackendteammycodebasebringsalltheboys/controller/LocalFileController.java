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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@RestController
@RequestMapping("/api/files/local")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileController {

  private final LocalStorageService storageService;

  @PutMapping("/{s3Key:.+}")
  public ResponseEntity<Void> uploadFile(
      @PathVariable String s3Key,
      @RequestHeader("Content-Type") String contentType, // available for future validation
      InputStream body) {

    storageService.saveWithKey(s3Key, body);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{s3Key:.+}")
  public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String s3Key) {
    StreamingResponseBody body = out -> {
      try (InputStream inputStream = storageService.downloadFile(s3Key)) {
        inputStream.transferTo(out);
      }
    };
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(body);
  }
}
