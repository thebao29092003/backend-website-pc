package com.websitePc.websidePc.repository;

import com.websitePc.websidePc.model.Img;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgRepository extends JpaRepository<Img, Long> {
    //    phục vụ cho việc thêm sản phẩm
    @Modifying
    @Query(value = """
            INSERT INTO img (img_link, product_id)
            VALUES (:imgLink, :productId)
            """, nativeQuery = true)
    void insertImg(
            String imgLink,
            Long productId
    );
}
