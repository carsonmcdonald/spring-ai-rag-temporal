package net.ioncannon.pipeline.workflows;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface LLMQueryWorkflow {
    @WorkflowMethod
    void queryMessage(String message, String conversationId);

    @QueryMethod
    String getQueryMessage();
}
