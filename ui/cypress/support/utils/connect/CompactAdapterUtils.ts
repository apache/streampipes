/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { CompactAdapter } from '../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

export class CompactAdapterUtils {
    public static storeCompactAdapter(compactAdapter: CompactAdapter) {
        return this.postCompactAdapterRequest(
            'application/json',
            compactAdapter,
        );
    }

    public static storeCompactYmlAdapter(body: string) {
        return this.postCompactAdapterRequest('application/yml', body);
    }

    private static postCompactAdapterRequest(contentType: string, body: any) {
        const token = window.localStorage.getItem('auth-token');
        return cy.request({
            method: 'POST',
            url: '/streampipes-backend/api/v2/connect/compact-adapters',
            body: body,
            headers: {
                'Accept': contentType,
                'Content-Type': contentType,
                'Authorization': `Bearer ${token}`,
            },
        });
    }
}
