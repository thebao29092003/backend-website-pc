package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.repository.ProductComponentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductComponentService {

    private final ProductComponentRepository productComponentRepository;

    @Transactional
    public void insertProductComponent(Long componentId, Long productId) {
        productComponentRepository.insertProductComponent(componentId, productId);
    }
}
