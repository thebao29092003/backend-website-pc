package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.UserProduct;
import com.websitePc.websidePc.model.UserProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProductRepository extends JpaRepository<UserProduct, UserProductId> {
}
