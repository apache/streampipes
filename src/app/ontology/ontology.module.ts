import * as angular from 'angular';

import ngFileUpload from 'ng-file-upload';

import { OntologyCtrl } from './ontology.controller';

export default angular.module('sp.ontology', [ngFileUpload])
	.controller('OntologyCtrl', OntologyCtrl)
	.name;
