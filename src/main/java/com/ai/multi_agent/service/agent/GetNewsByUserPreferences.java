package com.ai.multi_agent.service.agent;

import com.ai.multi_agent.dto.GetNewsByPreferenceRequest;
import com.ai.multi_agent.dto.GetNewsByPreferenceResponse;
import com.ai.multi_agent.dto.GetNewsRequest;
import com.ai.multi_agent.dto.NewsAndSentimentals;
import com.ai.multi_agent.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;


public class GetNewsByUserPreferences implements Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse> {

    private static final String CRYPTO = "crypto";
    private static final String STOCKS = "stocks";

    private static final Logger logger = LoggerFactory.getLogger(GetNewsByUserPreferences.class);
    private final NewsService newsService;

    public GetNewsByUserPreferences(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    public GetNewsByPreferenceResponse apply(GetNewsByPreferenceRequest getNewsByPreferenceRequest) {

        String topic = getNewsByPreferenceRequest.topic();
        GetNewsRequest request = null;
        if (topic.equals(CRYPTO)) {
            request = new GetNewsRequest(CRYPTO);
        }else {
            request = new GetNewsRequest(STOCKS);

        }

        logger.info("Request: {}", request);
        List<NewsAndSentimentals> news = newsService.getNews(request);
        return newsService.extractNewsAndGiveOpinion(news);
    }
}
