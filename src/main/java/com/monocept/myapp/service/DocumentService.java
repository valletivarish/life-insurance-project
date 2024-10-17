package com.monocept.myapp.service;

import com.monocept.myapp.dto.DocumentRequestDto;
import com.monocept.myapp.dto.DocumentResponseDto;
import com.monocept.myapp.entity.Document;
import com.monocept.myapp.util.PagedResponse;

public interface DocumentService {

	PagedResponse<DocumentResponseDto> getAllDocuments(int page, int size, String sortBy, String direction, Boolean verified);

	Document getDocumentById(int documentId);

	PagedResponse<DocumentResponseDto> getAllDocuments(long customerId, int page, int size, String sortBy,
			String direction, Boolean verified);

	String updateDocument(DocumentRequestDto documentRequestDto);

}
