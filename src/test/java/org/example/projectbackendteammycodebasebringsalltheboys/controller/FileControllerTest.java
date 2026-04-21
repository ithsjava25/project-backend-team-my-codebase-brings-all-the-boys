package org.example.projectbackendteammycodebasebringsalltheboys.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Role;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.mapper.DtoMapper;
import org.example.projectbackendteammycodebasebringsalltheboys.service.*;
import org.example.projectbackendteammycodebasebringsalltheboys.testConfig.TestViewConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileController.class)
@Import(TestViewConfig.class)
@ActiveProfiles("test")
class FileControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private FileService fileService;
    @MockitoBean private UserService userService;
    @MockitoBean private CaseService caseService;
    @MockitoBean private CommentService commentService;
    @MockitoBean private AuthorizationService authorizationService;
    @MockitoBean private DtoMapper dtoMapper;

    private UUID fileId;
    private FileMetadata fileMetadata;
    private User uploader;

    @BeforeEach
    void setUp() {
        fileId = UUID.randomUUID();

        Role role = new Role();
        role.setName("ROLE_STUDENT");

        uploader = new User();
        uploader.setId(UUID.randomUUID());
        uploader.setUsername("student");
        uploader.setRole(role);

        fileMetadata = new FileMetadata();
        fileMetadata.setId(fileId);
        fileMetadata.setFileName("test.png");
        fileMetadata.setContentType("image/png");
        fileMetadata.setFileSize(1024L);
        fileMetadata.setS3Key("some-key_test.png");
        fileMetadata.setUploader(uploader);
    }

    @Test
    @DisplayName("GET /api/files/{id}/download returns 200 for uploader")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void downloadFile_asUploader_returns200() throws Exception {
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(uploader));
        when(fileService.getFileById(fileId)).thenReturn(Optional.of(fileMetadata));
        when(fileService.downloadFile(fileMetadata))
                .thenReturn(new ByteArrayInputStream("file content".getBytes()));

        mockMvc
                .perform(get("/api/files/" + fileId + "/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.png\""))
                .andExpect(content().contentType("image/png"));
    }

    @Test
    @DisplayName("GET /api/files/{id}/download returns 404 when file not found")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void downloadFile_fileNotFound_returns404() throws Exception {
        when(userService.getUserByUsername("student")).thenReturn(Optional.of(uploader));
        when(fileService.getFileById(any())).thenReturn(Optional.empty());

        mockMvc
                .perform(get("/api/files/" + fileId + "/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/files/{id}/download returns 403 when user is not uploader or teacher/admin")
    @WithMockUser(username = "other", roles = {"STUDENT"})
    void downloadFile_asOtherStudent_returns403() throws Exception {
        Role role = new Role();
        role.setName("ROLE_STUDENT");

        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("other");
        otherUser.setRole(role);

        when(userService.getUserByUsername("other")).thenReturn(Optional.of(otherUser));
        when(fileService.getFileById(fileId)).thenReturn(Optional.of(fileMetadata));

        mockMvc
                .perform(get("/api/files/" + fileId + "/download"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/files/{id}/download returns 200 for teacher")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void downloadFile_asTeacher_returns200() throws Exception {
        Role role = new Role();
        role.setName("ROLE_TEACHER");

        User teacher = new User();
        teacher.setId(UUID.randomUUID());
        teacher.setUsername("teacher");
        teacher.setRole(role);

        when(userService.getUserByUsername("teacher")).thenReturn(Optional.of(teacher));
        when(fileService.getFileById(fileId)).thenReturn(Optional.of(fileMetadata));
        when(fileService.downloadFile(fileMetadata))
                .thenReturn(new ByteArrayInputStream("file content".getBytes()));

        mockMvc
                .perform(get("/api/files/" + fileId + "/download"))
                .andExpect(status().isOk());
    }
}