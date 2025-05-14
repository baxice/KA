package com.example.knowledgeapplication.repository;

import com.example.knowledgeapplication.entity.Document;
import com.example.knowledgeapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findByUser(User user, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.user = :user AND " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Document> searchByUserAndKeyword(@Param("user") User user, 
                                        @Param("keyword") String keyword, 
                                        Pageable pageable);
}
