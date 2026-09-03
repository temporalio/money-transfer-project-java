package moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.envconfig.ClientConfigProfile;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import java.io.IOException;

/** Creates a Temporal client from the standard environment and profile configuration. */
public final class ClientConfigProvider {
    private static final String DEFAULT_ADDRESS = "127.0.0.1:7233";
    private static final String DEFAULT_NAMESPACE = "default";

    private ClientConfigProvider() {}

    public static WorkflowClient newClient() throws IOException {
        ClientConfigProfile profile = ClientConfigProfile.load();
        ClientConfigProfile.Builder profileBuilder = profile.toBuilder();

        if (isBlank(profile.getAddress())) {
            profileBuilder.setAddress(DEFAULT_ADDRESS);
        }
        if (isBlank(profile.getNamespace())) {
            profileBuilder.setNamespace(DEFAULT_NAMESPACE);
        }

        ClientConfigProfile configuredProfile = profileBuilder.build();
        WorkflowServiceStubsOptions serviceOptions =
                configuredProfile.toWorkflowServiceStubsOptions();
        WorkflowClientOptions clientOptions = configuredProfile.toWorkflowClientOptions();
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(serviceOptions);
        return WorkflowClient.newInstance(service, clientOptions);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
