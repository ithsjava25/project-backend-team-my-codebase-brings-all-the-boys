package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
  List<FileMetadata> findByAssignment(Assignment assignment);

  List<FileMetadata> findByComment(Comment comment);

  List<FileMetadata> findBySubmission(
      org.example.projectbackendteammycodebasebringsalltheboys.entity.Submission submission);

  List<FileMetadata> findByUploader(User uploader);

  Optional<FileMetadata> findByS3Key(String s3Key);
}
