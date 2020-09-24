package transfermoneyapp;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

// @@@SNIPSTART java-project-template-workflow-interface
@WorkflowInterface
public interface TransferMoneyWorkflow {
    // The Workflow method is called by the initiator process either via code or CLI.
    @WorkflowMethod
    void transfer(String fromAccountId, String toAccountId, String referenceId, double amount);
}
// @@@SNIPEND
