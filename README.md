# Money transfer project: Java

Learn how the pieces of a Temporal application work together.
Follow the [Run your first app tutorial](https://docs.temporal.io/docs/java/run-your-first-app-tutorial)
to learn more about Temporal Workflows.

Note: This project uses [Snipsync](https://github.com/temporalio/snipsync)
comment wrappers to automatically keep code snippets up to date within our
documentation.

## Step 1: Start the Temporal Service
If you don't already have the `temporal` CLI installed on your machine,
follow the [Install the Temporal CLI](https://docs.temporal.io/cli)
instructions before you continue with the next step.

Open a terminal window and run the following command to start the
Temporal Service using the `temporal` CLI:

```
temporal server start-dev --ui-port 8080 --db-filename=temporal.db
```

The second argument specifies the port number for the Temporal Web
UI to use. If you have something else running on port 8080, modify
this command to specify a different port number.

## Step 2: Run the Worker

Open another terminal window and run the following command to run
the Worker:

```
mvn compile exec:java -Dexec.mainClass="moneytransferapp.MoneyTransferWorker" 
```

## Step 3: Start the Workflow

Open another terminal window and run the following command to start
the Workflow:

```
mvn compile exec:java -Dexec.mainClass="moneytransferapp.TransferApp"
```

The Workflow Execution should quickly complete. You can view its
status and details with the Temporal Web UI, which you can access
at <http://localhost:8080>.

## Configure a Temporal Client

The worker and starter automatically load Temporal client configuration from
the standard environment variables and Temporal CLI profile configuration. With
no configuration, they connect to `127.0.0.1:7233` in the `default` namespace.
Set values such as `TEMPORAL_ADDRESS` and `TEMPORAL_NAMESPACE`, or select a
configured profile with `TEMPORAL_PROFILE`, to connect to another Temporal
Service.


## Optional: Using the Makefile 

This project contains a Makefile, which defines targets corresponding to
the `mvn` commands shown in the above examples. If you have the `make`
utility installed on your machine, you can run `make worker` and
`make run` as an alternative to running the commands in steps 2 and 3.
