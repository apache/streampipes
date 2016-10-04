import angular from 'npm/angular';

import spServices from '../services/services.module';

import PipelineCtrl from './pipelines.controller';
import myStreamDataAndImageBind from './my-stream-data-and-image-bind.directive';
import mySepaDataAndImageBind from './my-sepa-data-and-image-bind.directive';
import myActionDataAndImageBind from './my-actions-data-and-image-bind.directive';
import pipelineDetails from './directives/pipeline-details.directive';
import pipelineCategoryFilter from './pipeline-category.filter';
import categoryAlreadyInPipelineFilter from './category-already-in-pipeline.filter';

export default angular.module('sp.pipeline', [spServices])
	.controller('PipelineCtrl', PipelineCtrl)
	.directive('myStreamDataAndImageBind', myStreamDataAndImageBind)
	.directive('mySepaDataAndImageBind', mySepaDataAndImageBind)
	.directive('myActionDataAndImageBind', myActionDataAndImageBind)
	.directive('pipelineDetails', pipelineDetails)
	.filter('pipelineCategoryFilter', pipelineCategoryFilter)
	.filter('categoryAlreadyInPipelineFilter', categoryAlreadyInPipelineFilter) 
	.name;
