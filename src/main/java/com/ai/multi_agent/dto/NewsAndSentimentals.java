package com.ai.multi_agent.dto;

public record NewsAndSentimentals(
        String title,
        String url,
        String timePublished,
        String summary,
        double overallSentimentScore,
        String overallSentimentLabel) {

}
