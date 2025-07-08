package com.websitePc.websidePc.service;

import com.websitePc.websidePc.dto.TokenPair;
import com.websitePc.websidePc.exception.ApplicationException;
import com.websitePc.websidePc.model.User;
import com.websitePc.websidePc.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {
    private final UserRepository userRepository;

//    lấy giá trị từ application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public TokenPair generateTokenPair(Authentication authentication) {
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);

        return new TokenPair(accessToken, refreshToken);
    }

//    generate access token
    public String generateAccessToken(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        System.out.println("userDetails" + userDetails + " is generating access token");

//        username chính là email của người dùng
        Map<String, String> claims = new HashMap<>();
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        claims.put("userId", user.get().getUserId());

    /*
    - userDetails.getAuthorities():
    Lấy danh sách các quyền (authorities) của người dùng
    Trả về một Collection các GrantedAuthority
    - .iterator():
    Tạo một Iterator để duyệt qua collection các quyền
    Iterator giúp truy cập tuần tự các phần tử trong collection
    - .next():
    Lấy phần tử đầu tiên từ Iterator
    Trả về đối tượng GrantedAuthority đầu tiên
    - .toString():
    Chuyển đối tượng GrantedAuthority thành chuỗi String
    Thường trả về dạng "ROLE_XXX" (ví dụ: "ROLE_ADMIN", "ROLE_USER")
    * */
        claims.put("role", userDetails.getAuthorities().isEmpty() ?
                "ROLE_USER" :
                userDetails.getAuthorities().iterator().next().toString());
        return generateToken(authentication, jwtExpirationMs, claims);
    }

    //    generate refresh token
    public String generateRefreshToken(Authentication authentication) {
//        Trong Java, Object là lớp cha của tất cả các lớp,
//        còn Objects là một lớp tiện ích (utility class)
//        trong gói java.util.Objects cung cấp các phương thức tĩnh
//        để thao tác với các đối tượng (ví dụ: kiểm tra null, so sánh, hash, v.v.).  Tóm lại:
//        Object: lớp gốc của mọi lớp trong Java (java.lang.Object)
//        Objects: lớp tiện ích chứa các phương thức hỗ trợ thao tác với đối tượng (java.util.Objects
        Map<String, String> claims = new HashMap<>();
        claims.put("tokenType", "refresh"); // add a claim to indicate this is a refresh token

        return generateToken(authentication, refreshTokenExpirationMs, claims);
    }

    private String generateToken(Authentication authentication,
                                 long expirationMs,
                                 Map<String, String> claims) {


//      authentication.getPrincipal() returns the authenticated user object
//      It's cast to UserDetails which contains user information like email, password, roles
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

//        Sử dụng {} để chèn tham số, tránh nối chuỗi (concatenation) làm giảm hiệu suất.
//        log.info: Ghi log cấp độ thông tin, phù hợp cho các sự kiện quan trọng như tạo token
//        ghi log thông tin người dùng để theo dõi quá trình tạo token

        Date now = new Date(); //time of token creation
        Date expiryDate = new Date(now.getTime() + expirationMs); //time of token expiration

//        getUsername do UserDetails nó có sẵn mình dùng username là email
//        from JJWT library to create JWT token
        return Jwts.builder()
//                adds extra claims (like token type)
                .subject(userPrincipal.getUsername())
//                adds claims is hashmap
                .claims(claims)
//                sets token creation time
                .issuedAt(now)
//                sets token expiration time
                .expiration(expiryDate)
//                sets the signing key using the secret key
                .signWith(getSigningKey())
                .compact();
    }

//    validate token
    public boolean validateTokenForUser(String token, UserDetails userDetails) {
//        extract email from the token
        final String email = extractEmailFromToken(token);
//        check if the email from the token matches the email from userDetails
//        is the authenticated user object when login
//        getUsername do UserDetails nó có sẵn mình dùng username là email
        return email!= null && email.equals(userDetails.getUsername());
    }

    public boolean isValidToken(String token) {
        return extractAllClaims(token) != null;
    }

    public String extractEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        if(claims != null) {
//            lấy được email vì ban đầu khi tạo token đã set subject là email
            return claims.getSubject();
        }
        return null;
    }

//    validate if the token is refresh token
    public boolean isRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null){
            return false;
        }
        return "refresh".equals(claims.get("tokenType"));
    }

    private Claims extractAllClaims(String token) {
        Claims claims = null;
//        ở đây mình dùng khối try thôi

        try {
            claims = Jwts.parser()
//                   Validates token signature
                    .verifyWith(getSigningKey())
//                   Creates parser instance
                    .build()
//                   Parses the token and extracts claims
                    .parseSignedClaims(token)
//                   Gets the payload (claims) from the parsed token
                    .getPayload();
        } catch (Exception e) {
            throw new ApplicationException("TOKEN_INVALID", e.getMessage());
        }

        return claims;
    }

    private SecretKey getSigningKey() {
//        giải mã chuỗi secret JWT từ base64
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
//        tạo khóa bí mật từ mảng byte đã giải mã
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
