package net.ioncannon.pipeline.activities;

import io.temporal.spring.boot.ActivityImpl;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
@ActivityImpl(workers = "rag-pipeline-worker")
public class LLMEmbeddingActivityImpl implements LLMEmbeddingActivity {
    private final VectorStore vectorStore;

    public LLMEmbeddingActivityImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public LLMEmbeddingActivityResult extractAndStore(String localFileLocation) {
        try {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new FileSystemResource(localFileLocation));
            TextSplitter textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(tikaDocumentReader.get()));

            return new LLMEmbeddingActivityResult(false, null);
        } catch (Exception e) {
            return new LLMEmbeddingActivityResult(true, e.getMessage());
        }
    }
}
