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

export class TransitionService {

    AuthService: any;
    $mdDialog: any;
    isPipelineAssemblyEmpty: boolean = true;

    constructor(AuthService, $mdDialog) {
        this.AuthService = AuthService;
        this.$mdDialog = $mdDialog;
    }

    onTransitionStarted(transitionInfo) {
        if (transitionInfo.$from().name === 'streampipes.editor' && !(this.isPipelineAssemblyEmpty)) {
            return this.showDiscardPipelineDialog().then(() => {
                this.isPipelineAssemblyEmpty = true;
                return true;
            }, function () {
                return false;
            });
        }
    }

    makePipelineAssemblyEmpty(status) {
        this.isPipelineAssemblyEmpty = status;
    }

    getPipelineAssemblyEmpty() {
        return this.isPipelineAssemblyEmpty;
    }

    showDiscardPipelineDialog() {
        var confirm = this.$mdDialog.confirm()
            .title('Discard changes?')
            .textContent('Your current pipeline will be discarded.')
            .ok('Discard Changes')
            .cancel('Cancel');
        return this.$mdDialog.show(confirm);
    }
}
TransitionService.$inject = ['AuthService', '$mdDialog'];