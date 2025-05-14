package com.example.knowledgeapplication.service.impl;

import com.example.knowledgeapplication.dto.DocumentDTO;
import com.example.knowledgeapplication.entity.Document;
import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.exception.BusinessException;
import com.example.knowledgeapplication.exception.ErrorCode;
import com.example.knowledgeapplication.repository.DocumentRepository;
import com.example.knowledgeapplication.service.DocumentService;
import io.minio.*;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public DocumentDTO uploadDocument(MultipartFile file, User user) {
        try {
            // 1. 生成存储文件名
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String storedFilename = UUID.randomUUID().toString() + "." + fileType;

            // 2. 上传文件到 MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storedFilename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 3. 保存文档信息到数据库
            Document document = new Document();
            document.setTitle(originalFilename);
            document.setOriginalFilename(originalFilename);
            document.setStoredFilename(storedFilename);
            document.setFileType(fileType);
            document.setFileSize(file.getSize());
            document.setMimeType(file.getContentType());
            document.setUser(user);

            Document savedDocument = documentRepository.save(document);

            // 4. 返回文档DTO
            return convertToDTO(savedDocument);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.DOCUMENT_UPLOAD_FAILED.getMessage());
        }
    }

    @Override
    public DocumentDTO getDocument(Long id, User user) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND.getMessage()));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED.getMessage());
        }

        return convertToDTO(document);
    }

    @Override
    public Page<DocumentDTO> getUserDocuments(User user, Pageable pageable) {
        return documentRepository.findByUser(user, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<DocumentDTO> searchDocuments(User user, String keyword, Pageable pageable) {
        return documentRepository.searchByUserAndKeyword(user, keyword, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id, User user) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND.getMessage()));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED.getMessage());
        }

        try {
            // 1. 从 MinIO 删除文件
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(document.getStoredFilename())
                    .build());

            // 2. 从数据库删除记录
            documentRepository.delete(document);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.DOCUMENT_DELETE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional
    public DocumentDTO updateDocument(Long id, DocumentDTO documentDTO, User user) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND.getMessage()));

        if (!document.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED.getMessage());
        }

        document.setTitle(documentDTO.getTitle());
        document.setContent(documentDTO.getContent());

        Document updatedDocument = documentRepository.save(document);
        return convertToDTO(updatedDocument);
    }

    private DocumentDTO convertToDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setOriginalFilename(document.getOriginalFilename());
        dto.setFileType(document.getFileType());
        dto.setFileSize(document.getFileSize());
        dto.setMimeType(document.getMimeType());
        dto.setContent(document.getContent());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setUsername(document.getUser().getUsername());
        return dto;
    }
} 