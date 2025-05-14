package com.example.knowledgeapplication.controller;

import com.example.knowledgeapplication.dto.DocumentDTO;
import com.example.knowledgeapplication.entity.User;
import com.example.knowledgeapplication.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        DocumentDTO document = documentService.uploadDocument(file, user);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        DocumentDTO document = documentService.getDocument(id, user);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    public ResponseEntity<Page<DocumentDTO>> getUserDocuments(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<DocumentDTO> documents = documentService.getUserDocuments(user, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentDTO>> searchDocuments(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<DocumentDTO> documents = documentService.searchDocuments(user, keyword, pageable);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        documentService.deleteDocument(id, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentDTO documentDTO,
            @AuthenticationPrincipal User user) {
        DocumentDTO updatedDocument = documentService.updateDocument(id, documentDTO, user);
        return ResponseEntity.ok(updatedDocument);
    }
} 