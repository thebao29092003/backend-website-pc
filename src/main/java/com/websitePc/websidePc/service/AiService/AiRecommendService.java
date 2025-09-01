package com.websitePc.websidePc.service.AiService;

import com.websitePc.websidePc.dto.AiDto.ProductAiOutput;
import com.websitePc.websidePc.service.ToolForAi.AiRecommendTool;
import com.websitePc.websidePc.dto.AiDto.ChatRecommend;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiRecommendService {
    private final ChatClient chatClient;
    private final AiRecommendTool aiRecommendTool;

//    mình không cần chat memory làm chi cả vì cái này Ai nó đề xuất sản phẩm dựa
//    vào 2 tool mình đã cũng cấp rồi ko cần lưu lịch sử chat làm giảm hiệu xuất khi phải hồi
    public AiRecommendService(ChatClient.Builder builder,
                              AiRecommendTool aiRecommendTool
    ) {

        this.aiRecommendTool = aiRecommendTool;
        this.chatClient = builder.build();
    }

    public List<ProductAiOutput> chatRecommend(ChatRecommend chatRecommend) {
        Prompt prompt = getPrompt(chatRecommend);

        
        return chatClient
                .prompt(prompt)
                .tools(aiRecommendTool)
                .call()
                .entity(new ParameterizedTypeReference<List<ProductAiOutput>>() {});
    }

    private Prompt getPrompt(ChatRecommend chatRecommend) {
        SystemMessage systemMessage = new SystemMessage("""
          You are an expert AI assistant for a PC shop, specializing in personalized product recommendations. Generate exactly 5 tailored product suggestions based on user purchase history and viewed products, including view counts. Base recommendations on tool data, structuring output with Recommended Products (prioritize in-stock products).
          """);

        UserMessage userMessage = new UserMessage("userId: "+ chatRecommend.userId());
        return new Prompt(systemMessage, userMessage);
    }
}
