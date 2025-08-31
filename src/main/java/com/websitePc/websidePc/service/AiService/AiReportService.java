package com.websitePc.websidePc.service.AiService;


import com.websitePc.websidePc.dto.AiDto.ChatRequest;
import com.websitePc.websidePc.service.ToolForAi.AiReportTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiReportService {
    private final ChatClient chatClient;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final AiReportTool aiReportTool;

    public AiReportService(ChatClient.Builder builder,
                           JdbcChatMemoryRepository jdbcChatMemoryRepository,
                           AiReportTool aiReportTool1
    ) {

        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;
        this.aiReportTool = aiReportTool1;
        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(20)
                .build();
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public Flux<String> chatReport(ChatRequest request) {
        SystemMessage systemMessage = new SystemMessage("""
               You are an AI assistant specializing in providing concise information for the admin of a PC shop (selling computers, hardware, and accessories). Your primary role is to deliver brief, accurate responses about store operations, such as sales, orders, inventory, or top-selling products, based on the admin's specific requests. Prioritize responding in Vietnamese for all communications. Use a professional, concise tone in all responses. Respond only to queries directly related to PC shop management. Ignore or politely decline any questions or topics unrelated to the PC shop, such as general conversations or personal advice. Always base your responses on provided data or available tools, structuring them clearly with sections like Summary, Key Information, and Recommendations if applicable. If the query is unclear, ask for clarification politely in Vietnamese.
                """);

        UserMessage userMessage = new UserMessage(request.message());

        Prompt prompt = new Prompt(systemMessage, userMessage);


        return chatClient
                .prompt(prompt)
                .tools(aiReportTool)
                .advisors(
                        advisorSpec ->
                                advisorSpec.param(ChatMemory.CONVERSATION_ID, request.userId()))
                .stream()
                .content();
    }
}
