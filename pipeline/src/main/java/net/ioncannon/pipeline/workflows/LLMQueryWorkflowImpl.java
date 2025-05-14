package net.ioncannon.pipeline.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import net.ioncannon.pipeline.activities.LLMQueryActivity;

import java.time.Duration;

@WorkflowImpl(workers = "llm-query-worker")
public class LLMQueryWorkflowImpl implements LLMQueryWorkflow {

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(120))
            .build();

    private final LLMQueryActivity llmQueryActivity = Workflow.newActivityStub(LLMQueryActivity.class, options);

    private String queryResponse;

    @Override
    public void queryMessage(String message, String conversationId) {
        queryResponse = llmQueryActivity.startQuery(message, conversationId);
    }

    @Override
    public String getQueryMessage() {
        return queryResponse;
    }

}
