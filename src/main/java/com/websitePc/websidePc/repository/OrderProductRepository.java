package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.OrderProduct;
import com.websitePc.websidePc.model.OrderProductId;
import com.websitePc.websidePc.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {

}
