package com.websitePc.websidePc.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

//    localDate: chỉ bao gồm ngày, không có giờ
    @CreationTimestamp
    private LocalDate createAt;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = true)
    private String OTP;

//    LocalDateTime: bao gồm cả ngày và giờ, thường dùng để lưu trữ thời gian chính xác hơn.
    @Column(nullable = true)
    private LocalDateTime OTP_create_at;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user", fetch = FetchType.EAGER)
    private Collection<Order> orders;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user", fetch = FetchType.EAGER)
    private Collection<Review> reviews;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user", fetch = FetchType.EAGER)
    private Collection<Cart> carts;

    public User(String email, String password,Role role,String fullName, String phone) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;

    }
}
