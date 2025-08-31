package com.websitePc.websidePc.service.AiService;

import com.websitePc.websidePc.service.ToolForAi.AiRecommendTool;
import com.websitePc.websidePc.dto.AiDto.ChatRecommend;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiRecommendService {
    private final ChatClient chatClient;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final AiRecommendTool aiRecommendTool;

    public AiRecommendService(ChatClient.Builder builder,
                              JdbcChatMemoryRepository jdbcChatMemoryRepository,
                              AiRecommendTool aiRecommendTool
    ) {

        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
        this.aiRecommendTool = aiRecommendTool;
        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(20)
                .build();
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public Flux<String> chatRecommend(ChatRecommend chatRecommend) {
        SystemMessage systemMessage = new SystemMessage("""
               You are an expert AI assistant specializing in personalized product recommendations for a PC shop (selling computers, hardware, and accessories). Your primary role is to generate tailored product suggestions based on a user's purchase history and viewed products, including the number of times each product was viewed. Prioritize responding in Vietnamese for all communications. Use a professional, concise tone in all responses. Respond only to queries directly related to product recommendations or related PC shop operations. Ignore or politely decline any questions or topics unrelated to the PC shop, such as general conversations or personal advice. Always base your recommendations on data from available tools, structuring responses with sections like Summary, Recommended Products, and Insights if applicable. If the query is unclear or lacks user data, ask for clarification politely in Vietnamese.
               """);

        Prompt prompt = new Prompt(systemMessage);


        return chatClient
                .prompt(prompt)
                .tools(aiRecommendTool)
                .advisors(
                        advisorSpec ->
                                advisorSpec.param(ChatMemory.CONVERSATION_ID, chatRecommend.userId()))
                .stream()
                .content();
    }
}
