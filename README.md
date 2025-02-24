# AI Agents and Their Patterns: A Practical Perspective

As AI-driven applications evolve, developers are increasingly leveraging large language model (LLM) agents to execute complex tasks efficiently. However, the most effective implementations rely not on overly complex frameworks but on simple, composable design patterns.

This article explores the distinction between workflows and agents, identifies key patterns used in AI-driven systems, and examines how these concepts apply to a Spring AI implementation designed for intelligent news retrieval based on user preferences.

## What Are AI Agents?

AI agents are systems that use LLMs to process information, interact with tools, and perform actions. They can be classified into two broad categories:

- Workflows: Structured sequences where LLMs and external tools follow predefined execution paths. These systems prioritize predictability and are ideal for well-defined, repeatable tasks.

- Agents: More dynamic and autonomous systems where LLMs dictate their own processes, selecting tools and determining how to accomplish tasks. This allows for greater flexibility and adaptation.

Choosing between these approaches depends on the problem domain—workflows excel in structured automation, while agents are preferable when decisions need to be made dynamically at scale.

![Ai Agent Flow](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/ryed4nv20ly5wc7lzmge.gif)


## Key Patterns in AI Agent Systems

1. **Chain Workflow**

A Chain Workflow organizes multiple steps in a linear sequence, where the output of one step feeds into the next. It ensures clarity and control while allowing for slight adaptability.

**When to use:**

- Tasks with clear sequential steps
- When you want to trade latency for higher accuracy
- When each step builds on the previous step's output

![Chain Workflow](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/mf08z3fd28tnsjiah5o0.png)

2. **Parallelization Workflow**

This pattern involves executing multiple tasks simultaneously, increasing efficiency in data-intensive operations. It is useful when fetching or processing large amounts of information concurrently.

**When to use:**

- Processing large volumes of similar but independent items
- Tasks requiring multiple independent perspectives
- When processing time is critical and tasks are parallelizable

![Parallelization Workflow](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/febbnyphui29v8vv2r22.jpeg)

3. **Routing Workflow**

A Routing Workflow dynamically directs execution paths based on input conditions, allowing the system to adapt to different cases without predefined sequences.

**When to use:**

- Complex tasks with distinct categories of input
- When different inputs require specialized processing
- When classification can be handled accurately

![Routing workflow](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/klmzl6w50pr5f6egfb26.jpeg)

4. **Orchestrator-Workers**

Orchestrator AI delegates tasks to multiple specialized worker agents, each responsible for a distinct function (e.g., data retrieval, analysis, summarization).

**When to use:**

- Complex tasks where subtasks can't be predicted upfront
- Tasks requiring different approaches or perspectives
- Situations needing adaptive problem-solving

![Orchestrator-Workers Workflow](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/txvz9eqf9jw7pz3ovkin.jpeg)

5. **Evaluator-Optimizer**

This pattern involves an Evaluator assessing the quality of an agent’s output and an Optimizer refining future responses based on feedback, improving accuracy over time.

**When to use:**

- Clear evaluation criteria exist
- Iterative refinement provides measurable value
- Tasks benefit from multiple rounds of critique

![Evaluator-Optimizer Workflow](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/ng7tdgvzvk1ci522dytf.jpeg)

## AI Agent Patterns Used

Patterns were used in the project:

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

# Spring Ai and Tool Calling

**Spring AI** is a framework that facilitates the integration of AI functionalities into Spring applications. One of its key features is **Tool Calling**, which allows AI models to interact with external APIs (tools) to extend their capabilities. These tools can be used for:

Details the implementation of [Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html) in Spring AI, focusing on the creation of AI agents that use tools to retrieve information and perform actions. Additionally, we evaluate whether some of the common workflow patterns in AI systems were used, such as **Chain Workflow**, **Parallelization Workflow**, **Routing Workflow**, **Orchestrator-Workers**, and **Evaluator-Optimizer**.

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
