package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Entity
@SoftDelete(columnName = "deleted")
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment extends BaseEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "assignment_id", nullable = false)
  private Assignment assignment;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_assignment_id")
  private UserAssignment userAssignment;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;

  @Column(unique = true)
  private String seedKey;

  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FileMetadata> files = new ArrayList<>();
}
