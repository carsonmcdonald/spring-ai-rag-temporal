package net.ioncannon.pipeline.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.Workflow;
import net.ioncannon.pipeline.activities.LLMEmbeddingActivity;
import net.ioncannon.pipeline.activities.LLMEmbeddingActivityResult;
import net.ioncannon.pipeline.activities.ResourceFetchActivity;
import net.ioncannon.pipeline.activities.ResourceFetchActivityResult;

import java.time.Duration;

@WorkflowImpl(workers = "rag-pipeline-worker")
public class RAGProcessingWorkflowImpl implements RAGProcessingWorkflow {

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .build();

    private final ResourceFetchActivity resourceFetchActivity = Workflow.newActivityStub(ResourceFetchActivity.class, options);
    private final LLMEmbeddingActivity llmEmbeddingActivity = Workflow.newActivityStub(LLMEmbeddingActivity.class, options);

    private String status = "INIT";

    @Override
    public void processResource(String resourceUrl) {
        status = "STARTED";

        ResourceFetchActivityResult resourceFetchActivityResult = resourceFetchActivity.startFetch(resourceUrl);
        if (resourceFetchActivityResult.failed()) {
            status = "FAILED";
        } else {
            status = "FETCHED";

            LLMEmbeddingActivityResult llmEmbeddingActivityResult = llmEmbeddingActivity.extractAndStore(resourceFetchActivityResult.fetchedFileLocation());

            if (llmEmbeddingActivityResult.failed()) {
                status = "FAILED";
            } else {
                status = "DONE";
            }
        }
    }

    @Override
    public String getStatus() {
        return status;
    }

}
