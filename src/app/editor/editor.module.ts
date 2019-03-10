import * as angular from 'angular';

import 'angular-ui-sortable';
import 'angular-ui-bootstrap';

import spServices from '../services/services.module';

import 'legacy/flowtype';
import 'legacy/jQuery.circleMenu';
import 'jquery.panzoom';
import 'npm/bootstrap';
import 'npm/angular-trix';
import 'npm/angular-datatables';
import 'npm/ng-showdown';

import {EditorCtrl} from './editor.controller';
import myDataBind from './my-data-bind.directive';
import imageBind  from './image-bind.directive';
//import capitalize from './capitalize.filter';
import displayRecommendedFilter from './filter/display-recommended.filter';
//import objectProvider from '../services/object-provider.service';

import {AnyComponent} from './components/any/any.component';
import {CustomOutputComponent} from './components/customoutput/customoutput.component';
import {DomainConceptComponent} from './components/domainconcept/domainconcept.component';
import {FreeTextComponent} from './components/freetext/freetext.component';
import {MappingUnaryComponent} from './components/mappingunary/mappingunary.component';
import {MappingNaryComponent} from './components/mappingnary/mappingnary.component';
import {MatchingPropertyComponent} from './components/matchingproperty/matchingproperty.component';
import {OneOfComponent} from './components/oneof/oneof.component';
import {ReplaceOutputComponent} from './components/replaceoutput/replaceoutput.component';
import {MultipleValueInputComponent} from './components/multivalue/multiple-value-input.component';
import {PipelineElementOptionsComponent} from './components/pipeline-element-options/pipeline-element-options.component';
import {CollectionComponent} from './components/collection/collection.component';
import {CustomizeDialogComponent} from './components/customize/customize-dialog.component';
import {TopicSelectionDialogComponent} from './components/topic/topic-selection-dialog.component';
import {PipelineComponent} from './components/pipeline/pipeline.component';
import {EditorDialogManager} from './services/editor-dialog-manager.service';
import {PipelineElementComponent} from './components/pipeline-element/pipeline-element.component';
import {PipelineElementRecommendationComponent} from "./components/pipeline-element-recommendation/pipeline-element-recommendation.component";
import {PipelineElementRecommendationService} from "./services/pipeline-element-recommendation.service";
import {PipelineAssemblyComponent} from "./components/pipeline-assembly/pipeline-assembly.component";
import {PipelineElementIconStandComponent} from './components/pipeline-element-icon-stand/pipeline-element-icon-stand.component';
import {PipelineValidationService} from "./services/pipeline-validation.service";
import {OneOfRemoteComponent} from "./components/oneof-remote/oneof-remote.component";

import {TextValidatorDirective} from "./validator/text/text-validator.directive";

import selectFilter from './filter/select.filter';
import elementNameFilter from './filter/element-name.filter';
import {PropertySelectionComponent} from "./components/customoutput/propertyselection/property-selection.component";
import {PipelineElementDocumentationComponent} from "./components/pipeline-element-documentation/pipeline-element-documentation.component";


export default angular.module('sp.editor', [spServices, 'angularTrix', 'ngAnimate', 'datatables', 'ng-showdown'])
    .controller('EditorCtrl', EditorCtrl)
    .directive('myDataBind', myDataBind)
    .directive('imageBind', imageBind)
    .directive("textValidator", () => new TextValidatorDirective())
    .filter('displayRecommendedFilter', displayRecommendedFilter)
    .filter('selectFilter', selectFilter)
    .filter('elementNameFilter', elementNameFilter)
    .component('any', AnyComponent)
    .component('customOutput', CustomOutputComponent)
    .component('domainConceptInput', DomainConceptComponent)
    .component('freetext', FreeTextComponent)
    .component('mappingPropertyNary', MappingNaryComponent)
    .component('mappingPropertyUnary', MappingUnaryComponent)
    .component('matchingProperty', MatchingPropertyComponent)
    .component('oneof', OneOfComponent)
    .component('oneofRemote', OneOfRemoteComponent)
    .component('propertySelection', PropertySelectionComponent)
    .component('replaceOutput', ReplaceOutputComponent)
    .component('multipleValueInput', MultipleValueInputComponent)
    .component('collectionStaticProperty', CollectionComponent)
    .component('customizeDialog', CustomizeDialogComponent)
    .component('topicSelectionDialog', TopicSelectionDialogComponent)
    .component('pipeline', PipelineComponent)
    .component('pipelineElement', PipelineElementComponent)
    .component('pipelineElementRecommendation', PipelineElementRecommendationComponent)
    .component('pipelineAssembly', PipelineAssemblyComponent)
    .component('pipelineElementIconStand', PipelineElementIconStandComponent)
    .component('pipelineElementOptions', PipelineElementOptionsComponent)
    .component('pipelineElementDocumentation', PipelineElementDocumentationComponent)
    .service('EditorDialogManager', EditorDialogManager)
    .service('PipelineElementRecommendationService', PipelineElementRecommendationService)
    .service('PipelineValidationService', PipelineValidationService)
    .name;
