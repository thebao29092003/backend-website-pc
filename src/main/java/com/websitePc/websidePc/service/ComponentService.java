package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.repository.ComponentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentService {
    private final ComponentRepository componentRepository;

    public ComponentService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @Transactional
    public void addComponent(JsonNode componentData) {
        String componentName = componentData.get("componentName").asText();
        String componentType = componentData.get("componentType").asText();

        componentRepository.insertComponent(componentName, componentType);
    }

    public void deleteComponentById(Long componentId) {
        componentRepository.deleteComponentById(componentId);
    }

    public List<Object[]> listComponentByType(String type) {
        return componentRepository.listComponentByType(type);
    }

    public Page<Object[]> listComponentByName(int page, int size, String name) {
        return componentRepository.listComponentByName(PageRequest.of(page, size), name);
    }

    public Page<Object[]> listComponentForAdmin(int page, int size) {
        return componentRepository.listComponentForAdmin(PageRequest.of(page, size));
    }

    public List<Object> getComponentByProductId(Long productId) {
        return componentRepository.findComponentByProductId(productId);
    }
}
