// @@@SNIPSTART money-transfer-java-workflow-interface
package moneytransferapp;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MoneyTransferWorkflow {

    @WorkflowMethod
    void transfer(TransactionDetails transaction);
}
// @@@SNIPEND
