import * as angular from 'angular';

import spServices from '../services/services.module';

import {PipelinePreviewComponent} from './components/preview/pipeline-preview.component';
import {PipelineStatusComponent} from './components/status/pipeline-status.component';
import {PipelineElementsComponent} from './components/elements/pipeline-elements.component'
import {PipelineElementsRowComponent} from './components/elements/pipeline-elements-row.component'
import {PipelineActionsComponent} from './components/actions/pipeline-actions.component';
//import customizeDialog from '../editor/directives/customize/customize-dialog.directive';
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
    .name;