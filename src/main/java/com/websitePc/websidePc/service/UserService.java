package com.websitePc.websidePc.service;

import com.websitePc.websidePc.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long hasUserBuyProduct(String email, Long productId) {
        return userRepository.hasUserBuyProduct(email, productId);
    }
}
