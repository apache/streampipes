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

export class JsonLdDialogController {

    $mdDialog: any;
    $timeout: any;
    title: any;
    content: any;
    loadingCompleted: any;

    constructor($mdDialog, $timeout, title, content) {
        this.$mdDialog = $mdDialog;
        this.$timeout = $timeout;
        this.title = title;
        this.content = content;
    }

    $onInit() {
        this.$timeout(() => {
            this.loadingCompleted = true;
        });
    }

    hide() {
        this.$mdDialog.hide();
    };
    cancel() {
        this.$mdDialog.cancel();
    };
}

JsonLdDialogController.$inject = ['$mdDialog', '$timeout', 'title', 'content'];