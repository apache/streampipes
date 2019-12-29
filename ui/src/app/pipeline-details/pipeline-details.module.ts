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

import * as angular from 'angular';

import spServices from '../services/services.module';

import {PipelinePreviewComponent} from './components/preview/pipeline-preview.component';
import {PipelineStatusComponent} from './components/status/pipeline-status.component';
import {PipelineElementsComponent} from './components/elements/pipeline-elements.component'
import {PipelineElementsRowComponent} from './components/elements/pipeline-elements-row.component';
import {PipelineActionsComponent} from './components/actions/pipeline-actions.component';
//import {CustomizeDialogComponent} from '../editor/components/customize/customize-dialog.component';
import {QuickEditComponent} from './components/edit/quickedit.component';

import {PipelineDetailsCtrl} from './pipeline-details.controller';

export default angular.module('sp.pipelineDetails', [spServices])
    .controller('PipelineDetailsCtrl', PipelineDetailsCtrl)
    .component('pipelineStatus', PipelineStatusComponent)
    .component('pipelinePreview', PipelinePreviewComponent)
    .component('pipelineElements', PipelineElementsComponent)
    .component('pipelineElementsRow', PipelineElementsRowComponent)
    .component('pipelineActions', PipelineActionsComponent)
    .component('quickEdit', QuickEditComponent)
    //.component('customizeDialog', CustomizeDialogComponent)
    .name;