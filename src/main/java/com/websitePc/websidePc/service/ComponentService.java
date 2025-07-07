package com.websitePc.websidePc.service;

import com.websitePc.websidePc.repository.ComponentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentService {
    private final ComponentRepository componentRepository;

    public ComponentService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public List<Object> getComponentByProductId(Long productId) {
        return componentRepository.findComponentByProductId(productId);
    }
}
