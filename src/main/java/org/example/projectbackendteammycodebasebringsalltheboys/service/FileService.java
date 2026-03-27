package org.example.projectbackendteammycodebasebringsalltheboys.service;

import lombok.RequiredArgsConstructor;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Assignment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.Comment;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.FileMetadata;
import org.example.projectbackendteammycodebasebringsalltheboys.entity.User;
import org.example.projectbackendteammycodebasebringsalltheboys.repository.FileMetadataRepository;
import org.example.projectbackendteammycodebasebringsalltheboys.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final StorageService storageService;
    private final ActivityLogService activityLogService;

    @Transactional
    public FileMetadata uploadAssignmentFile(Assignment assignment, User uploader, String fileName,
                                             InputStream inputStream, long size, String contentType) {

        String s3Key = storageService.uploadFile(fileName, inputStream, size, contentType);

        FileMetadata metadata = new FileMetadata();
        metadata.setS3Key(s3Key);
        metadata.setFileName(fileName);
        metadata.setFileSize(size);
        metadata.setContentType(contentType);
        metadata.setAssignment(assignment);
        metadata.setUploader(uploader);

        FileMetadata saved = fileMetadataRepository.save(metadata);

        activityLogService.log(uploader, "UPLOADED_FILE", "Assignment", assignment.getId(),
                "Uploaded file: " + fileName);

        return saved;
    }

    @Transactional
    public FileMetadata uploadCommentFile(Comment comment, User uploader, String fileName,
                                          InputStream inputStream, long size, String contentType) {

        String s3Key = storageService.uploadFile(fileName, inputStream, size, contentType);

        FileMetadata metadata = new FileMetadata();
        metadata.setS3Key(s3Key);
        metadata.setFileName(fileName);
        metadata.setFileSize(size);
        metadata.setContentType(contentType);
        metadata.setComment(comment);
        metadata.setAssignment(comment.getAssignment());
        metadata.setUploader(uploader);

        FileMetadata saved = fileMetadataRepository.save(metadata);

        activityLogService.log(uploader, "UPLOADED_FILE", "Comment", comment.getId(),
                "Uploaded file: " + fileName + " for comment");

        return saved;
    }

    @Transactional(readOnly = true)
    public List<FileMetadata> getFilesByAssignment(Assignment assignment) {
        return fileMetadataRepository.findByAssignment(assignment);
    }

    public InputStream downloadFile(FileMetadata metadata) {
        return storageService.downloadFile(metadata.getS3Key());
    }
}
