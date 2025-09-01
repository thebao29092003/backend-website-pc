package com.websitePc.websidePc.controller;

import com.websitePc.websidePc.dto.AiDto.*;
import com.websitePc.websidePc.service.AiService.AiRecommendService;
import com.websitePc.websidePc.service.AiService.AiReportService;
import com.websitePc.websidePc.service.ToolForAi.AiRecommendTool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping ("/api/public")
public class AiController {
    private final AiReportService aiReportService;
    private final AiRecommendService aiRecommendService;
    private final AiRecommendTool aiRecommendTool;


    public AiController(AiReportService aiReportService, AiRecommendService aiRecommendService, AiRecommendTool aiRecommendTool) {
        this.aiReportService = aiReportService;
        this.aiRecommendService = aiRecommendService;
        this.aiRecommendTool = aiRecommendTool;
    }

//    này là api trợ giúp admin quản lí cửa hàng, thế nên là khi mà chuyển sang web
//    bán game mik có thể thêm tool lấy xu hướng game hiện tại chẳng hạn (tìm trên IGDB xem có ko)
//    để nó có thể trợ giúp user đưa ra game xu hướng cần nhập bán chẳng hạn
    @PostMapping("/chatAi")
    public Flux<String> chatReport(@RequestBody ChatRequest chatRequest) {
        return aiReportService.chatReport(chatRequest);
    }
//    vẫn chả về sản phẩm mới nhất như thường nhưng mà khi user đăng nhập thì sẽ có thêm mục
//    dành riêng cho bạn
//    sau này gọi api này thì phản hồi mình phải cache nữa tiếng, 1 tiếng gì đó
//    tài vì này dùng Ai nên phải hồi khá lâu so với truy xuất database thông thường
    @PostMapping("/chat-recommend")
    public List<ProductAiOutput> chatRecommend(@RequestBody ChatRecommend chatRecommend) {
        return aiRecommendService.chatRecommend(chatRecommend);
    }
}
