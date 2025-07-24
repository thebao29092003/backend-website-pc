package com.websitePc.websidePc.service;

import com.websitePc.websidePc.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    public Page<Object[]> getOrderByUserId(String userId, String sortDirection, String sortField, int page, int size) {
        // Tạo Sort object với hướng sắp xếp động
        // hướng sắp xếp và trường sắp xếp
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ordersRepository.getOrderByUserId(userId, pageable);
    }

    public Page<Object[]> getListOrder(String sortDirection, String sortField, int page, int size) {
        // Tạo Sort object với hướng sắp xếp động
        // hướng sắp xếp và trường sắp xếp
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return ordersRepository.listOrder(pageable);
    }
}
