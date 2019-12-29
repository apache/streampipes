/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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

import {SensorsCtrl} from './sensors.controller';
import {startsWithLetter} from './starts-with-letter.filter';

import {DeploymentTypeComponent} from './components/deployment/deployment-type.component'
import {DeploymentComponent} from './components/deployment/deployment.component'

import {AdvancedSettingsComponent} from './components/general/advanced-settings.component'
import {CollapsibleComponent} from './components/general/collapsible.component'
import {OptionsComponent} from './components/general/options.component'
import {ValueSpecificationComponent} from './components/general/value-specification.component'

import {GeneratedElementDescriptionComponent} from './components/generated-element/generated-element-description.component'
import {GeneratedElementImplementationComponent} from './components/generated-element/generated-element-implementation.component'

import {SupportedGroundingComponent} from './components/grounding/supported-grounding.component'
import {TransportFormatComponent} from './components/grounding/transport-format.component'
import {TransportProtocolComponent} from './components/grounding/transport-protocol.component'

import {OutputStrategyComponent} from './components/output/output-strategy.component'

import {DatatypePropertyComponent} from './components/property/datatype-property.component'
import {DomainConceptPropertyComponent} from './components/property/domain-concept-property.component'
import {DomainPropertyComponent} from './components/property/domain-property.component'
import {EventPropertiesComponent} from './components/property/event-properties.component'
import {PropertyRestrictionComponent} from './components/property/property-restriction.component'
import {RequiredPropertyValuesComponent} from './components/property/required-property-values.component'
import {StaticPropertiesComponent} from './components/property/static-properties.component'

import {PropertyQualityDefinitionsComponent} from './components/quality/property-quality-definitions.component'
import {StreamQualityDefinitionsComponent} from './components/quality/stream-quality-definitions.component'

import {StreamRestrictionComponent} from './components/restriction/stream-restriction.component'

import {SepaBasicsComponent} from './components/sepa/sepa-basics.component'
import {SepaStreamDetailComponent} from './components/sepa/sepa-stream-detail.component'

import {MeasurementUnitComponent} from './components/unit/measurement-unit.component'


export default angular.module('sp.sensors', [spServices])
	.controller('SensorsCtrl', SensorsCtrl)
	.filter('startsWithLetter', startsWithLetter)
	.component('deploymentType', DeploymentTypeComponent)
	.component('deployment', DeploymentComponent)

	.component('advancedSettings', AdvancedSettingsComponent)
	.component('collapsible', CollapsibleComponent)
	.component('options', OptionsComponent)
	.component('valueSpecification', ValueSpecificationComponent)

	.component('generatedElementDescription', GeneratedElementDescriptionComponent)
	.component('generatedElementImplementation', GeneratedElementImplementationComponent)

	.component('supportedGrounding', SupportedGroundingComponent)
	.component('transportFormat', TransportFormatComponent)
	.component('transportProtocol', TransportProtocolComponent)

	.component('outputStrategy', OutputStrategyComponent)

	.component('datatypeProperty', DatatypePropertyComponent )
	.component('domainConceptProperty', DomainConceptPropertyComponent)
	.component('domainProperty', DomainPropertyComponent)
	.component('eventProperties', EventPropertiesComponent)
	.component('propertyRestriction', PropertyRestrictionComponent)
	.component('requiredPropertyValues', RequiredPropertyValuesComponent)
	.component('staticProperties', StaticPropertiesComponent)

	.component('propertyQualityDefinitions', PropertyQualityDefinitionsComponent)
	.component('streamQualityDefinitions', StreamQualityDefinitionsComponent)

	.component('streamRestriction', StreamRestrictionComponent)

	.component('sepaBasics', SepaBasicsComponent)
	.component('sepaStreamDetail', SepaStreamDetailComponent)

	.component('measurementUnit', MeasurementUnitComponent)
.name;
