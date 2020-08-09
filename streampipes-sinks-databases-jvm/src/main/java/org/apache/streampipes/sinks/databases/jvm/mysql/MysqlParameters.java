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

package org.apache.streampipes.sinks.databases.jvm.mysql;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class MysqlParameters extends EventSinkBindingParams {

    private String host;
    private String user;
    private String password;
    private String db;
    private String table;
    private Integer port;


    public MysqlParameters(DataSinkInvocation graph, String host, String user, String password, String db, String table,
                           Integer port) {
        super(graph);
        this.host = host;
        this.user = user;
        this.password = password;
        this.db = db;
        this.table = table;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDB() {
        return db;
    }

    public String getTable() {
        return table;
    }

    public Integer getPort() { return port; }
}
