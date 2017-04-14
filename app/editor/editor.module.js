import angular from 'npm/angular';

import spServices from '../services/services.module';

import flowtype from 'legacy/flowtype';
import circleMenu from 'legacy/jQuery.circleMenu';
import jqueryPanzoom from 'npm/jquery.panzoom';
import bootstrap from 'npm/bootstrap';
import angulerUiSortable from 'npm/angular-ui-sortable';
import angulerUiBootstrap from 'npm/angular-ui-bootstrap';

import EditorCtrl from './editor.controller';
import myDataBind from './my-data-bind.directive';
import imageBind  from './image-bind.directive';
import capitalize from './capitalize.filter';
import objectProvider from '../services/object-provider.service';

import any from './directives/any/any.directive';
import customOutput from './directives/customoutput/customoutput.directive';
import domainConceptInput from './directives/domainconcept/domainconcept.directive';
import freetext from './directives/freetext/freetext.directive';
import mappingPropertyUnary from './directives/mappingunary/mappingunary.directive';
import mappingPropertyNary from './directives/mappingnary/mappingnary.directive';
import matchingProperty from './directives/matchingproperty/matchingproperty.directive';
import oneof from './directives/oneof/oneof.directive';
import replaceOutput from './directives/replaceoutput/replaceoutput.directive';
import multipleValueInput from './directives/multivalue/multiple-value-input.directive';
import pipelineElementOptions from './directives/pipeline-element-options/pipeline-element-options.directive';
import collectionStaticProperty from './directives/collection/collection.directive';


export default angular.module('sp.editor', [spServices])
    .controller('EditorCtrl', EditorCtrl)
    .directive('myDataBind', myDataBind)
    .directive('imageBind', imageBind)
    .directive('objectProvider', objectProvider)
    .filter('capitalize', objectProvider)
    .directive('any', any)
    .directive('customOutput', customOutput)
    .directive('domainConceptInput', domainConceptInput)
    .directive('freetext', freetext)
    .directive('mappingPropertyNary', mappingPropertyNary)
    .directive('mappingPropertyUnary', mappingPropertyUnary)
    .directive('matchingProperty', matchingProperty)
    .directive('oneof', oneof)
    .directive('replaceOutput', replaceOutput)
    .directive('multipleValueInput', multipleValueInput)
    .directive('collectionStaticProperty', collectionStaticProperty)
    .name;
