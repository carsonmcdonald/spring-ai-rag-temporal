package net.ioncannon.api.controllers;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import net.ioncannon.api.workflows.RAGProcessingWorkflow;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class AdminActionController {

    private final WorkflowClient workflowClient;

    public AdminActionController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @PostMapping("/admin/add-resource")
    public String addResource(@RequestBody String resourceUrl) {
        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setWorkflowId(UUID.randomUUID().toString())
                .setTaskQueue("rag-pipeline-task-queue")
                .build();

        RAGProcessingWorkflow ragProcessingWorkflow = workflowClient.newWorkflowStub(RAGProcessingWorkflow.class, workflowOptions);

        WorkflowClient.start(ragProcessingWorkflow::processResource, resourceUrl);

        return workflowOptions.getWorkflowId();
    }

    @GetMapping("/admin/resource/{workflowId}/status")
    public String fetchResourceStatus(@PathVariable String workflowId) {
        RAGProcessingWorkflow ragProcessingWorkflow = workflowClient.newWorkflowStub(RAGProcessingWorkflow.class, workflowId);
        return ragProcessingWorkflow.getStatus();
    }

}
