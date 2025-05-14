package com.example.knowledgeapplication.service;

import com.example.knowledgeapplication.dto.DocumentDTO;
import com.example.knowledgeapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    DocumentDTO uploadDocument(MultipartFile file, User user);
    DocumentDTO getDocument(Long id, User user);
    Page<DocumentDTO> getUserDocuments(User user, Pageable pageable);
    Page<DocumentDTO> searchDocuments(User user, String keyword, Pageable pageable);
    void deleteDocument(Long id, User user);
    DocumentDTO updateDocument(Long id, DocumentDTO documentDTO, User user);
}
