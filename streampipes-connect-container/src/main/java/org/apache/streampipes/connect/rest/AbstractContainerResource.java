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

package org.apache.streampipes.connect.rest;

import org.apache.streampipes.model.shared.message.Message;

import javax.ws.rs.core.Response;

public abstract class AbstractContainerResource {

    protected <T> Response ok(T entity) {
        return Response
                .ok()
                .entity(entity)
                .build();
    }

    protected <T> Response ok() {
        return Response
                .ok()
                .build();
    }


    protected Response statusMessage(Message message) {
        return ok(message);
    }

    protected Response fail() {
       return Response.serverError().build();
    }

    protected <T> Response fail(T entity) {
        return Response
                .serverError()
                .entity(entity)
                .build();
    }
}
