package net.ioncannon.api.workflows;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface RAGProcessingWorkflow {
    @WorkflowMethod
    void processResource(String resourceUrl) ;

    @QueryMethod
    String getStatus();
}