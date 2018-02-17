import angular from 'angular';

import ngFileUpload from 'npm/ng-file-upload';

import { OntologyCtrl } from './ontology.controller';

export default angular.module('sp.ontology', [ngFileUpload])
	.controller('OntologyCtrl', OntologyCtrl)
	.name;
