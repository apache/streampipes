/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { AdapterDescription } from '@streampipes/platform-services';

/** Prepare an independent draft; the normal create endpoint assigns its IDs. */
export function createAdapterFromExisting(
    source: AdapterDescription,
): AdapterDescription {
    const adapter = AdapterDescription.fromData(structuredClone(source));
    adapter.elementId = undefined;
    adapter.rev = undefined;
    adapter.createdAt = undefined;
    adapter.correspondingDataStreamElementId = undefined;
    adapter.connectedTo = [];
    adapter.internallyManaged = false;
    adapter.running = false;
    adapter.selectedEndpointUrl = undefined;
    adapter.selectedServiceId = undefined;
    adapter.correspondingServiceGroup = undefined;
    adapter.eventGrounding = undefined;
    if (adapter.deploymentConfiguration) {
        adapter.deploymentConfiguration.selectedEndpointUrl = undefined;
    }
    if (adapter.dataStream) {
        adapter.dataStream.elementId = undefined;
        adapter.dataStream.rev = undefined;
        adapter.dataStream.correspondingAdapterId = undefined;
        adapter.dataStream.connectedTo = [];
        adapter.dataStream.internallyManaged = false;
        adapter.dataStream.eventGrounding = undefined;
        adapter.dataStream.index = undefined;
    }
    return adapter;
}
