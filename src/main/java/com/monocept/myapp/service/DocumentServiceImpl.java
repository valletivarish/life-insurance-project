package com.monocept.myapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.DocumentResponseDto;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.exception.GuardianLifeAssuranceException.ResourceNotFoundException;
import com.monocept.myapp.repository.DocumentRepository;
import com.monocept.myapp.util.PagedResponse;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public PagedResponse<DocumentResponseDto> getAllDocuments(int page, int size, String sortBy, String direction, Boolean verified) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Document> documentsPage;
        
        if (verified != null) {
            documentsPage = documentRepository.findAllByVerified(verified, pageRequest);
        } else {
            documentsPage = documentRepository.findAll(pageRequest);  
        }

        List<DocumentResponseDto> documents = documentsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(documents, documentsPage.getNumber(), documentsPage.getSize(),
                documentsPage.getTotalElements(), documentsPage.getTotalPages(), documentsPage.isLast());
    }

    private DocumentResponseDto convertToDto(Document document) {
        DocumentResponseDto dto = new DocumentResponseDto();
        dto.setDocumentId(document.getDocumentId());
        dto.setDocumentName(document.getDocumentName());
        dto.setVerified(document.isVerified());

        dto.setCustomerName(document.getCustomer() != null ? document.getCustomer().getFirstName()+" "+document.getCustomer().getLastName() : "N/A");
        dto.setVerifiedBy(document.getVerifyBy() != null ? document.getVerifyBy().getFirstName()+" "+document.getVerifyBy().getLastName() : null);

        return dto;
    }

    @Override
    public Document getDocumentById(int documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }
}
