package com.ai.multi_agent.service;

import com.ai.multi_agent.client.AlphaClientNewsSentimentals;
import com.ai.multi_agent.dto.GetNewsByPreferenceResponse;
import com.ai.multi_agent.dto.GetNewsRequest;
import com.ai.multi_agent.dto.NewsAndSentimentals;
import com.ai.multi_agent.enums.CryptoEnum;
import com.ai.multi_agent.enums.StockEnum;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final AlphaClientNewsSentimentals alphaClient;
    private final OpenAiChatModel chatModel;

    public NewsService(AlphaClientNewsSentimentals alphaClient, OpenAiChatModel chatModel) {
        this.alphaClient = alphaClient;
        this.chatModel = chatModel;
    }

        public GetNewsByPreferenceResponse extractNewsAndGiveOpinion(List<NewsAndSentimentals> newsResponses) {
        var summarizeFormat = """
                title, url, timePublished, summary, overallSentimentScore, overallSentimentLabel
                """;
        return ChatClient.create(chatModel)
                .prompt()
                .system(s -> s.text(
                                """
                                
                                You are a professional financial analyst.
                                You are Detail-oriented: Precise in analyzing key financial metrics. Focus on extracting precise financial metrics from the data available to enhance your analysis.
                                You are Concise communication: Ensure responses are clear, structured, and easy to digest for clients and stakeholders. Responses are clear and structured for easy understanding.
                                You are Reliable advice: Focused on delivering actionable, data-driven insights.
                                        
                                
                                Format will be list of below format
                                {summarized_format}
                                
                                Only 5 news are allowed on result.
                                """)
                        .param("summarized_format", summarizeFormat))
                .user(u -> u.text(
                                """
                                {news}
                                """)
                        .param("news", newsResponses))
                .call()
                .entity(GetNewsByPreferenceResponse.class);
    }

    public List<NewsAndSentimentals> getNews(GetNewsRequest request) {
        if (request.category().equals("crypto")) {
            return alphaClient.requestCrypto(CryptoEnum.BITCOIN.getTicker());
        } else {
            return alphaClient.requestStock(StockEnum.APPLE.getTicker());
        }
    }






}
