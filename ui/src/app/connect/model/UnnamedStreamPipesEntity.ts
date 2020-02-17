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

import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {RdfId} from "../../platform-services/tsonld/RdfId";

@RdfsClass('sp:UnnamedStreamPipesEntity')
export class UnnamedStreamPipesEntity {

    private prefix = "urn:streampipes.org:spi:";
    private chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @RdfId
    public id: string;

    constructor() {
        this.id = this.prefix + this.randomString(6);
    }

    randomString(length) {
        let result = '';
        for (let i = length; i > 0; --i) result += this.chars[Math.floor(Math.random() * this.chars.length)];
        return result;
    }

}