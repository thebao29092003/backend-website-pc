package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.dto.AiDto.ChatRecommend;
import com.websitePc.websidePc.dto.AiDto.ChatRequest;
import com.websitePc.websidePc.service.AiService.AiRecommendService;
import com.websitePc.websidePc.service.AiService.AiReportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping ("/api/public")
public class AiController {
    private final AiReportService aiReportService;
    private final AiRecommendService aiRecommendService;


    public AiController(AiReportService aiReportService, AiRecommendService aiRecommendService) {
        this.aiReportService = aiReportService;
        this.aiRecommendService = aiRecommendService;
    }

    @PostMapping("/chatAi")
    public Flux<String> chatReport(@RequestBody ChatRequest chatRequest) {
        return aiReportService.chatReport(chatRequest);
    }

//    cho user đã đăng nhập còn nếu user chưa đăng nhập thì gọi api dùng sql bình thường
//    chả về sản phẩm mới nhất thôi
    @PostMapping("/chat-recommend")
    public Flux<String> chatRecommend(@RequestBody ChatRecommend chatRecommend) {
        return aiRecommendService.chatRecommend(chatRecommend);
    }
}
