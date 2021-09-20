package moneytransferapp;

// @@@SNIPSTART money-transfer-project-template-java-activity-interface

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AccountActivity {

    @ActivityMethod
    void deposit(String accountId, String referenceId, double amount);

    @ActivityMethod
    void withdraw(String accountId, String referenceId, double amount);
}
// @@@SNIPEND
