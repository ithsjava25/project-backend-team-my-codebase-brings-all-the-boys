package org.example.projectbackendteammycodebasebringsalltheboys.repository;

import java.util.List;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Submission;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.UserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
  List<Submission> findByUserAssignment(UserAssignment userAssignment);

  void deleteByUserAssignment_Student_Id(UUID studentId);
}
