package org.example.requestsystem.service;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.example.requestsystem.model.TicketFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final MinioClient minioClient;
    private final String bucket;

    public FileStorageService(MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    public StoredFile upload(Long ticketId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл пустой");
        }

        String originalFileName = cleanFileName(file.getOriginalFilename());
        String contentType = file.getContentType() == null ? DEFAULT_CONTENT_TYPE : file.getContentType();
        String objectKey = "tickets/" + ticketId + "/" + UUID.randomUUID() + "-" + originalFileName;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
            return new StoredFile(objectKey, originalFileName, contentType, file.getSize());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось сохранить файл в S3", ex);
        }
    }

    public StoredFileDownload download(TicketFile file) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(file.getObjectKey())
                            .build()
            );
            return new StoredFileDownload(
                    response,
                    file.getOriginalFileName(),
                    file.getContentType(),
                    file.getSizeBytes()
            );
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось прочитать файл из S3", ex);
        }
    }

    private String cleanFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "file";
        }
        String fileName = originalFileName.replace("\\", "/");
        int slashIndex = fileName.lastIndexOf('/');
        if (slashIndex >= 0) {
            fileName = fileName.substring(slashIndex + 1);
        }
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return fileName.isBlank() ? "file" : fileName;
    }
}
