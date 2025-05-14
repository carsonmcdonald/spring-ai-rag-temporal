package net.ioncannon.pipeline.activities;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(workers = "llm-query-worker")
public class LLMQueryActivityImpl implements LLMQueryActivity {

    private final ChatClient chatClient;

    public LLMQueryActivityImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(retrievalAugmentationAdvisor, messageChatMemoryAdvisor)
                .build();
    }

    @Override
    public String startQuery(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
    }

}
