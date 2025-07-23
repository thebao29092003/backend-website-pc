package com.websitePc.websidePc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitePc.websidePc.dto.ChangePasswordRequest;
import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.model.UserProduct;
import com.websitePc.websidePc.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Object getUserTotalSpent(String userId) {
        return userRepository.getUserTotalSpent(userId);
    }

    public List<Object[]> getSpentPerMonth(String userId) {
        return userRepository.getSpentPerMonth(userId);
    }

    public Page<Object[]> getUserByName(String userName, int page, int size) {
        return userRepository.findUserByName(userName, PageRequest.of(page, size));
    }

    public Page<Object[]> listUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.listUser(pageable);
    }

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
//        lấy xác thực user hiện có
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//       lấy user detail
       UserDetails userDetails = (UserDetails) auth.getPrincipal();
//       tìm user bằng email
       Optional<User> user = userRepository.findByEmail(userDetails.getUsername());

        System.out.println("userDetails: "+userDetails);

//        nếu không tìm thấy user
        if(user.isEmpty()) {
            throw new ApplicationException("USER_NOT_FOUND", "USER_NOT_FOUND");
        }
//        check if the current password is correct
//        matches so sánh giữ mật khẩu plaintext và mật khẩu đã mã hóa trong database
        if(!passwordEncoder.matches(request.getCurrentPassword() , user.get().getPassword())){
            throw new ApplicationException("WRONG_PASSWORD", "WRONG_PASSWORD");
        }
//        check if thw two new passwords are the same
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new ApplicationException("PASSWORD_NOT_SAME", "PASSWORD_NOT_SAME");
        }
//        update the password
        user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
//        save the new password
        userRepository.save(user.get());

    }

    public void updateUser(JsonNode userDta) {
        // Cập nhật từng trường nếu có trong JSON

        String userId = userDta.get("userId").asText();
        String fullName = userDta.get("fullName").asText();
        String phone = userDta.get("phone").asText();

        userRepository.updateUser(userId, fullName, phone);
    }


    public Long hasUserBuyProduct(String userId, Long productId) {
        return userRepository.hasUserBuyProduct(userId, productId);
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

        System.out.println("cartItemData: " + cartItemData);
        //  Chuyển sang String
        Long productId = cartItemData.get("productId").asLong();
        String userId = cartItemData.get("userId").asText();

        userRepository.deleteCartItem(productId, userId);

    }
}
