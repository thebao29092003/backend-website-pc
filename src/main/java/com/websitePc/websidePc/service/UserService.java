package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.model.UserProduct;
import com.websitePc.websidePc.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long hasUserBuyProduct(String email, Long productId) {
        return userRepository.hasUserBuyProduct(email, productId);
    }

    public List<Object[]> productByUserId(String userId) {
        return userRepository.productByUserId(userId);
    }

    @Transactional
    public void addCartItem(JsonNode cartItemData) {
        //  Chuyển sang String
        Long productId = cartItemData.get("productId").asLong();
        String userId = cartItemData.get("userId").asText();
        Integer quantity = cartItemData.get("quantity").asInt();

        UserProduct userProduct = userRepository.existUserProduct(productId, userId);

//        nếu cart item đó có rồi thì lấy số lượng của nó ban đầu + thêm quantity
//        được thêm vào
        if(userProduct != null){
            userProduct.setQuantity(userProduct.getQuantity() + quantity);
        } else{
            userRepository.insertCartItem(productId, userId, quantity);
        }
    }

    @Transactional
    public void deleteCartItem(JsonNode cartItemData) {
        //  Chuyển sang String
        Long productId = cartItemData.get("productId").asLong();
        String userId = cartItemData.get("userId").asText();

        userRepository.deleteCartItem(productId, userId);

    }
}
