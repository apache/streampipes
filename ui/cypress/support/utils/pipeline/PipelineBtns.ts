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

export class PipelineBtns {
    public static statusPipeline() {
        return cy.dataCy('status-pipeline-green', { timeout: 10000 });
    }

    public static stopPipeline() {
        return cy.dataCy('stop-pipeline-button', { timeout: 10000 });
    }

    public static deletePipeline() {
        return cy.dataCy('delete-pipeline', { timeout: 10000 });
    }
}
