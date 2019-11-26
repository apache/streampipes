/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

import { PipelineCtrl } from './pipelines.controller';
import { PipelineDetailsComponent } from './components/pipeline-details/pipeline-details.component';
import { PipelineCategoryFilter } from './pipeline-category.filter';
import { CategoryAlreadyInPipelineFilter } from './category-already-in-pipeline.filter';
import { PipelineOperationsService } from "./services/pipeline-operations.service";
import { VersionService } from "../services/version/version.service";

import ngFileUpload from 'ng-file-upload';

export default angular.module('sp.pipeline', [spServices, ngFileUpload])
	.controller('PipelineCtrl', PipelineCtrl)
	.component('pipelineDetails', PipelineDetailsComponent)
	.filter('pipelineCategoryFilter', PipelineCategoryFilter)
	.filter('categoryAlreadyInPipelineFilter', CategoryAlreadyInPipelineFilter)
	.service('PipelineOperationsService', PipelineOperationsService)
	.service('VersionService', VersionService)
	.name;
