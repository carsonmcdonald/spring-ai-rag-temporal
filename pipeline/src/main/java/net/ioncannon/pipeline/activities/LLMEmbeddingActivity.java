package net.ioncannon.pipeline.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface LLMEmbeddingActivity {
    @ActivityMethod
    LLMEmbeddingActivityResult extractAndStore(String localFileLocation);
}
