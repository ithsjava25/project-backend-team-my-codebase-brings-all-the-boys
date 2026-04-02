package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Entity
@SoftDelete(columnName = "deleted")
@Table(name = "file_metadata")
@Getter
@Setter
@NoArgsConstructor
public class FileMetadata extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String s3Key;

  @Column(nullable = false)
  private String fileName;

  private Long fileSize;

  private String contentType;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "assignment_id")
  private Assignment assignment;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "submission_id")
  private Submission submission;

  @AssertTrue(message = "File metadata must be attached to exactly one parent")
  private boolean hasExactlyOneParent() {
    int count = 0;
    if (assignment != null) count++;
    if (comment != null) count++;
    if (submission != null) count++;
    return count == 1;
  }

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "uploader_id", nullable = false)
  private User uploader;
}
