package com.ai.multi_agent.transformer;

import com.ai.multi_agent.dto.NewsAndSentimentals;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
public class NewsAndSentimentalsConverter {

    public List<NewsAndSentimentals> convertJsonToData(String name, JSONObject sentiments) {
        JSONArray feed = sentiments.optJSONArray("feed");
        if (ObjectUtils.isEmpty(feed)) {
            return List.of();
        }
        return IntStream.range(0, feed.length())
                .mapToObj(i -> prepareNewsContent(name, feed.getJSONObject(i)))
                .collect(Collectors.toList());
    }

    private NewsAndSentimentals prepareNewsContent(String name, JSONObject jsonObject) {
        return extractNewsAndSentimentals(jsonObject);
    }

    private NewsAndSentimentals extractNewsAndSentimentals(JSONObject jsonObject) {
        return new NewsAndSentimentals(
                jsonObject.optString("title", "No Title"),
                jsonObject.optString("url", "No URL"),
                jsonObject.optString("time_published", "No Time Published"),
                jsonObject.optString("summary", "No Summary"),
                jsonObject.optDouble("overall_sentiment_score", 0.0),
                jsonObject.optString("overall_sentiment_label", "Neutral"));
    }
}
