import angular from 'npm/angular';

import spServices from '../services/services.module';

import pipelinePreview from './directives/preview/pipeline-preview.directive';
import pipelineStatus from './directives/status/pipeline-status.directive';
import pipelineElements from './directives/elements/pipeline-elements.directive'
import pipelineElementsRow from './directives/elements/pipeline-elements-row.directive'
import pipelineActions from './directives/actions/pipeline-actions.directive';
//import customizeDialog from '../editor/directives/customize/customize-dialog.directive';
import quickEdit from './directives/edit/quickedit.directive';

import PipelineDetailsCtrl from './pipeline-details.controller';

export default angular.module('sp.pipelineDetails', [spServices])
    .controller('PipelineDetailsCtrl', PipelineDetailsCtrl)
    .directive('pipelineStatus', pipelineStatus)
    .directive('pipelinePreview', pipelinePreview)
    .directive('pipelineElements', pipelineElements)
    .directive('pipelineElementsRow', pipelineElementsRow)
    .directive('pipelineActions', pipelineActions)
    .directive('quickEdit', quickEdit)
    .name;