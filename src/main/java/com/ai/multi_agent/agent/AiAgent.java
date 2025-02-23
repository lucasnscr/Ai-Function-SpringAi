package com.ai.multi_agent.agent;

import com.ai.multi_agent.dto.GetNewsByPreferenceRequest;
import com.ai.multi_agent.dto.GetNewsByPreferenceResponse;
import com.ai.multi_agent.dto.UserPreferenceRequest;
import com.ai.multi_agent.dto.UserPreferenceResponse;
import com.ai.multi_agent.repository.UserPreferencesRepository;
import com.ai.multi_agent.service.NewsService;
import com.ai.multi_agent.service.agent.GetNewsByUserPreferences;
import com.ai.multi_agent.service.agent.UserPreferencesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class AiAgent {


    public static final String GET_USER_PREFERENCES_FUNCTION_NAME = "getUserPreferences";
    public static final String GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME = "getLatestNewsByTopic";

    @Bean(name = GET_USER_PREFERENCES_FUNCTION_NAME)
    @Description("Get topic by userId")
    public Function<UserPreferenceRequest, UserPreferenceResponse> getUserPreferencesInfo(
            UserPreferencesRepository userPreferencesRepository) {
        return new UserPreferencesService(userPreferencesRepository);
    }

    @Bean(name = GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME)
    @Description("Get latest news from user topic")
    public Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse> getNewsFromPreference(NewsService newsService) {
        return new GetNewsByUserPreferences(newsService);
    }

}
