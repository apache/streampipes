import * as angular from 'angular';

import spServices from '../services/services.module';

import {PipelineCtrl} from './pipelines.controller';
import {PipelineDetailsComponent} from './components/pipeline-details/pipeline-details.component';
import {PipelineCategoryFilter} from './pipeline-category.filter';
import {CategoryAlreadyInPipelineFilter} from './category-already-in-pipeline.filter';
import {PipelineOperationsService} from "./services/pipeline-operations.service";

export default angular.module('sp.pipeline', [spServices])
	.controller('PipelineCtrl', PipelineCtrl)
	.component('pipelineDetails', PipelineDetailsComponent)
	.filter('pipelineCategoryFilter', PipelineCategoryFilter)
	.filter('categoryAlreadyInPipelineFilter', CategoryAlreadyInPipelineFilter)
	.service('PipelineOperationsService', PipelineOperationsService)
	.name;
