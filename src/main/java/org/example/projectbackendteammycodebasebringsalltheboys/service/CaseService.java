package org.example.projectbackendteammycodebasebringsalltheboys.service;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final AssignmentRepository assignmentRepository;
    private final ActivityLogService activityLogService;

    @Transactional
    public Assignment createCase(String title, String description, User creator) {
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setCreator(creator);
        
        Assignment saved = assignmentRepository.save(assignment);
        
        activityLogService.log(creator, "CREATED_CASE", "Assignment", saved.getId(), "Case created: " + title);
        
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Assignment> getAllCases() {
        return assignmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Assignment> getCaseById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Assignment> getCasesByCreator(User creator) {
        return assignmentRepository.findByCreator(creator);
    }
}
