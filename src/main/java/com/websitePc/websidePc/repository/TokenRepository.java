package com.websitePc.websidePc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

// cái repository này sẽ lưu trữ access token và refresh token và blacklist của chúng
// nó sẽ sử dụng Redis để lưu trữ các token này, nếu hết hạn thì sẽ tự động xóa trong redis
// những token nào trong blacklist thì sẽ không được sử dụng nữa phục vụ cho việc logout
// và xóa access token cũ khi refresh token tạo ra một access token mới

@Repository
public class TokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;

//    key prefixes for token storage
    private static final String ACCESS_TOKEN_KEY_PREFIX = "user:access:";
    private static final String REFRESH_TOKEN_KEY_PREFIX = "user:refresh:";

//    key prefixes for token blacklisting
    private static final String ACCESS_BLACKLIST_PREFIX = "blacklist:access:";
    private static final String REFRESH_BLACKLIST_PREFIX = "blacklist:refresh:";

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration}")
    private long  refreshTokenExpiration;

    public TokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeTokens(String username,
                            String accessToken,
                            String refreshToken) {

//        Store access token in Redis with expiration
        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        storeToken(accessKey, accessToken, jwtExpiration);

//        store refresh token in Redis with expiration
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + username;
        storeToken(refreshKey, refreshToken, refreshTokenExpiration);
    }

    private void storeToken (String key, String token, long expiration){
//        Uses Redis' String operations to store the token value
//        Associates the token with the provided key
        redisTemplate.opsForValue().set(key, token);

//        Sets an expiration time on the key
//        After the expiration time passes, Redis will automatically delete the key-value pair
//        The expiration is set in milliseconds (using TimeUnit.MILLISECONDS)
        redisTemplate.expire(key, expiration, TimeUnit.MILLISECONDS);
    }

//    retrieve access token from Redis
    public String getAccessToken(String username) {
        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        return getToken(accessKey);
    }

    //    retrieve access token from Redis
    public String getRefreshToken(String username) {
        String accessKey = REFRESH_TOKEN_KEY_PREFIX + username;
        return getToken(accessKey);
    }

    private String getToken(String accessKey) {
//        Uses Redis' String operations to retrieve the token value
        Object token = redisTemplate.opsForValue().get(accessKey);
//        If the token is not found, it returns null

        return token != null ? token.toString() : null;
    }

//    remove all tokens for a user (complete logout)
    public void removeAllTokens(String username) {
        String accessToken = getAccessToken(username);
        String refreshToken = getRefreshToken(username);

        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        String refreshKey = REFRESH_TOKEN_KEY_PREFIX + username;

//        redisTemplate.delete(accessKey);: Xóa access token của user (key dạng user:access:username).
//        redisTemplate.delete(refreshKey);: Xóa refresh token của user (key dạng user:refresh:username).
//        Sau khi xóa, các token này không còn tồn tại trong Redis, đồng nghĩa với việc user sẽ phải đăng nhập lại để lấy token mới.
        redisTemplate.delete(accessKey);
        redisTemplate.delete(refreshKey);

        if(accessToken != null) {
//            thêm vào blacklist để không sử dụng được access token cũ nữa
            String accessBlackListKey = ACCESS_BLACKLIST_PREFIX + accessToken;
            blacklistToken(accessBlackListKey, jwtExpiration);
        }
        if(refreshToken != null) {
//            them vào blacklist để không sử dụng được refresh token cũ nữa
            String refreshBlackListKey = REFRESH_BLACKLIST_PREFIX + refreshToken;
            blacklistToken(refreshBlackListKey, refreshTokenExpiration);
        }
    }

//    kiểm tra xem access token có bị blacklist hay không
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String blacklistKey = ACCESS_BLACKLIST_PREFIX + accessToken;
        return redisTemplate.hasKey(blacklistKey);
    }

//   kiểm tra xem refresh token có bị blacklist hay không
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        String blacklistKey = REFRESH_BLACKLIST_PREFIX + refreshToken;
        return redisTemplate.hasKey(blacklistKey);
    }

    private void blacklistToken(String blacklistKey, long expiration) {
//        Uses Redis' String operations to store the token value
//        Associates the token with the provided key
        redisTemplate.opsForValue().set(blacklistKey, "blacklisted");

//        Sets an expiration time on the key
//        After the expiration time passes, Redis will automatically delete the key-value pair
//        The expiration is set in milliseconds (using TimeUnit.MILLISECONDS)
        redisTemplate.expire(blacklistKey, expiration, TimeUnit.MILLISECONDS);
    }

//    phục vụ cho việc xóa access token cũ khi refresh token tạo ra 1 new access token
    public void removeAccessToken(String username) {
        String accessToken = getAccessToken(username);
        String accessKey = ACCESS_TOKEN_KEY_PREFIX + username;
        redisTemplate.delete(accessKey);

//        blacklist the access token
        String accessBlackListKey = ACCESS_BLACKLIST_PREFIX + accessToken;
        blacklistToken(accessBlackListKey, jwtExpiration);
    }
}
