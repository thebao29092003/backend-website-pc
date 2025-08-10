package com.websitePc.websidePc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//Class RedisConfig được đánh dấu bằng annotation @Configuration,
// cho biết đây là một class cấu hình trong Spring, nơi bạn định nghĩa
// các bean để quản lý Redis (cơ sở dữ liệu NoSQL in-memory).
//"In-memory" là gì?: là lưu data trong RAM để đạt tốc độ cao.
@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
//        new LettuceConnectionFactory(): Tạo một kết nối mặc định tới Redis
//        tại localhost:6379 (port mặc định) mà không cần cấu hình thêm nếu Redis chạy local.
        return new LettuceConnectionFactory();
    }

//   Phương thức này tạo và trả về một bean RedisTemplate,
//   là lớp trung tâm để thực hiện các thao tác Redis (SET, GET, DEL, v.v.)
//   với dữ liệu dạng key-value.
//    RedisTemplate<String, Object> cho phép sử dụng key là String và
//    value là bất kỳ đối tượng nào
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory()): Liên kết RedisTemplate với RedisConnectionFactory
//        đã định nghĩa để sử dụng kết nối Redis.
        template.setConnectionFactory(redisConnectionFactory());


//        Thiết lập serializer cho các key trong Redis
//        StringRedisSerializer chuyển đổi key thành chuỗi String
//        Điều này đảm bảo key luôn được lưu dưới dạng String để dễ dàng tìm kiếm và quản lý
        template.setKeySerializer(new StringRedisSerializer());

//        Thiết lập serializer cho các value trong Redis
//        GenericJackson2JsonRedisSerializer chuyển đổi các object Java thành JSON khi lưu vào Redis
//        Khi đọc dữ liệu từ Redis, nó sẽ tự động chuyển đổi JSON trở lại thành object Java
//        Việc sử dụng JSON giúp lưu trữ các đối tượng phức tạp một cách linh hoạt và có thể đọc được
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

//        template.setHashKeySerializer(new StringRedisSerializer()): Serialize key của hash
//        (nếu dùng cấu trúc hash) thành String.
        template.setHashKeySerializer(new StringRedisSerializer());

//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()): Serialize value của hash thành JSON.
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

//        template.afterPropertiesSet(): Khởi tạo và xác nhận các thuộc tính của RedisTemplate sau khi cấu hình,
//        đảm bảo mọi thứ sẵn sàng.
        template.afterPropertiesSet();
        return template;
    }
}
