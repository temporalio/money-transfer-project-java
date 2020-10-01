package moneytransferapp;

// @@@SNIPSTART money-transfer-project-template-java-activity-interface

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface AccountActivity {

    void deposit(String accountId, String referenceId, double amount);

    void withdraw(String accountId, String referenceId, double amount);
}
// @@@SNIPEND
