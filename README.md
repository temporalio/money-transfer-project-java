# Transfer money: Java project

This project can be used as a template to start building your own Temporal Workflow application.

Follow the [Run your first app tutorial](https://docs/temporal.io/docs/java-run-your-first-app) to learn more about Temporal Workflows.

## How to use the template

To use the template, either download it as a zip file or click "Use Template" to make a copy of it in your own Github account.

## Build the project

In the project's root directory run:

```
./gradlew build
```

## Run the Workflow

First, make sure the [Temporal server](https://docs.temporal.io/docs/install-temporal-server) is running.

To start the Workflow, run:

```
./gradlew initiateTransfer
```

To start the Worker, run:

```
./gradlew startWorker
```
