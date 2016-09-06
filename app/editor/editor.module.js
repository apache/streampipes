import angular from 'npm/angular';

import EditorCtrl from './editor.controller'
import myDataBind from './my-data-bind.directive'
import imageBind  from './image-bind.directive'
import objectProvider from './object-provider.service'
import capitalize from './capitalize.filter'

import any from './directives/any/any.directive'
import customOutput from './directives/customoutput/customoutput.directive'
import domainConceptInput from './directives/domainconcept/domainconcept.directive'
import freetext from './directives/freetext/freetext.directive'
import mappingPropertyUnary from './directives/mappingunary/mappingunary.directive'
import mappingPropertyNary from './directives/mappingnary/mappingnary.directive'
import matchingProperty from './directives/matchingproperty/matchingproperty.directive'
import oneof from './directives/oneof/oneof.directive'
import replaceOutput from './directives/replaceoutput/replaceoutput.directive'


export default angular.module('sp.editor', [])
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
	.name;
