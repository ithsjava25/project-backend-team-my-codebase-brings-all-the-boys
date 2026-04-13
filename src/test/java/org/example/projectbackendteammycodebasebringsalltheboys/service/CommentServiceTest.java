package org.example.projectbackendteammycodebasebringsalltheboys.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock private CommentRepository commentRepository;

  private CommentService commentService;

  @BeforeEach
  void setUp() {
    commentService = new CommentService(commentRepository);
  }

  @Test
  @DisplayName("addComment saves comment when valid")
  void addComment_valid_savesComment() {
    Assignment assignment = new Assignment();
    User author = new User();
    String text = "Nice work!";
    when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

    Comment result = commentService.addComment(assignment, author, text);

    assertThat(result.getAssignment()).isEqualTo(assignment);
    assertThat(result.getAuthor()).isEqualTo(author);
    assertThat(result.getText()).isEqualTo(text);
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  @DisplayName("addComment throws NullPointerException if assignment is null")
  void addComment_nullAssignment_throwsException() {
    assertThatThrownBy(() -> commentService.addComment(null, new User(), "text"))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("addComment throws IllegalArgumentException if text is blank")
  void addComment_blankText_throwsException() {
    assertThatThrownBy(() -> commentService.addComment(new Assignment(), new User(), "  "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("getCommentById delegates to repository")
  void getCommentById_delegatesToRepository() {
    UUID id = UUID.randomUUID();
    commentService.getCommentById(id);
    verify(commentRepository).findById(id);
  }
}
