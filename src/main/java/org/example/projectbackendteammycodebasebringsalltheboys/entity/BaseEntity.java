package org.example.projectbackendteammycodebasebringsalltheboys.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Version private Long version;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @LastModifiedBy private String updatedBy;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || org.hibernate.Hibernate.getClass(this) != org.hibernate.Hibernate.getClass(o))
      return false;
    BaseEntity that = (BaseEntity) o;
    return id != null && id.equals(that.getId());
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : System.identityHashCode(this);
  }
}
