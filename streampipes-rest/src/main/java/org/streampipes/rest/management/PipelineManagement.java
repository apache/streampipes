/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.management;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.pipeline.PipelineOperationStatus;
import org.streampipes.rest.impl.AbstractRestInterface;

import javax.ws.rs.core.Response;

public class PipelineManagement extends AbstractRestInterface {

    public Response stopPipeline(String pipelineId) {
        try {
            Pipeline pipeline = getPipelineStorage().getPipeline(pipelineId);
            PipelineOperationStatus status = Operations.stopPipeline(pipeline);
            return ok(status);
        } catch
                (Exception e) {
            e.printStackTrace();
            return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
        }
    }

}
