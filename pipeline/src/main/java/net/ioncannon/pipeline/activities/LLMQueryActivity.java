package net.ioncannon.pipeline.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface LLMQueryActivity {
    @ActivityMethod
    String startQuery(String message, String conversationId);
}
