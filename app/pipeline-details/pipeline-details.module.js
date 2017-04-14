import angular from 'npm/angular';

import spServices from '../services/services.module';

import pipelinePreview from './directives/preview/pipeline-preview.directive';

import PipelineDetailsCtrl from './pipeline-details.controller';

export default angular.module('sp.pipelineDetails', [spServices])
    .controller('PipelineDetailsCtrl', PipelineDetailsCtrl)
    .directive('pipelinePreview', pipelinePreview)
    .name;