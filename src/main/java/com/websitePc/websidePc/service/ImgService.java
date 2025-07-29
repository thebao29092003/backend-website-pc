package com.websitePc.websidePc.service;

import com.websitePc.websidePc.repository.ImgRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImgService {
    private final ImgRepository imgRepository;
    @Transactional
    public void insertImg(String imgLink, Long productId) {
        imgRepository.insertImg(imgLink, productId);
    }
}
