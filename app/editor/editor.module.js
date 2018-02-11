import angular from 'npm/angular';

import spServices from '../services/services.module';

import flowtype from 'legacy/flowtype';
import circleMenu from 'legacy/jQuery.circleMenu';
import jqueryPanzoom from 'npm/jquery.panzoom';
import bootstrap from 'npm/bootstrap';
import angulerUiSortable from 'npm/angular-ui-sortable';
import angulerUiBootstrap from 'npm/angular-ui-bootstrap';
import 'npm/angular-trix';

import {EditorCtrl} from './editor.controller';
import myDataBind from './my-data-bind.directive';
import imageBind  from './image-bind.directive';
//import capitalize from './capitalize.filter';
import displayRecommendedFilter from './display-recommended.filter';
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
import {EditorDialogManager} from './editor-dialog-manager.service';

export default angular.module('sp.editor', [spServices, 'angularTrix'])
    .controller('EditorCtrl', EditorCtrl)
    .directive('myDataBind', myDataBind)
    .directive('imageBind', imageBind)
    //.directive('objectProvider', objectProvider)
    //.filter('capitalize', objectProvider)
    .filter('displayRecommendedFilter', displayRecommendedFilter)
    .component('any', AnyComponent)
    .component('customOutput', CustomOutputComponent)
    .component('domainConceptInput', DomainConceptComponent)
    .component('freetext', FreeTextComponent)
    .component('mappingPropertyNary', MappingNaryComponent)
    .component('mappingPropertyUnary', MappingUnaryComponent)
    .component('matchingProperty', MatchingPropertyComponent)
    .component('oneof', OneOfComponent)
    .component('replaceOutput', ReplaceOutputComponent)
    .component('multipleValueInput', MultipleValueInputComponent)
    .component('collectionStaticProperty', CollectionComponent)
    .component('customizeDialog', CustomizeDialogComponent)
    .component('topicSelectionDialog', TopicSelectionDialogComponent)
    .component('pipeline', PipelineComponent)
    .service('EditorDialogManager', EditorDialogManager)
    .name;
