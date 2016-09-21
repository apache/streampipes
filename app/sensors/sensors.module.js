import angular from 'npm/angular';

import spServices from '../services/services.module';

import SensorsCtrl from './sensors.controller';
import startsWithLetter from './starts-with-letter.filter';

import deploymentType from './directives/deployment/deployment-type.directive'
import deployment from './directives/deployment/deployment.directive'

import advancedSettings from './directives/general/advanced-settings.directive'
import collapsible from './directives/general/collapsible.directive'
import nagPrism from './directives/general/nag-prism.directive'
import options from './directives/general/options.directive'
import valueSpecification from './directives/general/value-specification.directive'

import generatedElementDescription from './directives/generated-element/generated-element-description.directive'
import generatedElementImplementation from './directives/generated-element/generated-element-implementation.directive'

import supportedGrounding from './directives/grounding/supported-grounding.directive'
import transportFormat from './directives/grounding/transport-format.directive'
import transportProtocol from './directives/grounding/transport-protocol.directive'

import outputStrategy from './directives/output/output-strategy.directive'

import datatypeProperty from './directives/property/datatype-property.directive'
import domainConceptProperty from './directives/property/domain-concept-property.directive'
import domainProperty from './directives/property/domain-property.directive'
import eventProperties from './directives/property/event-properties.directive'
import propertyRestriction from './directives/property/property-restriction.directive'
import requiredPropertyValues from './directives/property/required-property-values.directive'
import staticProperties from './directives/property/static-properties.directive'

import propertyQualityDefinitions from './directives/quality/property-quality-definitions.directive'
import streamQualityDefinitions from './directives/quality/stream-quality-definitions.directive'

import streamRestriction from './directives/restriction/stream-restriction.directive'

import sepaBasics from './directives/sepa/sepa-basics.directive'
import sepaStreamDetail from './directives/sepa/sepa-stream-detail.directive'

import measurementUnit from './directives/unit/measurement-unit.directive'


export default angular.module('sp.sensors', [spServices])
	.controller('SensorsCtrl', SensorsCtrl)
	.filter('startsWithLetter', startsWithLetter)
	.directive('deploymentType', deploymentType)
	.directive('deployment', deployment)

	.directive('advancedSettings', advancedSettings)
	.directive('collapsible', collapsible)
	.directive('nagPrism', nagPrism)
	.directive('options', options)
	.directive('valueSpecification', valueSpecification)

	.directive('generatedElementDescription', generatedElementDescription)
	.directive('generatedElementImplementation', generatedElementImplementation)

	.directive('supportedGrounding', supportedGrounding)
	.directive('transportFormat', transportFormat)
	.directive('transportProtocol', transportProtocol)

	.directive('outputStrategy', outputStrategy)

	.directive('datatypeProperty', datatypeProperty )
	.directive('domainConceptProperty', domainConceptProperty)
	.directive('domainProperty', domainProperty)
	.directive('eventProperties', eventProperties)
	.directive('propertyRestriction', propertyRestriction)
	.directive('requiredPropertyValues', requiredPropertyValues)
	.directive('staticProperties', staticProperties)

	.directive('propertyQualityDefinitions', propertyQualityDefinitions)
	.directive('streamQualityDefinitions', streamQualityDefinitions)

	.directive('streamRestriction', streamRestriction)

	.directive('sepaBasics', sepaBasics)
	.directive('sepaStreamDetail', sepaStreamDetail)

	.directive('measurementUnit', measurementUnit)
.name;
