/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.connect.rest.worker;

import org.streampipes.connect.management.RuntimeResovable;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.model.runtime.RuntimeOptionsRequest;
import org.streampipes.model.runtime.RuntimeOptionsResponse;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.SpMediaType;
import org.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/api/v1/{username}/worker/resolvable")
public class RuntimeResolvableResource extends AbstractContainerResource {

    @POST
    @Path("{id}/configurations")
    @JsonLdSerialized
    @Produces(SpMediaType.JSONLD)
    public Response fetchConfigurations(@PathParam("id") String elementId,
                                        String payload) {

        try {
            RuntimeOptionsRequest runtimeOptionsRequest = new JsonLdTransformer().fromJsonLd(payload,
                    RuntimeOptionsRequest.class);

            ResolvesContainerProvidedOptions adapterClass =
                    RuntimeResovable.getRuntimeResolvableAdapter(elementId);

            List<Option> availableOptions =
                    adapterClass.resolveOptions(runtimeOptionsRequest.getRequestId(),
                            StaticPropertyExtractor.from(runtimeOptionsRequest.getStaticProperties(),
                                    runtimeOptionsRequest.getInputStreams()));

            return ok(new RuntimeOptionsResponse(runtimeOptionsRequest,
                    availableOptions));
        } catch (IOException e) {
            e.printStackTrace();
            return fail();
        }
    }

}
