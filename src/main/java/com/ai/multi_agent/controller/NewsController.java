package com.ai.multi_agent.controller;

import com.ai.multi_agent.agent.AiAgent;
import com.ai.multi_agent.dto.NewsAndSentimentals;
import com.ai.multi_agent.util.AgentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final OpenAiChatModel chatModel;
    private final BeanOutputConverter<List<NewsAndSentimentals>> outputConverter;

    public NewsController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        this.outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<NewsAndSentimentals>>() { });
    }

    @GetMapping("/short")
    public List<NewsAndSentimentals> getNewsFromInterest(@RequestHeader Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        UserMessage userMessage = new UserMessage(
                """
                Get summarize news by topic depending on userId: %s.
                Only result need to be provided
                Example:
                [
                 {
                    String title,
                    String opinion,
                    String url,
                    String source
                 }
                ]
                """
                        .formatted(userId)
        );
        OpenAiChatOptions aiChatOptions;
        aiChatOptions = AgentUtil.createFunctionOptions(
                // Order here doesn't matter
                // It's just something you let LLM know what function they need to call
                AiAgent.GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME,
                AiAgent.GET_USER_PREFERENCES_FUNCTION_NAME
        );
        ChatResponse response = this.chatModel.call(new Prompt(userMessage, aiChatOptions));
        logger.info("Response: {}", response);
        Generation generation = response.getResult();
        return this.outputConverter.convert(generation.getOutput().getText());
    }
}
