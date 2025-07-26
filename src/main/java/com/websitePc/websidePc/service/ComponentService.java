package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.model.UserProduct;
import com.websitePc.websidePc.repository.ComponentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ComponentService {
    private final ComponentRepository componentRepository;

    public ComponentService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    @Transactional
    public void addComponent(JsonNode componentData) {
        //  Chuyển sang String
        int componentInStock = componentData.get("componentInStock").asInt();
        BigDecimal componentPrice = new BigDecimal(componentData.get("componentPrice").asText());
        String componentName = componentData.get("componentName").asText();
        String componentType = componentData.get("componentType").asText();

        componentRepository.insertComponent(componentInStock, componentPrice, componentName, componentType);
    }

    public void deleteComponentById(Long componentId) {
        componentRepository.deleteComponentById(componentId);
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
