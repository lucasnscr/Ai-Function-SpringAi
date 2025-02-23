# Spring AI Tool Calling and AI Agents

This README details the implementation of **Tool Calling** in Spring AI, focusing on the creation of AI agents that use tools to retrieve information and perform actions. Additionally, we evaluate whether some of the common workflow patterns in AI systems were used, such as **Chain Workflow**, **Parallelization Workflow**, **Routing Workflow**, **Orchestrator-Workers**, and **Evaluator-Optimizer**.

---

## Overview

**Spring AI** is a framework that facilitates the integration of AI functionalities into Spring applications. One of its key features is **Tool Calling**, which allows AI models to interact with external APIs (tools) to extend their capabilities. These tools can be used for:

1. **Information Retrieval**: Accessing external data, such as databases, web services, or search engines.
2. **Action Execution**: Performing automated tasks, such as sending emails or creating records in databases.

In this project, we implemented two main services:

1. **UserPreferencesService**: Retrieves user preferences (e.g., topics of interest like "crypto" or "stocks").
2. **GetNewsByUserPreferences**: Retrieves news based on user preferences and analyzes them using an AI model.

---

## Project Structure

### 1. **UserPreferencesService**

This service is responsible for retrieving user preferences from a repository (e.g., a database). It is implemented as a function (`Function<UserPreferenceRequest, UserPreferenceResponse>`) and exposed as a Spring bean.

```java
@Bean(name = GET_USER_PREFERENCES_FUNCTION_NAME)
@Description("Get topic by userId")
public Function<UserPreferenceRequest, UserPreferenceResponse> getUserPreferencesInfo(
        UserPreferencesRepository userPreferencesRepository) {
    return new UserPreferencesService(userPreferencesRepository);
}
```

#### Features:
- Takes a `userId` as input.
- Queries the repository to retrieve user preferences.
- Returns the user's topic of interest (e.g., "crypto" or "stocks").

---

### 2. **GetNewsByUserPreferences**

This service retrieves news based on the user's topic of interest and analyzes them using an AI model. It is also implemented as a function (`Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse>`).

```java
@Bean(name = GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME)
@Description("Get latest news from user topic")
public Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse> getNewsFromPreference(NewsService newsService) {
    return new GetNewsByUserPreferences(newsService);
}
```

#### Features:
- Takes a topic of interest (e.g., "crypto" or "stocks") as input.
- Queries an external service (e.g., AlphaClientNewsSentimentals) to retrieve related news.
- Uses an AI model (e.g., OpenAI) to summarize and analyze the news.
- Returns the summarized and analyzed news.

---

### 3. **NewsService**

This service is responsible for:
- Retrieving news from an external API (AlphaClientNewsSentimentals).
- Using an AI model (OpenAiChatModel) to summarize and analyze the news.

```java
@Service
public class NewsService {

    private final AlphaClientNewsSentimentals alphaClient;
    private final OpenAiChatModel chatModel;

    public NewsService(AlphaClientNewsSentimentals alphaClient, OpenAiChatModel chatModel) {
        this.alphaClient = alphaClient;
        this.chatModel = chatModel;
    }

    public List<NewsAndSentimentals> getNews(GetNewsRequest request) {
        if (request.category().equals("crypto")) {
            return alphaClient.requestCrypto(CryptoEnum.BITCOIN.getTicker());
        } else {
            return alphaClient.requestStock(StockEnum.APPLE.getTicker());
        }
    }

    public GetNewsByPreferenceResponse extractNewsAndGiveOpinion(List<NewsAndSentimentals> newsResponses) {
        return ChatClient.create(chatModel)
                .prompt()
                .system(s -> s.text("You are a professional financial analyst..."))
                .user(u -> u.text("{news}").param("news", newsResponses))
                .call()
                .entity(GetNewsByPreferenceResponse.class);
    }
}
```

---

## Workflow Patterns Used

We evaluated whether the following workflow patterns were used in the project:

### 1. **Chain Workflow**
   - **Description**: Chaining prompts or tasks to improve accuracy.
   - **Application in the Project**: The workflow involves retrieving user preferences and then fetching and analyzing news based on those preferences. This can be seen as a chain of tasks, where the output of one task (user preferences) is used as input for the next (news retrieval).

### 2. **Routing Workflow**
   - **Description**: Intelligent routing of inputs to specialized flows.
   - **Application in the Project**: The `GetNewsByUserPreferences` service routes the request to different API endpoints (crypto or stocks) based on the user's topic of interest. This is an example of **Routing Workflow**.

### 3. **Orchestrator-Workers**
   - **Description**: A central AI (orchestrator) distributes tasks to specialized subprocesses (workers).
   - **Application in the Project**: The `NewsService` acts as an orchestrator, coordinating news retrieval and analysis with the AI model. The AI model (OpenAiChatModel) functions as a specialized worker for text analysis.

### 4. **Evaluator-Optimizer**
   - **Description**: Use of LLMs for iterative evaluation and refinement.
   - **Application in the Project**: The `extractNewsAndGiveOpinion` method uses an AI model to summarize and analyze news, which can be seen as a form of content evaluation and refinement.

### 5. **Parallelization Workflow**
   - **Description**: Parallel execution of tasks for greater efficiency.
   - **Application in the Project**: There is no evidence of parallel execution in the provided code. News retrieval and analysis are performed sequentially.

---

## Conclusion

This project demonstrates how **Spring AI** can be used to create AI agents that interact with external tools to retrieve information and perform actions. The workflow patterns **Chain Workflow**, **Routing Workflow**, and **Orchestrator-Workers** were identified in the code, while **Parallelization Workflow** and **Evaluator-Optimizer** were not directly applied.

### Next Steps
- Implement **Parallelization Workflow** to improve efficiency in news retrieval.
- Explore the use of **Evaluator-Optimizer** for iterative refinement of the analyses generated by the AI model.

---

## How to Run the Project

1. **Prerequisites**:
   - Java 17+
   - Spring Boot 3.x
   - OpenAI API Key (for the AI model)
   - Configuration of the user preferences repository (e.g., a database).

2. **Setup**:
   - Add the OpenAI API Key in `application.properties`:
     ```properties
     spring.ai.openai.api-key=YOUR_KEY_HERE
     ```
     
   - Add the Alpha Advantage API Key in `application.properties`:
     ```properties
     Alpha.api-key==YOUR_KEY_HERE
     ```

     - Running the Sql's: Schema and data

3. **Execution**:
   - Run the project with:
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Testing**:
   - Use REST endpoints to test preference and news retrieval.

---

## Contribution

Contributions are welcome! Feel free to open issues or pull requests for improvements or new features.

---
