package uk.co.itmoore.intellisubsteps.execution;

import com.technophobia.substeps.execution.ExecutionNodeResult;

/**
 * Created by ian on 15/09/15.
 */
public interface ExecutionNodeResultNotificationHandler {
    void handleNotification(ExecutionNodeResult result);
}
