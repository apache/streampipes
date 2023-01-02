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

/* tslint:disable */
/* eslint-disable */
// @ts-nocheck
// Generated using typescript-generator version 2.27.744 on 2022-10-23 22:36:57.

export class AbstractStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.base.AbstractStreamPipesEntity'
        | 'org.apache.streampipes.model.base.NamedStreamPipesEntity'
        | 'org.apache.streampipes.model.connect.adapter.AdapterDescription'
        | 'org.apache.streampipes.model.connect.adapter.AdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.AdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.grounding.ProtocolDescription'
        | 'org.apache.streampipes.model.graph.DataSourceDescription'
        | 'org.apache.streampipes.model.template.PipelineTemplateDescription'
        | 'org.apache.streampipes.model.connect.grounding.FormatDescription'
        | 'org.apache.streampipes.model.SpDataStream'
        | 'org.apache.streampipes.model.SpDataSet'
        | 'org.apache.streampipes.model.base.InvocableStreamPipesEntity'
        | 'org.apache.streampipes.model.graph.DataProcessorInvocation'
        | 'org.apache.streampipes.model.graph.DataSinkInvocation'
        | 'org.apache.streampipes.model.base.UnnamedStreamPipesEntity'
        | 'org.apache.streampipes.model.connect.guess.GuessSchema'
        | 'org.apache.streampipes.model.connect.rules.TransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.StreamTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription'
        | 'org.apache.streampipes.model.dashboard.DashboardWidgetSettings'
        | 'org.apache.streampipes.model.datalake.DataLakeMeasure'
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsRequest'
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsResponse'
        | 'org.apache.streampipes.model.staticproperty.StaticProperty'
        | 'org.apache.streampipes.model.staticproperty.CodeInputStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.CollectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.DomainStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.FileStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.FreeTextStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.MatchingStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.SecretStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternative'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyGroup'
        | 'org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.SelectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.AnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyNary'
        | 'org.apache.streampipes.model.template.PipelineTemplateInvocation'
        | 'org.apache.streampipes.model.ApplicationLink'
        | 'org.apache.streampipes.model.grounding.EventGrounding'
        | 'org.apache.streampipes.model.schema.EventSchema'
        | 'org.apache.streampipes.model.template.BoundPipelineElement'
        | 'org.apache.streampipes.model.grounding.TransportProtocol'
        | 'org.apache.streampipes.model.grounding.JmsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.KafkaTransportProtocol'
        | 'org.apache.streampipes.model.grounding.MqttTransportProtocol'
        | 'org.apache.streampipes.model.grounding.NatsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.TransportFormat'
        | 'org.apache.streampipes.model.quality.EventStreamQualityRequirement'
        | 'org.apache.streampipes.model.quality.MeasurementCapability'
        | 'org.apache.streampipes.model.quality.MeasurementObject'
        | 'org.apache.streampipes.model.schema.EventProperty'
        | 'org.apache.streampipes.model.schema.EventPropertyList'
        | 'org.apache.streampipes.model.schema.EventPropertyNested'
        | 'org.apache.streampipes.model.schema.EventPropertyPrimitive'
        | 'org.apache.streampipes.model.output.OutputStrategy'
        | 'org.apache.streampipes.model.output.AppendOutputStrategy'
        | 'org.apache.streampipes.model.output.CustomOutputStrategy'
        | 'org.apache.streampipes.model.output.CustomTransformOutputStrategy'
        | 'org.apache.streampipes.model.output.FixedOutputStrategy'
        | 'org.apache.streampipes.model.output.KeepOutputStrategy'
        | 'org.apache.streampipes.model.output.ListOutputStrategy'
        | 'org.apache.streampipes.model.output.TransformOutputStrategy'
        | 'org.apache.streampipes.model.output.UserDefinedOutputStrategy'
        | 'org.apache.streampipes.model.monitoring.ElementStatusInfoSettings'
        | 'org.apache.streampipes.model.staticproperty.Option'
        | 'org.apache.streampipes.model.staticproperty.SupportedProperty'
        | 'org.apache.streampipes.model.staticproperty.PropertyValueSpecification'
        | 'org.apache.streampipes.model.grounding.TopicDefinition'
        | 'org.apache.streampipes.model.grounding.SimpleTopicDefinition'
        | 'org.apache.streampipes.model.grounding.WildcardTopicDefinition'
        | 'org.apache.streampipes.model.quality.MeasurementProperty'
        | 'org.apache.streampipes.model.quality.EventStreamQualityDefinition'
        | 'org.apache.streampipes.model.quality.Frequency'
        | 'org.apache.streampipes.model.quality.Latency'
        | 'org.apache.streampipes.model.quality.EventPropertyQualityDefinition'
        | 'org.apache.streampipes.model.quality.Accuracy'
        | 'org.apache.streampipes.model.quality.MeasurementRange'
        | 'org.apache.streampipes.model.quality.Precision'
        | 'org.apache.streampipes.model.quality.Resolution'
        | 'org.apache.streampipes.model.quality.EventPropertyQualityRequirement'
        | 'org.apache.streampipes.model.output.PropertyRenameRule'
        | 'org.apache.streampipes.model.schema.ValueSpecification'
        | 'org.apache.streampipes.model.schema.QuantitativeValue'
        | 'org.apache.streampipes.model.schema.Enumeration'
        | 'org.apache.streampipes.model.output.TransformOperation'
        | 'org.apache.streampipes.model.grounding.WildcardTopicMapping';
    'elementId': string;

    static 'fromData'(
        data: AbstractStreamPipesEntity,
        target?: AbstractStreamPipesEntity,
    ): AbstractStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new AbstractStreamPipesEntity();
        instance['@class'] = data['@class'];
        instance.elementId = data.elementId;
        return instance;
    }
}

export class UnnamedStreamPipesEntity extends AbstractStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.base.UnnamedStreamPipesEntity'
        | 'org.apache.streampipes.model.connect.guess.GuessSchema'
        | 'org.apache.streampipes.model.connect.rules.TransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.StreamTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription'
        | 'org.apache.streampipes.model.dashboard.DashboardWidgetSettings'
        | 'org.apache.streampipes.model.datalake.DataLakeMeasure'
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsRequest'
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsResponse'
        | 'org.apache.streampipes.model.staticproperty.StaticProperty'
        | 'org.apache.streampipes.model.staticproperty.CodeInputStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.CollectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.DomainStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.FileStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.FreeTextStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.MatchingStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.SecretStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternative'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyGroup'
        | 'org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.SelectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.AnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyNary'
        | 'org.apache.streampipes.model.template.PipelineTemplateInvocation'
        | 'org.apache.streampipes.model.ApplicationLink'
        | 'org.apache.streampipes.model.grounding.EventGrounding'
        | 'org.apache.streampipes.model.schema.EventSchema'
        | 'org.apache.streampipes.model.template.BoundPipelineElement'
        | 'org.apache.streampipes.model.grounding.TransportProtocol'
        | 'org.apache.streampipes.model.grounding.JmsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.KafkaTransportProtocol'
        | 'org.apache.streampipes.model.grounding.MqttTransportProtocol'
        | 'org.apache.streampipes.model.grounding.NatsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.TransportFormat'
        | 'org.apache.streampipes.model.quality.EventStreamQualityRequirement'
        | 'org.apache.streampipes.model.quality.MeasurementCapability'
        | 'org.apache.streampipes.model.quality.MeasurementObject'
        | 'org.apache.streampipes.model.schema.EventProperty'
        | 'org.apache.streampipes.model.schema.EventPropertyList'
        | 'org.apache.streampipes.model.schema.EventPropertyNested'
        | 'org.apache.streampipes.model.schema.EventPropertyPrimitive'
        | 'org.apache.streampipes.model.output.OutputStrategy'
        | 'org.apache.streampipes.model.output.AppendOutputStrategy'
        | 'org.apache.streampipes.model.output.CustomOutputStrategy'
        | 'org.apache.streampipes.model.output.CustomTransformOutputStrategy'
        | 'org.apache.streampipes.model.output.FixedOutputStrategy'
        | 'org.apache.streampipes.model.output.KeepOutputStrategy'
        | 'org.apache.streampipes.model.output.ListOutputStrategy'
        | 'org.apache.streampipes.model.output.TransformOutputStrategy'
        | 'org.apache.streampipes.model.output.UserDefinedOutputStrategy'
        | 'org.apache.streampipes.model.monitoring.ElementStatusInfoSettings'
        | 'org.apache.streampipes.model.staticproperty.Option'
        | 'org.apache.streampipes.model.staticproperty.SupportedProperty'
        | 'org.apache.streampipes.model.staticproperty.PropertyValueSpecification'
        | 'org.apache.streampipes.model.grounding.TopicDefinition'
        | 'org.apache.streampipes.model.grounding.SimpleTopicDefinition'
        | 'org.apache.streampipes.model.grounding.WildcardTopicDefinition'
        | 'org.apache.streampipes.model.quality.MeasurementProperty'
        | 'org.apache.streampipes.model.quality.EventStreamQualityDefinition'
        | 'org.apache.streampipes.model.quality.Frequency'
        | 'org.apache.streampipes.model.quality.Latency'
        | 'org.apache.streampipes.model.quality.EventPropertyQualityDefinition'
        | 'org.apache.streampipes.model.quality.Accuracy'
        | 'org.apache.streampipes.model.quality.MeasurementRange'
        | 'org.apache.streampipes.model.quality.Precision'
        | 'org.apache.streampipes.model.quality.Resolution'
        | 'org.apache.streampipes.model.quality.EventPropertyQualityRequirement'
        | 'org.apache.streampipes.model.output.PropertyRenameRule'
        | 'org.apache.streampipes.model.schema.ValueSpecification'
        | 'org.apache.streampipes.model.schema.QuantitativeValue'
        | 'org.apache.streampipes.model.schema.Enumeration'
        | 'org.apache.streampipes.model.output.TransformOperation'
        | 'org.apache.streampipes.model.grounding.WildcardTopicMapping';

    static 'fromData'(
        data: UnnamedStreamPipesEntity,
        target?: UnnamedStreamPipesEntity,
    ): UnnamedStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new UnnamedStreamPipesEntity();
        super.fromData(data, instance);
        return instance;
    }
}

export class MeasurementProperty extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.quality.MeasurementProperty'
        | 'org.apache.streampipes.model.quality.EventStreamQualityDefinition'
        | 'org.apache.streampipes.model.quality.Frequency'
        | 'org.apache.streampipes.model.quality.Latency'
        | 'org.apache.streampipes.model.quality.EventPropertyQualityDefinition'
        | 'org.apache.streampipes.model.quality.Accuracy'
        | 'org.apache.streampipes.model.quality.MeasurementRange'
        | 'org.apache.streampipes.model.quality.Precision'
        | 'org.apache.streampipes.model.quality.Resolution';

    static 'fromData'(
        data: MeasurementProperty,
        target?: MeasurementProperty,
    ): MeasurementProperty {
        if (!data) {
            return data;
        }
        const instance = target || new MeasurementProperty();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: MeasurementPropertyUnion,
    ): MeasurementPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.quality.EventPropertyQualityDefinition':
                return EventPropertyQualityDefinition.fromData(data);
            case 'org.apache.streampipes.model.quality.EventStreamQualityDefinition':
                return EventStreamQualityDefinition.fromData(data);
        }
    }
}

export class EventPropertyQualityDefinition extends MeasurementProperty {
    '@class':
        | 'org.apache.streampipes.model.quality.EventPropertyQualityDefinition'
        | 'org.apache.streampipes.model.quality.Accuracy'
        | 'org.apache.streampipes.model.quality.MeasurementRange'
        | 'org.apache.streampipes.model.quality.Precision'
        | 'org.apache.streampipes.model.quality.Resolution';

    static 'fromData'(
        data: EventPropertyQualityDefinition,
        target?: EventPropertyQualityDefinition,
    ): EventPropertyQualityDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyQualityDefinition();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: EventPropertyQualityDefinitionUnion,
    ): EventPropertyQualityDefinitionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.quality.Accuracy':
                return Accuracy.fromData(data);
            case 'org.apache.streampipes.model.quality.MeasurementRange':
                return MeasurementRange.fromData(data);
            case 'org.apache.streampipes.model.quality.Precision':
                return Precision.fromData(data);
            case 'org.apache.streampipes.model.quality.Resolution':
                return Resolution.fromData(data);
        }
    }
}

export class Accuracy extends EventPropertyQualityDefinition {
    '@class': 'org.apache.streampipes.model.quality.Accuracy';
    'quantityValue': number;

    static 'fromData'(data: Accuracy, target?: Accuracy): Accuracy {
        if (!data) {
            return data;
        }
        const instance = target || new Accuracy();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class NamedStreamPipesEntity extends AbstractStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.base.NamedStreamPipesEntity'
        | 'org.apache.streampipes.model.connect.adapter.AdapterDescription'
        | 'org.apache.streampipes.model.connect.adapter.AdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.AdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.grounding.ProtocolDescription'
        | 'org.apache.streampipes.model.graph.DataSourceDescription'
        | 'org.apache.streampipes.model.template.PipelineTemplateDescription'
        | 'org.apache.streampipes.model.connect.grounding.FormatDescription'
        | 'org.apache.streampipes.model.SpDataStream'
        | 'org.apache.streampipes.model.SpDataSet'
        | 'org.apache.streampipes.model.base.InvocableStreamPipesEntity'
        | 'org.apache.streampipes.model.graph.DataProcessorInvocation'
        | 'org.apache.streampipes.model.graph.DataSinkInvocation';
    '_rev': string;
    'appId': string;
    'applicationLinks': ApplicationLink[];
    'connectedTo': string[];
    'description': string;
    'dom': string;
    'iconUrl': string;
    'includedAssets': string[];
    'includedLocales': string[];
    'includesAssets': boolean;
    'includesLocales': boolean;
    'internallyManaged': boolean;
    'name': string;
    'uri': string;

    static 'fromData'(
        data: NamedStreamPipesEntity,
        target?: NamedStreamPipesEntity,
    ): NamedStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new NamedStreamPipesEntity();
        super.fromData(data, instance);
        instance.name = data.name;
        instance.description = data.description;
        instance.iconUrl = data.iconUrl;
        instance.appId = data.appId;
        instance.includesAssets = data.includesAssets;
        instance.includesLocales = data.includesLocales;
        instance.includedAssets = __getCopyArrayFn(__identity<string>())(
            data.includedAssets,
        );
        instance.includedLocales = __getCopyArrayFn(__identity<string>())(
            data.includedLocales,
        );
        instance.applicationLinks = __getCopyArrayFn(ApplicationLink.fromData)(
            data.applicationLinks,
        );
        instance.internallyManaged = data.internallyManaged;
        instance.connectedTo = __getCopyArrayFn(__identity<string>())(
            data.connectedTo,
        );
        instance.dom = data.dom;
        instance.uri = data.uri;
        instance._rev = data._rev;
        return instance;
    }
}

export class AdapterDescription extends NamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.connect.adapter.AdapterDescription'
        | 'org.apache.streampipes.model.connect.adapter.AdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.AdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription';
    'adapterType': string;
    'category': string[];
    'config': StaticPropertyUnion[];
    'correspondingDataStreamElementId': string;
    'correspondingServiceGroup': string;
    'createdAt': number;
    'eventGrounding': EventGrounding;
    'icon': string;
    'rules': TransformationRuleDescriptionUnion[];
    'schemaRules': TransformationRuleDescriptionUnion[];
    'selectedEndpointUrl': string;
    'streamRules': TransformationRuleDescriptionUnion[];
    'valueRules': TransformationRuleDescriptionUnion[];

    static 'fromData'(
        data: AdapterDescription,
        target?: AdapterDescription,
    ): AdapterDescription {
        if (!data) {
            return data;
        }
        const instance = target || new AdapterDescription();
        super.fromData(data, instance);
        instance.eventGrounding = EventGrounding.fromData(data.eventGrounding);
        instance.adapterType = data.adapterType;
        instance.icon = data.icon;
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.rules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.rules);
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        instance.createdAt = data.createdAt;
        instance.selectedEndpointUrl = data.selectedEndpointUrl;
        instance.correspondingServiceGroup = data.correspondingServiceGroup;
        instance.correspondingDataStreamElementId =
            data.correspondingDataStreamElementId;
        instance.streamRules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.streamRules);
        instance.schemaRules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.schemaRules);
        instance.valueRules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.valueRules);
        return instance;
    }

    static 'fromDataUnion'(
        data: AdapterDescriptionUnion,
    ): AdapterDescriptionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription':
                return GenericAdapterSetDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription':
                return GenericAdapterStreamDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription':
                return SpecificAdapterStreamDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription':
                return SpecificAdapterSetDescription.fromData(data);
        }
    }
}

export class AdapterEventPreview {
    inputData: { [index: string]: GuessTypeInfo };
    rules: TransformationRuleDescriptionUnion[];

    static fromData(
        data: AdapterEventPreview,
        target?: AdapterEventPreview,
    ): AdapterEventPreview {
        if (!data) {
            return data;
        }
        const instance = target || new AdapterEventPreview();
        instance.rules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.rules);
        instance.inputData = __getCopyObjectFn(GuessTypeInfo.fromData)(
            data.inputData,
        );
        return instance;
    }
}

export class AdapterSetDescription extends AdapterDescription {
    '@class':
        | 'org.apache.streampipes.model.connect.adapter.AdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription';
    'dataSet': SpDataSet;

    static 'fromData'(
        data: AdapterSetDescription,
        target?: AdapterSetDescription,
    ): AdapterSetDescription {
        if (!data) {
            return data;
        }
        const instance = target || new AdapterSetDescription();
        super.fromData(data, instance);
        instance.dataSet = SpDataSet.fromData(data.dataSet);
        return instance;
    }

    static 'fromDataUnion'(
        data: AdapterSetDescriptionUnion,
    ): AdapterSetDescriptionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription':
                return GenericAdapterSetDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription':
                return SpecificAdapterSetDescription.fromData(data);
        }
    }
}

export class AdapterStreamDescription extends AdapterDescription {
    '@class':
        | 'org.apache.streampipes.model.connect.adapter.AdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription'
        | 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription';
    'dataStream': SpDataStreamUnion;
    'running': boolean;

    static 'fromData'(
        data: AdapterStreamDescription,
        target?: AdapterStreamDescription,
    ): AdapterStreamDescription {
        if (!data) {
            return data;
        }
        const instance = target || new AdapterStreamDescription();
        super.fromData(data, instance);
        instance.dataStream = SpDataStream.fromDataUnion(data.dataStream);
        instance.running = data.running;
        return instance;
    }

    static 'fromDataUnion'(
        data: AdapterStreamDescriptionUnion,
    ): AdapterStreamDescriptionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription':
                return SpecificAdapterStreamDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription':
                return SpecificAdapterStreamDescription.fromData(data);
        }
    }
}

export class AdapterType {
    code: string;
    description: string;
    label: string;

    static fromData(data: AdapterType, target?: AdapterType): AdapterType {
        if (!data) {
            return data;
        }
        const instance = target || new AdapterType();
        instance.label = data.label;
        instance.description = data.description;
        instance.code = data.code;
        return instance;
    }
}

export class TransformationRuleDescription extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.connect.rules.TransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.StreamTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription';

    static 'fromData'(
        data: TransformationRuleDescription,
        target?: TransformationRuleDescription,
    ): TransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new TransformationRuleDescription();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: TransformationRuleDescriptionUnion,
    ): TransformationRuleDescriptionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription':
                return AddTimestampRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription':
                return AddValueTransformationRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription':
                return TimestampTranfsformationRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription':
                return UnitTransformRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription':
                return EventRateTransformationRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription':
                return RemoveDuplicatesTransformationRuleDescription.fromData(
                    data,
                );
            case 'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription':
                return CreateNestedRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription':
                return DeleteRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription':
                return RenameRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription':
                return MoveRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription':
                return ChangeDatatypeTransformationRuleDescription.fromData(
                    data,
                );
            case 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription':
                return CorrectionValueTransformationRuleDescription.fromData(
                    data,
                );
        }
    }
}

export class ValueTransformationRuleDescription extends TransformationRuleDescription {
    '@class':
        | 'org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription';

    static 'fromData'(
        data: ValueTransformationRuleDescription,
        target?: ValueTransformationRuleDescription,
    ): ValueTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new ValueTransformationRuleDescription();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: ValueTransformationRuleDescriptionUnion,
    ): ValueTransformationRuleDescriptionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription':
                return AddTimestampRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription':
                return AddValueTransformationRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription':
                return TimestampTranfsformationRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription':
                return UnitTransformRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription':
                return CorrectionValueTransformationRuleDescription.fromData(
                    data,
                );
        }
    }
}

export class AddTimestampRuleDescription extends ValueTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription';
    'runtimeKey': string;

    static 'fromData'(
        data: AddTimestampRuleDescription,
        target?: AddTimestampRuleDescription,
    ): AddTimestampRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new AddTimestampRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        return instance;
    }
}

export class AddValueTransformationRuleDescription extends ValueTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription';
    'runtimeKey': string;
    'staticValue': string;

    static 'fromData'(
        data: AddValueTransformationRuleDescription,
        target?: AddValueTransformationRuleDescription,
    ): AddValueTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new AddValueTransformationRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        instance.staticValue = data.staticValue;
        return instance;
    }
}

export class StaticProperty extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.staticproperty.StaticProperty'
        | 'org.apache.streampipes.model.staticproperty.CodeInputStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.CollectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.DomainStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.FileStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.FreeTextStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.MatchingStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.SecretStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternative'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives'
        | 'org.apache.streampipes.model.staticproperty.StaticPropertyGroup'
        | 'org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.SelectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.AnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyNary';
    'description': string;
    'index': number;
    'internalName': string;
    'label': string;
    'predefined': boolean;
    'staticPropertyType': StaticPropertyType;
    'valueRequired': boolean;

    static 'fromData'(
        data: StaticProperty,
        target?: StaticProperty,
    ): StaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new StaticProperty();
        super.fromData(data, instance);
        instance.index = data.index;
        instance.label = data.label;
        instance.description = data.description;
        instance.internalName = data.internalName;
        instance.valueRequired = data.valueRequired;
        instance.predefined = data.predefined;
        instance.staticPropertyType = data.staticPropertyType;
        return instance;
    }

    static 'fromDataUnion'(data: StaticPropertyUnion): StaticPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.staticproperty.AnyStaticProperty':
                return AnyStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.CodeInputStaticProperty':
                return CodeInputStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.CollectionStaticProperty':
                return CollectionStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty':
                return ColorPickerStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.DomainStaticProperty':
                return DomainStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.FileStaticProperty':
                return FileStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.FreeTextStaticProperty':
                return FreeTextStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary':
                return MappingPropertyUnary.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.MappingPropertyNary':
                return MappingPropertyNary.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.MatchingStaticProperty':
                return MatchingStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty':
                return OneOfStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty':
                return RuntimeResolvableAnyStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty':
                return RuntimeResolvableOneOfStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty':
                return RuntimeResolvableTreeInputStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.SecretStaticProperty':
                return SecretStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternative':
                return StaticPropertyAlternative.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives':
                return StaticPropertyAlternatives.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.StaticPropertyGroup':
                return StaticPropertyGroup.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty':
                return SlideToggleStaticProperty.fromData(data);
        }
    }
}

export class SelectionStaticProperty extends StaticProperty {
    '@class':
        | 'org.apache.streampipes.model.staticproperty.SelectionStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.AnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty';
    'horizontalRendering': boolean;
    'options': Option[];

    static 'fromData'(
        data: SelectionStaticProperty,
        target?: SelectionStaticProperty,
    ): SelectionStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new SelectionStaticProperty();
        super.fromData(data, instance);
        instance.options = __getCopyArrayFn(Option.fromData)(data.options);
        instance.horizontalRendering = data.horizontalRendering;
        return instance;
    }

    static 'fromDataUnion'(
        data: SelectionStaticPropertyUnion,
    ): SelectionStaticPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.staticproperty.AnyStaticProperty':
                return AnyStaticProperty.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty':
                return OneOfStaticProperty.fromData(data);
        }
    }
}

export class AnyStaticProperty extends SelectionStaticProperty {
    '@class':
        | 'org.apache.streampipes.model.staticproperty.AnyStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty';

    static 'fromData'(
        data: AnyStaticProperty,
        target?: AnyStaticProperty,
    ): AnyStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new AnyStaticProperty();
        super.fromData(data, instance);
        return instance;
    }
}

export class OutputStrategy extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.output.OutputStrategy'
        | 'org.apache.streampipes.model.output.AppendOutputStrategy'
        | 'org.apache.streampipes.model.output.CustomOutputStrategy'
        | 'org.apache.streampipes.model.output.CustomTransformOutputStrategy'
        | 'org.apache.streampipes.model.output.FixedOutputStrategy'
        | 'org.apache.streampipes.model.output.KeepOutputStrategy'
        | 'org.apache.streampipes.model.output.ListOutputStrategy'
        | 'org.apache.streampipes.model.output.TransformOutputStrategy'
        | 'org.apache.streampipes.model.output.UserDefinedOutputStrategy';
    'name': string;
    'renameRules': PropertyRenameRule[];

    static 'fromData'(
        data: OutputStrategy,
        target?: OutputStrategy,
    ): OutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new OutputStrategy();
        super.fromData(data, instance);
        instance.name = data.name;
        instance.renameRules = __getCopyArrayFn(PropertyRenameRule.fromData)(
            data.renameRules,
        );
        return instance;
    }

    static 'fromDataUnion'(data: OutputStrategyUnion): OutputStrategyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.output.AppendOutputStrategy':
                return AppendOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.CustomOutputStrategy':
                return CustomOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.CustomTransformOutputStrategy':
                return CustomTransformOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.FixedOutputStrategy':
                return FixedOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.KeepOutputStrategy':
                return KeepOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.ListOutputStrategy':
                return ListOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.TransformOutputStrategy':
                return TransformOutputStrategy.fromData(data);
            case 'org.apache.streampipes.model.output.UserDefinedOutputStrategy':
                return UserDefinedOutputStrategy.fromData(data);
        }
    }
}

export class AppendOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.AppendOutputStrategy';
    'eventProperties': EventPropertyUnion[];

    static 'fromData'(
        data: AppendOutputStrategy,
        target?: AppendOutputStrategy,
    ): AppendOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new AppendOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
        return instance;
    }
}

export class ApplicationLink extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.ApplicationLink';
    'applicationDescription': string;
    'applicationIconUrl': string;
    'applicationLinkType': string;
    'applicationName': string;
    'applicationUrl': string;

    static 'fromData'(
        data: ApplicationLink,
        target?: ApplicationLink,
    ): ApplicationLink {
        if (!data) {
            return data;
        }
        const instance = target || new ApplicationLink();
        super.fromData(data, instance);
        instance.applicationName = data.applicationName;
        instance.applicationDescription = data.applicationDescription;
        instance.applicationUrl = data.applicationUrl;
        instance.applicationIconUrl = data.applicationIconUrl;
        instance.applicationLinkType = data.applicationLinkType;
        return instance;
    }
}

export class AssetExportConfiguration {
    adapters: ExportItem[];
    assetId: string;
    assetName: string;
    assets: ExportItem[];
    dashboards: ExportItem[];
    dataLakeMeasures: ExportItem[];
    dataSources: ExportItem[];
    dataViews: ExportItem[];
    files: ExportItem[];
    overrideBrokerSettings: boolean;
    pipelines: ExportItem[];

    static fromData(
        data: AssetExportConfiguration,
        target?: AssetExportConfiguration,
    ): AssetExportConfiguration {
        if (!data) {
            return data;
        }
        const instance = target || new AssetExportConfiguration();
        instance.assetId = data.assetId;
        instance.assetName = data.assetName;
        instance.assets = __getCopyArrayFn(ExportItem.fromData)(data.assets);
        instance.adapters = __getCopyArrayFn(ExportItem.fromData)(
            data.adapters,
        );
        instance.dashboards = __getCopyArrayFn(ExportItem.fromData)(
            data.dashboards,
        );
        instance.dataViews = __getCopyArrayFn(ExportItem.fromData)(
            data.dataViews,
        );
        instance.dataLakeMeasures = __getCopyArrayFn(ExportItem.fromData)(
            data.dataLakeMeasures,
        );
        instance.dataSources = __getCopyArrayFn(ExportItem.fromData)(
            data.dataSources,
        );
        instance.pipelines = __getCopyArrayFn(ExportItem.fromData)(
            data.pipelines,
        );
        instance.files = __getCopyArrayFn(ExportItem.fromData)(data.files);
        instance.overrideBrokerSettings = data.overrideBrokerSettings;
        return instance;
    }
}

export class BoundPipelineElement extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.template.BoundPipelineElement';
    'connectedTo': BoundPipelineElement[];
    'pipelineElementTemplate': InvocableStreamPipesEntity;

    static 'fromData'(
        data: BoundPipelineElement,
        target?: BoundPipelineElement,
    ): BoundPipelineElement {
        if (!data) {
            return data;
        }
        const instance = target || new BoundPipelineElement();
        super.fromData(data, instance);
        instance.pipelineElementTemplate = InvocableStreamPipesEntity.fromData(
            data.pipelineElementTemplate,
        );
        instance.connectedTo = __getCopyArrayFn(BoundPipelineElement.fromData)(
            data.connectedTo,
        );
        return instance;
    }
}

export class CanvasPosition {
    x: number;
    y: number;

    static fromData(
        data: CanvasPosition,
        target?: CanvasPosition,
    ): CanvasPosition {
        if (!data) {
            return data;
        }
        const instance = target || new CanvasPosition();
        instance.x = data.x;
        instance.y = data.y;
        return instance;
    }
}

export class Category {
    _id: string;
    _rev: string;
    internalName: string;
    name: string;
    superLabel: string;
    superLabelId: string;

    static fromData(data: Category, target?: Category): Category {
        if (!data) {
            return data;
        }
        const instance = target || new Category();
        instance.name = data.name;
        instance.internalName = data.internalName;
        instance.superLabelId = data.superLabelId;
        instance._id = data._id;
        instance._rev = data._rev;
        instance.superLabel = data.superLabel;
        return instance;
    }
}

export class ChangeDatatypeTransformationRuleDescription extends ValueTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription';
    'originalDatatypeXsd': string;
    'runtimeKey': string;
    'targetDatatypeXsd': string;

    static 'fromData'(
        data: ChangeDatatypeTransformationRuleDescription,
        target?: ChangeDatatypeTransformationRuleDescription,
    ): ChangeDatatypeTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance =
            target || new ChangeDatatypeTransformationRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        instance.originalDatatypeXsd = data.originalDatatypeXsd;
        instance.targetDatatypeXsd = data.targetDatatypeXsd;
        return instance;
    }
}

export class CodeInputStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.CodeInputStaticProperty';
    'codeTemplate': string;
    'language': string;
    'value': string;

    static 'fromData'(
        data: CodeInputStaticProperty,
        target?: CodeInputStaticProperty,
    ): CodeInputStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new CodeInputStaticProperty();
        super.fromData(data, instance);
        instance.language = data.language;
        instance.codeTemplate = data.codeTemplate;
        instance.value = data.value;
        return instance;
    }
}

export class CollectionStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.CollectionStaticProperty';
    'memberType': string;
    'members': StaticPropertyUnion[];
    'staticPropertyTemplate': StaticPropertyUnion;

    static 'fromData'(
        data: CollectionStaticProperty,
        target?: CollectionStaticProperty,
    ): CollectionStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new CollectionStaticProperty();
        super.fromData(data, instance);
        instance.staticPropertyTemplate = StaticProperty.fromDataUnion(
            data.staticPropertyTemplate,
        );
        instance.members = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.members,
        );
        instance.memberType = data.memberType;
        return instance;
    }
}

export class ColorPickerStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty';
    'selectedColor': string;

    static 'fromData'(
        data: ColorPickerStaticProperty,
        target?: ColorPickerStaticProperty,
    ): ColorPickerStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new ColorPickerStaticProperty();
        super.fromData(data, instance);
        instance.selectedColor = data.selectedColor;
        return instance;
    }
}

export class MessagesInfo {
    groupId: string;
    topicName: string;

    static fromData(data: MessagesInfo, target?: MessagesInfo): MessagesInfo {
        if (!data) {
            return data;
        }
        const instance = target || new MessagesInfo();
        instance.topicName = data.topicName;
        instance.groupId = data.groupId;
        return instance;
    }
}

export class ConsumedMessagesInfo extends MessagesInfo {
    consumedMessagesSincePipelineStart: number;
    lag: number;
    totalMessagesSincePipelineStart: number;

    static fromData(
        data: ConsumedMessagesInfo,
        target?: ConsumedMessagesInfo,
    ): ConsumedMessagesInfo {
        if (!data) {
            return data;
        }
        const instance = target || new ConsumedMessagesInfo();
        super.fromData(data, instance);
        instance.totalMessagesSincePipelineStart =
            data.totalMessagesSincePipelineStart;
        instance.consumedMessagesSincePipelineStart =
            data.consumedMessagesSincePipelineStart;
        instance.lag = data.lag;
        return instance;
    }
}

export class CorrectionValueTransformationRuleDescription extends ValueTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription';
    'correctionValue': number;
    'operator': string;
    'runtimeKey': string;

    static 'fromData'(
        data: CorrectionValueTransformationRuleDescription,
        target?: CorrectionValueTransformationRuleDescription,
    ): CorrectionValueTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance =
            target || new CorrectionValueTransformationRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        instance.correctionValue = data.correctionValue;
        instance.operator = data.operator;
        return instance;
    }
}

export class SchemaTransformationRuleDescription extends TransformationRuleDescription {
    '@class':
        | 'org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription';

    static 'fromData'(
        data: SchemaTransformationRuleDescription,
        target?: SchemaTransformationRuleDescription,
    ): SchemaTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new SchemaTransformationRuleDescription();
        super.fromData(data, instance);
        return instance;
    }
}

export class CreateNestedRuleDescription extends SchemaTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription';
    'runtimeKey': string;

    static 'fromData'(
        data: CreateNestedRuleDescription,
        target?: CreateNestedRuleDescription,
    ): CreateNestedRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new CreateNestedRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        return instance;
    }
}

export class CustomOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.CustomOutputStrategy';
    'availablePropertyKeys': string[];
    'outputRight': boolean;
    'selectedPropertyKeys': string[];

    static 'fromData'(
        data: CustomOutputStrategy,
        target?: CustomOutputStrategy,
    ): CustomOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new CustomOutputStrategy();
        super.fromData(data, instance);
        instance.selectedPropertyKeys = __getCopyArrayFn(__identity<string>())(
            data.selectedPropertyKeys,
        );
        instance.outputRight = data.outputRight;
        instance.availablePropertyKeys = __getCopyArrayFn(__identity<string>())(
            data.availablePropertyKeys,
        );
        return instance;
    }
}

export class CustomTransformOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.CustomTransformOutputStrategy';
    'eventProperties': EventPropertyUnion[];

    static 'fromData'(
        data: CustomTransformOutputStrategy,
        target?: CustomTransformOutputStrategy,
    ): CustomTransformOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new CustomTransformOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
        return instance;
    }
}

export class DashboardEntity {
    _id: string;
    _rev: string;

    static fromData(
        data: DashboardEntity,
        target?: DashboardEntity,
    ): DashboardEntity {
        if (!data) {
            return data;
        }
        const instance = target || new DashboardEntity();
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class DashboardItem {
    cols: number;
    component: string;
    id: string;
    name: string;
    rows: number;
    settings: string[];
    x: number;
    y: number;

    static fromData(
        data: DashboardItem,
        target?: DashboardItem,
    ): DashboardItem {
        if (!data) {
            return data;
        }
        const instance = target || new DashboardItem();
        instance.id = data.id;
        instance.name = data.name;
        instance.component = data.component;
        instance.settings = __getCopyArrayFn(__identity<string>())(
            data.settings,
        );
        instance.cols = data.cols;
        instance.rows = data.rows;
        instance.x = data.x;
        instance.y = data.y;
        return instance;
    }
}

export class DashboardModel {
    _id: string;
    _rev: string;
    dashboardGeneralSettings: { [index: string]: any };
    dashboardTimeSettings: { [index: string]: any };
    description: string;
    displayHeader: boolean;
    id: string;
    name: string;
    widgets: DashboardItem[];

    static fromData(
        data: DashboardModel,
        target?: DashboardModel,
    ): DashboardModel {
        if (!data) {
            return data;
        }
        const instance = target || new DashboardModel();
        instance.id = data.id;
        instance.name = data.name;
        instance.description = data.description;
        instance.displayHeader = data.displayHeader;
        instance.dashboardTimeSettings = __getCopyObjectFn(__identity<any>())(
            data.dashboardTimeSettings,
        );
        instance.dashboardGeneralSettings = __getCopyObjectFn(
            __identity<any>(),
        )(data.dashboardGeneralSettings);
        instance.widgets = __getCopyArrayFn(DashboardItem.fromData)(
            data.widgets,
        );
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class DashboardWidgetModel extends DashboardEntity {
    dashboardWidgetSettings: DashboardWidgetSettings;
    pipelineId: string;
    visualizationName: string;
    widgetId: string;
    widgetType: string;

    static fromData(
        data: DashboardWidgetModel,
        target?: DashboardWidgetModel,
    ): DashboardWidgetModel {
        if (!data) {
            return data;
        }
        const instance = target || new DashboardWidgetModel();
        super.fromData(data, instance);
        instance.widgetId = data.widgetId;
        instance.widgetType = data.widgetType;
        instance.dashboardWidgetSettings = DashboardWidgetSettings.fromData(
            data.dashboardWidgetSettings,
        );
        instance.pipelineId = data.pipelineId;
        instance.visualizationName = data.visualizationName;
        return instance;
    }
}

export class DashboardWidgetSettings extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.dashboard.DashboardWidgetSettings';
    'config': StaticPropertyUnion[];
    'requiredSchema': EventSchema;
    'widgetDescription': string;
    'widgetIconName': string;
    'widgetLabel': string;
    'widgetName': string;

    static 'fromData'(
        data: DashboardWidgetSettings,
        target?: DashboardWidgetSettings,
    ): DashboardWidgetSettings {
        if (!data) {
            return data;
        }
        const instance = target || new DashboardWidgetSettings();
        super.fromData(data, instance);
        instance.widgetLabel = data.widgetLabel;
        instance.widgetName = data.widgetName;
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.requiredSchema = EventSchema.fromData(data.requiredSchema);
        instance.widgetIconName = data.widgetIconName;
        instance.widgetDescription = data.widgetDescription;
        return instance;
    }
}

export class DataExplorerWidgetModel extends DashboardEntity {
    baseAppearanceConfig: { [index: string]: any };
    dataConfig: { [index: string]: any };
    measureName: string;
    pipelineId: string;
    visualizationConfig: { [index: string]: any };
    widgetId: string;
    widgetType: string;

    static fromData(
        data: DataExplorerWidgetModel,
        target?: DataExplorerWidgetModel,
    ): DataExplorerWidgetModel {
        if (!data) {
            return data;
        }
        const instance = target || new DataExplorerWidgetModel();
        super.fromData(data, instance);
        instance.widgetId = data.widgetId;
        instance.widgetType = data.widgetType;
        instance.baseAppearanceConfig = __getCopyObjectFn(__identity<any>())(
            data.baseAppearanceConfig,
        );
        instance.visualizationConfig = __getCopyObjectFn(__identity<any>())(
            data.visualizationConfig,
        );
        instance.dataConfig = __getCopyObjectFn(__identity<any>())(
            data.dataConfig,
        );
        instance.pipelineId = data.pipelineId;
        instance.measureName = data.measureName;
        return instance;
    }
}

export class DataLakeMeasure extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.datalake.DataLakeMeasure';
    '_rev': string;
    'eventSchema': EventSchema;
    'measureName': string;
    'pipelineId': string;
    'pipelineIsRunning': boolean;
    'pipelineName': string;
    'schemaVersion': string;
    'timestampField': string;

    static 'fromData'(
        data: DataLakeMeasure,
        target?: DataLakeMeasure,
    ): DataLakeMeasure {
        if (!data) {
            return data;
        }
        const instance = target || new DataLakeMeasure();
        super.fromData(data, instance);
        instance.measureName = data.measureName;
        instance.timestampField = data.timestampField;
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.pipelineId = data.pipelineId;
        instance.pipelineName = data.pipelineName;
        instance.pipelineIsRunning = data.pipelineIsRunning;
        instance.schemaVersion = data.schemaVersion;
        instance._rev = data._rev;
        return instance;
    }
}

export class InvocableStreamPipesEntity extends NamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.base.InvocableStreamPipesEntity'
        | 'org.apache.streampipes.model.graph.DataProcessorInvocation'
        | 'org.apache.streampipes.model.graph.DataSinkInvocation';
    'belongsTo': string;
    'configured': boolean;
    'correspondingPipeline': string;
    'correspondingUser': string;
    'inputStreams': SpDataStreamUnion[];
    'selectedEndpointUrl': string;
    'staticProperties': StaticPropertyUnion[];
    'statusInfoSettings': ElementStatusInfoSettings;
    'streamRequirements': SpDataStreamUnion[];
    'supportedGrounding': EventGrounding;
    'uncompleted': boolean;

    static 'fromData'(
        data: InvocableStreamPipesEntity,
        target?: InvocableStreamPipesEntity,
    ): InvocableStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new InvocableStreamPipesEntity();
        super.fromData(data, instance);
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(
            data.inputStreams,
        );
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        instance.belongsTo = data.belongsTo;
        instance.statusInfoSettings = ElementStatusInfoSettings.fromData(
            data.statusInfoSettings,
        );
        instance.supportedGrounding = EventGrounding.fromData(
            data.supportedGrounding,
        );
        instance.correspondingPipeline = data.correspondingPipeline;
        instance.correspondingUser = data.correspondingUser;
        instance.streamRequirements = __getCopyArrayFn(
            SpDataStream.fromDataUnion,
        )(data.streamRequirements);
        instance.configured = data.configured;
        instance.uncompleted = data.uncompleted;
        instance.selectedEndpointUrl = data.selectedEndpointUrl;
        return instance;
    }
}

export class DataProcessorInvocation extends InvocableStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.graph.DataProcessorInvocation';
    'category': string[];
    'outputStrategies': OutputStrategyUnion[];
    'outputStream': SpDataStreamUnion;
    'pathName': string;

    static 'fromData'(
        data: DataProcessorInvocation,
        target?: DataProcessorInvocation,
    ): DataProcessorInvocation {
        if (!data) {
            return data;
        }
        const instance = target || new DataProcessorInvocation();
        super.fromData(data, instance);
        instance.outputStream = SpDataStream.fromDataUnion(data.outputStream);
        instance.outputStrategies = __getCopyArrayFn(
            OutputStrategy.fromDataUnion,
        )(data.outputStrategies);
        instance.pathName = data.pathName;
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        return instance;
    }
}

export class DataProcessorType {
    code: string;
    description: string;
    label: string;

    static fromData(
        data: DataProcessorType,
        target?: DataProcessorType,
    ): DataProcessorType {
        if (!data) {
            return data;
        }
        const instance = target || new DataProcessorType();
        instance.label = data.label;
        instance.description = data.description;
        instance.code = data.code;
        return instance;
    }
}

export class DataSeries {
    headers: string[];
    rows: any[][];
    tags: { [index: string]: string };
    total: number;

    static fromData(data: DataSeries, target?: DataSeries): DataSeries {
        if (!data) {
            return data;
        }
        const instance = target || new DataSeries();
        instance.total = data.total;
        instance.rows = __getCopyArrayFn(__getCopyArrayFn(__identity<any>()))(
            data.rows,
        );
        instance.tags = __getCopyObjectFn(__identity<string>())(data.tags);
        instance.headers = __getCopyArrayFn(__identity<string>())(data.headers);
        return instance;
    }
}

export class Message {
    elementName: string;
    notifications: Notification[];
    success: boolean;

    static fromData(data: Message, target?: Message): Message {
        if (!data) {
            return data;
        }
        const instance = target || new Message();
        instance.success = data.success;
        instance.elementName = data.elementName;
        instance.notifications = __getCopyArrayFn(Notification.fromData)(
            data.notifications,
        );
        return instance;
    }
}

export class DataSetModificationMessage extends Message {
    eventGrounding: EventGrounding;
    invocationId: string;

    static fromData(
        data: DataSetModificationMessage,
        target?: DataSetModificationMessage,
    ): DataSetModificationMessage {
        if (!data) {
            return data;
        }
        const instance = target || new DataSetModificationMessage();
        super.fromData(data, instance);
        instance.eventGrounding = EventGrounding.fromData(data.eventGrounding);
        instance.invocationId = data.invocationId;
        return instance;
    }
}

export class DataSinkInvocation extends InvocableStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.graph.DataSinkInvocation';
    'category': string[];

    static 'fromData'(
        data: DataSinkInvocation,
        target?: DataSinkInvocation,
    ): DataSinkInvocation {
        if (!data) {
            return data;
        }
        const instance = target || new DataSinkInvocation();
        super.fromData(data, instance);
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        return instance;
    }
}

export class DataSinkType {
    code: string;
    description: string;
    label: string;

    static fromData(data: DataSinkType, target?: DataSinkType): DataSinkType {
        if (!data) {
            return data;
        }
        const instance = target || new DataSinkType();
        instance.label = data.label;
        instance.description = data.description;
        instance.code = data.code;
        return instance;
    }
}

export class DataSourceDescription extends NamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.graph.DataSourceDescription';
    'correspondingSourceId': string;
    'spDataStreams': SpDataStreamUnion[];

    static 'fromData'(
        data: DataSourceDescription,
        target?: DataSourceDescription,
    ): DataSourceDescription {
        if (!data) {
            return data;
        }
        const instance = target || new DataSourceDescription();
        super.fromData(data, instance);
        instance.spDataStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(
            data.spDataStreams,
        );
        instance.correspondingSourceId = data.correspondingSourceId;
        return instance;
    }
}

export class DeleteRuleDescription extends SchemaTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription';
    'runtimeKey': string;

    static 'fromData'(
        data: DeleteRuleDescription,
        target?: DeleteRuleDescription,
    ): DeleteRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new DeleteRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        return instance;
    }
}

export class DomainStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.DomainStaticProperty';
    'requiredClass': string;
    'supportedProperties': SupportedProperty[];

    static 'fromData'(
        data: DomainStaticProperty,
        target?: DomainStaticProperty,
    ): DomainStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new DomainStaticProperty();
        super.fromData(data, instance);
        instance.requiredClass = data.requiredClass;
        instance.supportedProperties = __getCopyArrayFn(
            SupportedProperty.fromData,
        )(data.supportedProperties);
        return instance;
    }
}

export class EdgeValidationStatus {
    notifications: Notification[];
    validationStatusType: EdgeValidationStatusType;

    static fromData(
        data: EdgeValidationStatus,
        target?: EdgeValidationStatus,
    ): EdgeValidationStatus {
        if (!data) {
            return data;
        }
        const instance = target || new EdgeValidationStatus();
        instance.validationStatusType = data.validationStatusType;
        instance.notifications = __getCopyArrayFn(Notification.fromData)(
            data.notifications,
        );
        return instance;
    }
}

export class ElementComposition {
    description: string;
    name: string;
    sepas: DataProcessorInvocation[];
    streams: SpDataStreamUnion[];

    static fromData(
        data: ElementComposition,
        target?: ElementComposition,
    ): ElementComposition {
        if (!data) {
            return data;
        }
        const instance = target || new ElementComposition();
        instance.sepas = __getCopyArrayFn(DataProcessorInvocation.fromData)(
            data.sepas,
        );
        instance.streams = __getCopyArrayFn(SpDataStream.fromDataUnion)(
            data.streams,
        );
        instance.name = data.name;
        instance.description = data.description;
        return instance;
    }
}

export class ElementStatusInfoSettings extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.monitoring.ElementStatusInfoSettings';
    'elementIdentifier': string;
    'errorTopic': string;
    'kafkaHost': string;
    'kafkaPort': number;
    'statsTopic': string;

    static 'fromData'(
        data: ElementStatusInfoSettings,
        target?: ElementStatusInfoSettings,
    ): ElementStatusInfoSettings {
        if (!data) {
            return data;
        }
        const instance = target || new ElementStatusInfoSettings();
        super.fromData(data, instance);
        instance.elementIdentifier = data.elementIdentifier;
        instance.kafkaHost = data.kafkaHost;
        instance.kafkaPort = data.kafkaPort;
        instance.errorTopic = data.errorTopic;
        instance.statsTopic = data.statsTopic;
        return instance;
    }
}

export class ValueSpecification extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.schema.ValueSpecification'
        | 'org.apache.streampipes.model.schema.QuantitativeValue'
        | 'org.apache.streampipes.model.schema.Enumeration';

    static 'fromData'(
        data: ValueSpecification,
        target?: ValueSpecification,
    ): ValueSpecification {
        if (!data) {
            return data;
        }
        const instance = target || new ValueSpecification();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: ValueSpecificationUnion,
    ): ValueSpecificationUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.schema.QuantitativeValue':
                return QuantitativeValue.fromData(data);
            case 'org.apache.streampipes.model.schema.Enumeration':
                return Enumeration.fromData(data);
        }
    }
}

export class Enumeration extends ValueSpecification {
    '@class': 'org.apache.streampipes.model.schema.Enumeration';
    'description': string;
    'label': string;
    'runtimeValues': string[];

    static 'fromData'(data: Enumeration, target?: Enumeration): Enumeration {
        if (!data) {
            return data;
        }
        const instance = target || new Enumeration();
        super.fromData(data, instance);
        instance.label = data.label;
        instance.description = data.description;
        instance.runtimeValues = __getCopyArrayFn(__identity<string>())(
            data.runtimeValues,
        );
        return instance;
    }
}

export class ErrorMessage extends Message {
    static fromData(data: ErrorMessage, target?: ErrorMessage): ErrorMessage {
        if (!data) {
            return data;
        }
        const instance = target || new ErrorMessage();
        super.fromData(data, instance);
        return instance;
    }
}

export class EventGrounding extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.grounding.EventGrounding';
    'transportFormats': TransportFormat[];
    'transportProtocols': TransportProtocolUnion[];

    static 'fromData'(
        data: EventGrounding,
        target?: EventGrounding,
    ): EventGrounding {
        if (!data) {
            return data;
        }
        const instance = target || new EventGrounding();
        super.fromData(data, instance);
        instance.transportProtocols = __getCopyArrayFn(
            TransportProtocol.fromDataUnion,
        )(data.transportProtocols);
        instance.transportFormats = __getCopyArrayFn(TransportFormat.fromData)(
            data.transportFormats,
        );
        return instance;
    }
}

export class EventProperty extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.schema.EventProperty'
        | 'org.apache.streampipes.model.schema.EventPropertyList'
        | 'org.apache.streampipes.model.schema.EventPropertyNested'
        | 'org.apache.streampipes.model.schema.EventPropertyPrimitive';
    'description': string;
    'domainProperties': string[];
    'eventPropertyQualities': EventPropertyQualityDefinitionUnion[];
    'index': number;
    'label': string;
    'propertyScope': string;
    'required': boolean;
    'requiresEventPropertyQualities': EventPropertyQualityRequirement[];
    'runtimeId': string;
    'runtimeName': string;

    static 'fromData'(
        data: EventProperty,
        target?: EventProperty,
    ): EventProperty {
        if (!data) {
            return data;
        }
        const instance = target || new EventProperty();
        super.fromData(data, instance);
        instance.label = data.label;
        instance.description = data.description;
        instance.runtimeName = data.runtimeName;
        instance.required = data.required;
        instance.domainProperties = __getCopyArrayFn(__identity<string>())(
            data.domainProperties,
        );
        instance.eventPropertyQualities = __getCopyArrayFn(
            EventPropertyQualityDefinition.fromDataUnion,
        )(data.eventPropertyQualities);
        instance.requiresEventPropertyQualities = __getCopyArrayFn(
            EventPropertyQualityRequirement.fromData,
        )(data.requiresEventPropertyQualities);
        instance.propertyScope = data.propertyScope;
        instance.index = data.index;
        instance.runtimeId = data.runtimeId;
        return instance;
    }

    static 'fromDataUnion'(data: EventPropertyUnion): EventPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.schema.EventPropertyList':
                return EventPropertyList.fromData(data);
            case 'org.apache.streampipes.model.schema.EventPropertyNested':
                return EventPropertyNested.fromData(data);
            case 'org.apache.streampipes.model.schema.EventPropertyPrimitive':
                return EventPropertyPrimitive.fromData(data);
        }
    }
}

export class EventPropertyList extends EventProperty {
    '@class': 'org.apache.streampipes.model.schema.EventPropertyList';
    'eventProperty': EventPropertyUnion;

    static 'fromData'(
        data: EventPropertyList,
        target?: EventPropertyList,
    ): EventPropertyList {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyList();
        super.fromData(data, instance);
        instance.eventProperty = EventProperty.fromDataUnion(
            data.eventProperty,
        );
        return instance;
    }
}

export class EventPropertyNested extends EventProperty {
    '@class': 'org.apache.streampipes.model.schema.EventPropertyNested';
    'eventProperties': EventPropertyUnion[];

    static 'fromData'(
        data: EventPropertyNested,
        target?: EventPropertyNested,
    ): EventPropertyNested {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyNested();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
        return instance;
    }
}

export class EventPropertyPrimitive extends EventProperty {
    '@class': 'org.apache.streampipes.model.schema.EventPropertyPrimitive';
    'measurementUnit': string;
    'runtimeType': string;
    'valueSpecification': ValueSpecificationUnion;

    static 'fromData'(
        data: EventPropertyPrimitive,
        target?: EventPropertyPrimitive,
    ): EventPropertyPrimitive {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyPrimitive();
        super.fromData(data, instance);
        instance.runtimeType = data.runtimeType;
        instance.measurementUnit = data.measurementUnit;
        instance.valueSpecification = ValueSpecification.fromDataUnion(
            data.valueSpecification,
        );
        return instance;
    }
}

export class EventPropertyQualityRequirement extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.quality.EventPropertyQualityRequirement';
    'maximumPropertyQuality': EventPropertyQualityDefinitionUnion;
    'minimumPropertyQuality': EventPropertyQualityDefinitionUnion;

    static 'fromData'(
        data: EventPropertyQualityRequirement,
        target?: EventPropertyQualityRequirement,
    ): EventPropertyQualityRequirement {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyQualityRequirement();
        super.fromData(data, instance);
        instance.minimumPropertyQuality =
            EventPropertyQualityDefinition.fromDataUnion(
                data.minimumPropertyQuality,
            );
        instance.maximumPropertyQuality =
            EventPropertyQualityDefinition.fromDataUnion(
                data.maximumPropertyQuality,
            );
        return instance;
    }
}

export class StreamTransformationRuleDescription extends TransformationRuleDescription {
    '@class':
        | 'org.apache.streampipes.model.connect.rules.stream.StreamTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription'
        | 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription';

    static 'fromData'(
        data: StreamTransformationRuleDescription,
        target?: StreamTransformationRuleDescription,
    ): StreamTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new StreamTransformationRuleDescription();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: StreamTransformationRuleDescriptionUnion,
    ): StreamTransformationRuleDescriptionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription':
                return EventRateTransformationRuleDescription.fromData(data);
            case 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription':
                return RemoveDuplicatesTransformationRuleDescription.fromData(
                    data,
                );
        }
    }
}

export class EventRateTransformationRuleDescription extends StreamTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription';
    'aggregationTimeWindow': number;
    'aggregationType': string;

    static 'fromData'(
        data: EventRateTransformationRuleDescription,
        target?: EventRateTransformationRuleDescription,
    ): EventRateTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new EventRateTransformationRuleDescription();
        super.fromData(data, instance);
        instance.aggregationTimeWindow = data.aggregationTimeWindow;
        instance.aggregationType = data.aggregationType;
        return instance;
    }
}

export class EventSchema extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.schema.EventSchema';
    'eventProperties': EventPropertyUnion[];

    static 'fromData'(data: EventSchema, target?: EventSchema): EventSchema {
        if (!data) {
            return data;
        }
        const instance = target || new EventSchema();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
        return instance;
    }
}

export class EventStreamQualityDefinition extends MeasurementProperty {
    '@class':
        | 'org.apache.streampipes.model.quality.EventStreamQualityDefinition'
        | 'org.apache.streampipes.model.quality.Frequency'
        | 'org.apache.streampipes.model.quality.Latency';

    static 'fromData'(
        data: EventStreamQualityDefinition,
        target?: EventStreamQualityDefinition,
    ): EventStreamQualityDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new EventStreamQualityDefinition();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: EventStreamQualityDefinitionUnion,
    ): EventStreamQualityDefinitionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.quality.Frequency':
                return Frequency.fromData(data);
            case 'org.apache.streampipes.model.quality.Latency':
                return Latency.fromData(data);
        }
    }
}

export class EventStreamQualityRequirement extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.quality.EventStreamQualityRequirement';
    'maximumStreamQuality': EventStreamQualityDefinitionUnion;
    'minimumStreamQuality': EventStreamQualityDefinitionUnion;

    static 'fromData'(
        data: EventStreamQualityRequirement,
        target?: EventStreamQualityRequirement,
    ): EventStreamQualityRequirement {
        if (!data) {
            return data;
        }
        const instance = target || new EventStreamQualityRequirement();
        super.fromData(data, instance);
        instance.minimumStreamQuality =
            EventStreamQualityDefinition.fromDataUnion(
                data.minimumStreamQuality,
            );
        instance.maximumStreamQuality =
            EventStreamQualityDefinition.fromDataUnion(
                data.maximumStreamQuality,
            );
        return instance;
    }
}

export class ExportConfiguration {
    assetExportConfiguration: AssetExportConfiguration[];

    static fromData(
        data: ExportConfiguration,
        target?: ExportConfiguration,
    ): ExportConfiguration {
        if (!data) {
            return data;
        }
        const instance = target || new ExportConfiguration();
        instance.assetExportConfiguration = __getCopyArrayFn(
            AssetExportConfiguration.fromData,
        )(data.assetExportConfiguration);
        return instance;
    }
}

export class ExportItem {
    label: string;
    resourceId: string;
    selected: boolean;

    static fromData(data: ExportItem, target?: ExportItem): ExportItem {
        if (!data) {
            return data;
        }
        const instance = target || new ExportItem();
        instance.resourceId = data.resourceId;
        instance.label = data.label;
        instance.selected = data.selected;
        return instance;
    }
}

export class FieldStatusInfo {
    additionalInfo: string;
    changesRequired: boolean;
    fieldStatus: FieldStatus;

    static fromData(
        data: FieldStatusInfo,
        target?: FieldStatusInfo,
    ): FieldStatusInfo {
        if (!data) {
            return data;
        }
        const instance = target || new FieldStatusInfo();
        instance.fieldStatus = data.fieldStatus;
        instance.additionalInfo = data.additionalInfo;
        instance.changesRequired = data.changesRequired;
        return instance;
    }
}

export class FileMetadata {
    createdAt: number;
    createdByUser: string;
    fileId: string;
    filetype: string;
    internalFilename: string;
    lastModified: number;
    originalFilename: string;
    rev: string;

    static fromData(data: FileMetadata, target?: FileMetadata): FileMetadata {
        if (!data) {
            return data;
        }
        const instance = target || new FileMetadata();
        instance.fileId = data.fileId;
        instance.rev = data.rev;
        instance.internalFilename = data.internalFilename;
        instance.originalFilename = data.originalFilename;
        instance.filetype = data.filetype;
        instance.createdAt = data.createdAt;
        instance.lastModified = data.lastModified;
        instance.createdByUser = data.createdByUser;
        return instance;
    }
}

export class FileStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.FileStaticProperty';
    'endpointUrl': string;
    'locationPath': string;
    'requiredFiletypes': string[];

    static 'fromData'(
        data: FileStaticProperty,
        target?: FileStaticProperty,
    ): FileStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new FileStaticProperty();
        super.fromData(data, instance);
        instance.endpointUrl = data.endpointUrl;
        instance.locationPath = data.locationPath;
        instance.requiredFiletypes = __getCopyArrayFn(__identity<string>())(
            data.requiredFiletypes,
        );
        return instance;
    }
}

export class FixedOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.FixedOutputStrategy';
    'eventProperties': EventPropertyUnion[];

    static 'fromData'(
        data: FixedOutputStrategy,
        target?: FixedOutputStrategy,
    ): FixedOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new FixedOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
        return instance;
    }
}

export class FormatDescription extends NamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.connect.grounding.FormatDescription';
    'config': StaticPropertyUnion[];
    'formatType': string;

    static 'fromData'(
        data: FormatDescription,
        target?: FormatDescription,
    ): FormatDescription {
        if (!data) {
            return data;
        }
        const instance = target || new FormatDescription();
        super.fromData(data, instance);
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.formatType = data.formatType;
        return instance;
    }
}

export class FreeTextStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.FreeTextStaticProperty';
    'htmlAllowed': boolean;
    'htmlFontFormat': boolean;
    'mapsTo': string;
    'multiLine': boolean;
    'placeholdersSupported': boolean;
    'requiredDatatype': string;
    'requiredDomainProperty': string;
    'value': string;
    'valueSpecification': PropertyValueSpecification;

    static 'fromData'(
        data: FreeTextStaticProperty,
        target?: FreeTextStaticProperty,
    ): FreeTextStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new FreeTextStaticProperty();
        super.fromData(data, instance);
        instance.value = data.value;
        instance.requiredDatatype = data.requiredDatatype;
        instance.requiredDomainProperty = data.requiredDomainProperty;
        instance.mapsTo = data.mapsTo;
        instance.multiLine = data.multiLine;
        instance.htmlAllowed = data.htmlAllowed;
        instance.htmlFontFormat = data.htmlFontFormat;
        instance.placeholdersSupported = data.placeholdersSupported;
        instance.valueSpecification = PropertyValueSpecification.fromData(
            data.valueSpecification,
        );
        return instance;
    }
}

export class Frequency extends EventStreamQualityDefinition {
    '@class': 'org.apache.streampipes.model.quality.Frequency';
    'quantityValue': number;

    static 'fromData'(data: Frequency, target?: Frequency): Frequency {
        if (!data) {
            return data;
        }
        const instance = target || new Frequency();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class FunctionDefinition {
    consumedStreams: string[];
    functionId: FunctionId;

    static fromData(
        data: FunctionDefinition,
        target?: FunctionDefinition,
    ): FunctionDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new FunctionDefinition();
        instance.functionId = FunctionId.fromData(data.functionId);
        instance.consumedStreams = __getCopyArrayFn(__identity<string>())(
            data.consumedStreams,
        );
        return instance;
    }
}

export class FunctionId {
    id: string;
    version: number;

    static fromData(data: FunctionId, target?: FunctionId): FunctionId {
        if (!data) {
            return data;
        }
        const instance = target || new FunctionId();
        instance.id = data.id;
        instance.version = data.version;
        return instance;
    }
}

export interface GenericAdapterDescription {
    eventSchema: EventSchema;
    formatDescription: FormatDescription;
    protocolDescription: ProtocolDescription;
    rules: TransformationRuleDescriptionUnion[];
}

export class GenericAdapterSetDescription
    extends AdapterSetDescription
    implements GenericAdapterDescription
{
    '@class': 'org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription';
    'eventSchema': EventSchema;
    'formatDescription': FormatDescription;
    'protocolDescription': ProtocolDescription;

    static 'fromData'(
        data: GenericAdapterSetDescription,
        target?: GenericAdapterSetDescription,
    ): GenericAdapterSetDescription {
        if (!data) {
            return data;
        }
        const instance = target || new GenericAdapterSetDescription();
        super.fromData(data, instance);
        instance.formatDescription = FormatDescription.fromData(
            data.formatDescription,
        );
        instance.protocolDescription = ProtocolDescription.fromData(
            data.protocolDescription,
        );
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        return instance;
    }
}

export class GenericAdapterStreamDescription
    extends AdapterStreamDescription
    implements GenericAdapterDescription
{
    '@class': 'org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription';
    'eventSchema': EventSchema;
    'formatDescription': FormatDescription;
    'protocolDescription': ProtocolDescription;

    static 'fromData'(
        data: GenericAdapterStreamDescription,
        target?: GenericAdapterStreamDescription,
    ): GenericAdapterStreamDescription {
        if (!data) {
            return data;
        }
        const instance = target || new GenericAdapterStreamDescription();
        super.fromData(data, instance);
        instance.formatDescription = FormatDescription.fromData(
            data.formatDescription,
        );
        instance.protocolDescription = ProtocolDescription.fromData(
            data.protocolDescription,
        );
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        return instance;
    }
}

export class GuessSchema extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.connect.guess.GuessSchema';
    'eventPreview': { [index: string]: GuessTypeInfo }[];
    'eventSchema': EventSchema;
    'fieldStatusInfo': { [index: string]: FieldStatusInfo };

    static 'fromData'(data: GuessSchema, target?: GuessSchema): GuessSchema {
        if (!data) {
            return data;
        }
        const instance = target || new GuessSchema();
        super.fromData(data, instance);
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.eventPreview = __getCopyArrayFn(
            __getCopyObjectFn(GuessTypeInfo.fromData),
        )(data.eventPreview);
        instance.fieldStatusInfo = __getCopyObjectFn(FieldStatusInfo.fromData)(
            data.fieldStatusInfo,
        );
        return instance;
    }
}

export class GuessTypeInfo {
    type: string;
    value: any;

    static fromData(
        data: GuessTypeInfo,
        target?: GuessTypeInfo,
    ): GuessTypeInfo {
        if (!data) {
            return data;
        }
        const instance = target || new GuessTypeInfo();
        instance.type = data.type;
        instance.value = data.value;
        return instance;
    }
}

export class TransportProtocol extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.grounding.TransportProtocol'
        | 'org.apache.streampipes.model.grounding.JmsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.KafkaTransportProtocol'
        | 'org.apache.streampipes.model.grounding.MqttTransportProtocol'
        | 'org.apache.streampipes.model.grounding.NatsTransportProtocol';
    'brokerHostname': string;
    'topicDefinition': TopicDefinitionUnion;

    static 'fromData'(
        data: TransportProtocol,
        target?: TransportProtocol,
    ): TransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new TransportProtocol();
        super.fromData(data, instance);
        instance.brokerHostname = data.brokerHostname;
        instance.topicDefinition = TopicDefinition.fromDataUnion(
            data.topicDefinition,
        );
        return instance;
    }

    static 'fromDataUnion'(
        data: TransportProtocolUnion,
    ): TransportProtocolUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.grounding.JmsTransportProtocol':
                return JmsTransportProtocol.fromData(data);
            case 'org.apache.streampipes.model.grounding.KafkaTransportProtocol':
                return KafkaTransportProtocol.fromData(data);
            case 'org.apache.streampipes.model.grounding.MqttTransportProtocol':
                return MqttTransportProtocol.fromData(data);
            case 'org.apache.streampipes.model.grounding.NatsTransportProtocol':
                return NatsTransportProtocol.fromData(data);
        }
    }
}

export class JmsTransportProtocol extends TransportProtocol {
    '@class': 'org.apache.streampipes.model.grounding.JmsTransportProtocol';
    'port': number;

    static 'fromData'(
        data: JmsTransportProtocol,
        target?: JmsTransportProtocol,
    ): JmsTransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new JmsTransportProtocol();
        super.fromData(data, instance);
        instance.port = data.port;
        return instance;
    }
}

export class KafkaTransportProtocol extends TransportProtocol {
    '@class': 'org.apache.streampipes.model.grounding.KafkaTransportProtocol';
    'acks': string;
    'batchSize': string;
    'groupId': string;
    'kafkaPort': number;
    'lingerMs': number;
    'maxRequestSize': string;
    'messageMaxBytes': string;
    'offset': string;
    'zookeeperHost': string;
    'zookeeperPort': number;

    static 'fromData'(
        data: KafkaTransportProtocol,
        target?: KafkaTransportProtocol,
    ): KafkaTransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new KafkaTransportProtocol();
        super.fromData(data, instance);
        instance.zookeeperHost = data.zookeeperHost;
        instance.zookeeperPort = data.zookeeperPort;
        instance.kafkaPort = data.kafkaPort;
        instance.lingerMs = data.lingerMs;
        instance.messageMaxBytes = data.messageMaxBytes;
        instance.maxRequestSize = data.maxRequestSize;
        instance.acks = data.acks;
        instance.batchSize = data.batchSize;
        instance.offset = data.offset;
        instance.groupId = data.groupId;
        return instance;
    }
}

export class KeepOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.KeepOutputStrategy';
    'eventName': string;
    'keepBoth': boolean;

    static 'fromData'(
        data: KeepOutputStrategy,
        target?: KeepOutputStrategy,
    ): KeepOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new KeepOutputStrategy();
        super.fromData(data, instance);
        instance.eventName = data.eventName;
        instance.keepBoth = data.keepBoth;
        return instance;
    }
}

export class Label {
    _id: string;
    _rev: string;
    categoryId: string;
    color: string;
    internalName: string;
    name: string;

    static fromData(data: Label, target?: Label): Label {
        if (!data) {
            return data;
        }
        const instance = target || new Label();
        instance.name = data.name;
        instance.color = data.color;
        instance.internalName = data.internalName;
        instance.categoryId = data.categoryId;
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class Latency extends EventStreamQualityDefinition {
    '@class': 'org.apache.streampipes.model.quality.Latency';
    'quantityValue': number;

    static 'fromData'(data: Latency, target?: Latency): Latency {
        if (!data) {
            return data;
        }
        const instance = target || new Latency();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class ListOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.ListOutputStrategy';
    'propertyName': string;

    static 'fromData'(
        data: ListOutputStrategy,
        target?: ListOutputStrategy,
    ): ListOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new ListOutputStrategy();
        super.fromData(data, instance);
        instance.propertyName = data.propertyName;
        return instance;
    }
}

export class MappingProperty extends StaticProperty {
    '@class':
        | 'org.apache.streampipes.model.staticproperty.MappingProperty'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary'
        | 'org.apache.streampipes.model.staticproperty.MappingPropertyNary';
    'mapsFromOptions': string[];
    'propertyScope': string;
    'requirementSelector': string;

    static 'fromData'(
        data: MappingProperty,
        target?: MappingProperty,
    ): MappingProperty {
        if (!data) {
            return data;
        }
        const instance = target || new MappingProperty();
        super.fromData(data, instance);
        instance.requirementSelector = data.requirementSelector;
        instance.mapsFromOptions = __getCopyArrayFn(__identity<string>())(
            data.mapsFromOptions,
        );
        instance.propertyScope = data.propertyScope;
        return instance;
    }

    static 'fromDataUnion'(data: MappingPropertyUnion): MappingPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.staticproperty.MappingPropertyNary':
                return MappingPropertyNary.fromData(data);
            case 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary':
                return MappingPropertyUnary.fromData(data);
        }
    }
}

export class MappingPropertyNary extends MappingProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.MappingPropertyNary';
    'selectedProperties': string[];

    static 'fromData'(
        data: MappingPropertyNary,
        target?: MappingPropertyNary,
    ): MappingPropertyNary {
        if (!data) {
            return data;
        }
        const instance = target || new MappingPropertyNary();
        super.fromData(data, instance);
        instance.selectedProperties = __getCopyArrayFn(__identity<string>())(
            data.selectedProperties,
        );
        return instance;
    }
}

export class MappingPropertyUnary extends MappingProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.MappingPropertyUnary';
    'selectedProperty': string;

    static 'fromData'(
        data: MappingPropertyUnary,
        target?: MappingPropertyUnary,
    ): MappingPropertyUnary {
        if (!data) {
            return data;
        }
        const instance = target || new MappingPropertyUnary();
        super.fromData(data, instance);
        instance.selectedProperty = data.selectedProperty;
        return instance;
    }
}

export class MatchingStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.MatchingStaticProperty';
    'matchLeft': string;
    'matchRight': string;

    static 'fromData'(
        data: MatchingStaticProperty,
        target?: MatchingStaticProperty,
    ): MatchingStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new MatchingStaticProperty();
        super.fromData(data, instance);
        instance.matchLeft = data.matchLeft;
        instance.matchRight = data.matchRight;
        return instance;
    }
}

export class MeasurementCapability extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.quality.MeasurementCapability';
    'capability': string;

    static 'fromData'(
        data: MeasurementCapability,
        target?: MeasurementCapability,
    ): MeasurementCapability {
        if (!data) {
            return data;
        }
        const instance = target || new MeasurementCapability();
        super.fromData(data, instance);
        instance.capability = data.capability;
        return instance;
    }
}

export class MeasurementObject extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.quality.MeasurementObject';
    'measuresObject': string;

    static 'fromData'(
        data: MeasurementObject,
        target?: MeasurementObject,
    ): MeasurementObject {
        if (!data) {
            return data;
        }
        const instance = target || new MeasurementObject();
        super.fromData(data, instance);
        instance.measuresObject = data.measuresObject;
        return instance;
    }
}

export class MeasurementRange extends EventPropertyQualityDefinition {
    '@class': 'org.apache.streampipes.model.quality.MeasurementRange';
    'maxValue': number;
    'minValue': number;

    static 'fromData'(
        data: MeasurementRange,
        target?: MeasurementRange,
    ): MeasurementRange {
        if (!data) {
            return data;
        }
        const instance = target || new MeasurementRange();
        super.fromData(data, instance);
        instance.minValue = data.minValue;
        instance.maxValue = data.maxValue;
        return instance;
    }
}

export class MessageCounter {
    counter: number;
    lastTimestamp: number;

    static fromData(
        data: MessageCounter,
        target?: MessageCounter,
    ): MessageCounter {
        if (!data) {
            return data;
        }
        const instance = target || new MessageCounter();
        instance.lastTimestamp = data.lastTimestamp;
        instance.counter = data.counter;
        return instance;
    }
}

export class MoveRuleDescription extends SchemaTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription';
    'newRuntimeKey': string;
    'oldRuntimeKey': string;

    static 'fromData'(
        data: MoveRuleDescription,
        target?: MoveRuleDescription,
    ): MoveRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new MoveRuleDescription();
        super.fromData(data, instance);
        instance.oldRuntimeKey = data.oldRuntimeKey;
        instance.newRuntimeKey = data.newRuntimeKey;
        return instance;
    }
}

export class MqttTransportProtocol extends TransportProtocol {
    '@class': 'org.apache.streampipes.model.grounding.MqttTransportProtocol';
    'port': number;

    static 'fromData'(
        data: MqttTransportProtocol,
        target?: MqttTransportProtocol,
    ): MqttTransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new MqttTransportProtocol();
        super.fromData(data, instance);
        instance.port = data.port;
        return instance;
    }
}

export class NatsTransportProtocol extends TransportProtocol {
    '@class': 'org.apache.streampipes.model.grounding.NatsTransportProtocol';
    'port': number;

    static 'fromData'(
        data: NatsTransportProtocol,
        target?: NatsTransportProtocol,
    ): NatsTransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new NatsTransportProtocol();
        super.fromData(data, instance);
        instance.port = data.port;
        return instance;
    }
}

export class Notification {
    additionalInformation: string;
    description: string;
    title: string;

    static fromData(data: Notification, target?: Notification): Notification {
        if (!data) {
            return data;
        }
        const instance = target || new Notification();
        instance.title = data.title;
        instance.description = data.description;
        instance.additionalInformation = data.additionalInformation;
        return instance;
    }
}

export class OneOfStaticProperty extends SelectionStaticProperty {
    '@class':
        | 'org.apache.streampipes.model.staticproperty.OneOfStaticProperty'
        | 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty';

    static 'fromData'(
        data: OneOfStaticProperty,
        target?: OneOfStaticProperty,
    ): OneOfStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new OneOfStaticProperty();
        super.fromData(data, instance);
        return instance;
    }

    static 'fromDataUnion'(
        data: OneOfStaticPropertyUnion,
    ): OneOfStaticPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty':
                return RuntimeResolvableOneOfStaticProperty.fromData(data);
        }
    }
}

export class Option extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.staticproperty.Option';
    'internalName': string;
    'name': string;
    'selected': boolean;

    static 'fromData'(data: Option, target?: Option): Option {
        if (!data) {
            return data;
        }
        const instance = target || new Option();
        super.fromData(data, instance);
        instance.name = data.name;
        instance.selected = data.selected;
        instance.internalName = data.internalName;
        return instance;
    }
}

export class PageResult extends DataSeries {
    page: number;
    pageSum: number;

    static fromData(data: PageResult, target?: PageResult): PageResult {
        if (!data) {
            return data;
        }
        const instance = target || new PageResult();
        super.fromData(data, instance);
        instance.page = data.page;
        instance.pageSum = data.pageSum;
        return instance;
    }
}

export class PersistedDataStream {
    measureName: string;
    pipelineId: string;
    pipelineName: string;
    schema: EventSchema;

    static fromData(
        data: PersistedDataStream,
        target?: PersistedDataStream,
    ): PersistedDataStream {
        if (!data) {
            return data;
        }
        const instance = target || new PersistedDataStream();
        instance.pipelineId = data.pipelineId;
        instance.pipelineName = data.pipelineName;
        instance.schema = EventSchema.fromData(data.schema);
        instance.measureName = data.measureName;
        return instance;
    }
}

export class Pipeline extends ElementComposition {
    _id: string;
    _rev: string;
    actions: DataSinkInvocation[];
    createdAt: number;
    createdByUser: string;
    healthStatus: PipelineHealthStatus;
    pipelineCategories: string[];
    pipelineNotifications: string[];
    publicElement: boolean;
    restartOnSystemReboot: boolean;
    running: boolean;
    startedAt: number;

    static fromData(data: Pipeline, target?: Pipeline): Pipeline {
        if (!data) {
            return data;
        }
        const instance = target || new Pipeline();
        super.fromData(data, instance);
        instance.actions = __getCopyArrayFn(DataSinkInvocation.fromData)(
            data.actions,
        );
        instance.running = data.running;
        instance.restartOnSystemReboot = data.restartOnSystemReboot;
        instance.startedAt = data.startedAt;
        instance.createdAt = data.createdAt;
        instance.publicElement = data.publicElement;
        instance.createdByUser = data.createdByUser;
        instance.pipelineCategories = __getCopyArrayFn(__identity<string>())(
            data.pipelineCategories,
        );
        instance.pipelineNotifications = __getCopyArrayFn(__identity<string>())(
            data.pipelineNotifications,
        );
        instance.healthStatus = data.healthStatus;
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class PipelineCanvasComment {
    comment: string;
    position: CanvasPosition;

    static fromData(
        data: PipelineCanvasComment,
        target?: PipelineCanvasComment,
    ): PipelineCanvasComment {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineCanvasComment();
        instance.comment = data.comment;
        instance.position = CanvasPosition.fromData(data.position);
        return instance;
    }
}

export class PipelineCanvasMetadata {
    _id: string;
    _rev: string;
    comments: PipelineCanvasComment[];
    pipelineElementMetadata: { [index: string]: PipelineElementMetadata };
    pipelineId: string;

    static fromData(
        data: PipelineCanvasMetadata,
        target?: PipelineCanvasMetadata,
    ): PipelineCanvasMetadata {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineCanvasMetadata();
        instance.pipelineId = data.pipelineId;
        instance.pipelineElementMetadata = __getCopyObjectFn(
            PipelineElementMetadata.fromData,
        )(data.pipelineElementMetadata);
        instance.comments = __getCopyArrayFn(PipelineCanvasComment.fromData)(
            data.comments,
        );
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class PipelineCategory {
    _id: string;
    _rev: string;
    categoryDescription: string;
    categoryName: string;

    static fromData(
        data: PipelineCategory,
        target?: PipelineCategory,
    ): PipelineCategory {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineCategory();
        instance.categoryName = data.categoryName;
        instance.categoryDescription = data.categoryDescription;
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class PipelineEdgeValidation {
    sourceId: string;
    status: EdgeValidationStatus;
    targetId: string;

    static fromData(
        data: PipelineEdgeValidation,
        target?: PipelineEdgeValidation,
    ): PipelineEdgeValidation {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineEdgeValidation();
        instance.sourceId = data.sourceId;
        instance.targetId = data.targetId;
        instance.status = EdgeValidationStatus.fromData(data.status);
        return instance;
    }
}

export class PipelineElementMetadata {
    customName: string;
    position: CanvasPosition;

    static fromData(
        data: PipelineElementMetadata,
        target?: PipelineElementMetadata,
    ): PipelineElementMetadata {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementMetadata();
        instance.position = CanvasPosition.fromData(data.position);
        instance.customName = data.customName;
        return instance;
    }
}

export class PipelineElementMonitoringInfo {
    consumedMessageInfoExists: boolean;
    consumedMessagesInfos: ConsumedMessagesInfo[];
    pipelineElementId: string;
    pipelineElementName: string;
    producedMessageInfoExists: boolean;
    producedMessagesInfo: ProducedMessagesInfo;

    static fromData(
        data: PipelineElementMonitoringInfo,
        target?: PipelineElementMonitoringInfo,
    ): PipelineElementMonitoringInfo {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementMonitoringInfo();
        instance.pipelineElementId = data.pipelineElementId;
        instance.pipelineElementName = data.pipelineElementName;
        instance.consumedMessageInfoExists = data.consumedMessageInfoExists;
        instance.producedMessageInfoExists = data.producedMessageInfoExists;
        instance.consumedMessagesInfos = __getCopyArrayFn(
            ConsumedMessagesInfo.fromData,
        )(data.consumedMessagesInfos);
        instance.producedMessagesInfo = ProducedMessagesInfo.fromData(
            data.producedMessagesInfo,
        );
        return instance;
    }
}

export class PipelineElementRecommendation {
    count: number;
    description: string;
    elementId: string;
    name: string;
    weight: number;

    static fromData(
        data: PipelineElementRecommendation,
        target?: PipelineElementRecommendation,
    ): PipelineElementRecommendation {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementRecommendation();
        instance.elementId = data.elementId;
        instance.name = data.name;
        instance.description = data.description;
        instance.weight = data.weight;
        instance.count = data.count;
        return instance;
    }
}

export class PipelineElementRecommendationMessage {
    possibleElements: PipelineElementRecommendation[];
    recommendedElements: PipelineElementRecommendation[];
    success: boolean;

    static fromData(
        data: PipelineElementRecommendationMessage,
        target?: PipelineElementRecommendationMessage,
    ): PipelineElementRecommendationMessage {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementRecommendationMessage();
        instance.possibleElements = __getCopyArrayFn(
            PipelineElementRecommendation.fromData,
        )(data.possibleElements);
        instance.recommendedElements = __getCopyArrayFn(
            PipelineElementRecommendation.fromData,
        )(data.recommendedElements);
        instance.success = data.success;
        return instance;
    }
}

export class PipelineElementStatus {
    elementId: string;
    elementName: string;
    optionalMessage: string;
    success: boolean;

    static fromData(
        data: PipelineElementStatus,
        target?: PipelineElementStatus,
    ): PipelineElementStatus {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementStatus();
        instance.elementId = data.elementId;
        instance.elementName = data.elementName;
        instance.optionalMessage = data.optionalMessage;
        instance.success = data.success;
        return instance;
    }
}

export class PipelineElementTemplate {
    _id: string;
    _rev: string;
    basePipelineElementAppId: string;
    templateConfigs: { [index: string]: PipelineElementTemplateConfig };
    templateDescription: string;
    templateName: string;

    static fromData(
        data: PipelineElementTemplate,
        target?: PipelineElementTemplate,
    ): PipelineElementTemplate {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementTemplate();
        instance.templateName = data.templateName;
        instance.templateDescription = data.templateDescription;
        instance.basePipelineElementAppId = data.basePipelineElementAppId;
        instance.templateConfigs = __getCopyObjectFn(
            PipelineElementTemplateConfig.fromData,
        )(data.templateConfigs);
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class PipelineElementTemplateConfig {
    displayed: boolean;
    editable: boolean;
    value: any;

    static fromData(
        data: PipelineElementTemplateConfig,
        target?: PipelineElementTemplateConfig,
    ): PipelineElementTemplateConfig {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementTemplateConfig();
        instance.editable = data.editable;
        instance.displayed = data.displayed;
        instance.value = data.value;
        return instance;
    }
}

export class PipelineElementValidationInfo {
    level: ValidationInfoLevel;
    message: string;

    static fromData(
        data: PipelineElementValidationInfo,
        target?: PipelineElementValidationInfo,
    ): PipelineElementValidationInfo {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineElementValidationInfo();
        instance.level = data.level;
        instance.message = data.message;
        return instance;
    }
}

export class PipelineModification {
    domId: string;
    elementId: string;
    inputStreams: SpDataStreamUnion[];
    outputStrategies: OutputStrategyUnion[];
    outputStream: SpDataStreamUnion;
    pipelineElementValid: boolean;
    staticProperties: StaticPropertyUnion[];
    validationInfos: PipelineElementValidationInfo[];

    static fromData(
        data: PipelineModification,
        target?: PipelineModification,
    ): PipelineModification {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineModification();
        instance.domId = data.domId;
        instance.elementId = data.elementId;
        instance.pipelineElementValid = data.pipelineElementValid;
        instance.validationInfos = __getCopyArrayFn(
            PipelineElementValidationInfo.fromData,
        )(data.validationInfos);
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        instance.outputStrategies = __getCopyArrayFn(
            OutputStrategy.fromDataUnion,
        )(data.outputStrategies);
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(
            data.inputStreams,
        );
        instance.outputStream = SpDataStream.fromDataUnion(data.outputStream);
        return instance;
    }
}

export class PipelineModificationMessage extends Message {
    edgeValidations: PipelineEdgeValidation[];
    pipelineModifications: PipelineModification[];
    pipelineValid: boolean;

    static fromData(
        data: PipelineModificationMessage,
        target?: PipelineModificationMessage,
    ): PipelineModificationMessage {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineModificationMessage();
        super.fromData(data, instance);
        instance.pipelineModifications = __getCopyArrayFn(
            PipelineModification.fromData,
        )(data.pipelineModifications);
        instance.pipelineValid = data.pipelineValid;
        instance.edgeValidations = __getCopyArrayFn(
            PipelineEdgeValidation.fromData,
        )(data.edgeValidations);
        return instance;
    }
}

export class PipelineMonitoringInfo {
    createdAt: number;
    pipelineElementMonitoringInfo: PipelineElementMonitoringInfo[];
    pipelineId: string;
    startedAt: number;

    static fromData(
        data: PipelineMonitoringInfo,
        target?: PipelineMonitoringInfo,
    ): PipelineMonitoringInfo {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineMonitoringInfo();
        instance.pipelineId = data.pipelineId;
        instance.createdAt = data.createdAt;
        instance.startedAt = data.startedAt;
        instance.pipelineElementMonitoringInfo = __getCopyArrayFn(
            PipelineElementMonitoringInfo.fromData,
        )(data.pipelineElementMonitoringInfo);
        return instance;
    }
}

export class PipelineOperationStatus {
    elementStatus: PipelineElementStatus[];
    pipelineId: string;
    pipelineName: string;
    success: boolean;
    title: string;

    static fromData(
        data: PipelineOperationStatus,
        target?: PipelineOperationStatus,
    ): PipelineOperationStatus {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineOperationStatus();
        instance.pipelineId = data.pipelineId;
        instance.pipelineName = data.pipelineName;
        instance.title = data.title;
        instance.success = data.success;
        instance.elementStatus = __getCopyArrayFn(
            PipelineElementStatus.fromData,
        )(data.elementStatus);
        return instance;
    }
}

export class PipelinePreviewModel {
    previewId: string;
    supportedPipelineElementDomIds: string[];

    static fromData(
        data: PipelinePreviewModel,
        target?: PipelinePreviewModel,
    ): PipelinePreviewModel {
        if (!data) {
            return data;
        }
        const instance = target || new PipelinePreviewModel();
        instance.previewId = data.previewId;
        instance.supportedPipelineElementDomIds = __getCopyArrayFn(
            __identity<string>(),
        )(data.supportedPipelineElementDomIds);
        return instance;
    }
}

export class PipelineStatusMessage {
    message: string;
    messageType: string;
    pipelineId: string;
    timestamp: number;

    static fromData(
        data: PipelineStatusMessage,
        target?: PipelineStatusMessage,
    ): PipelineStatusMessage {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineStatusMessage();
        instance.pipelineId = data.pipelineId;
        instance.timestamp = data.timestamp;
        instance.messageType = data.messageType;
        instance.message = data.message;
        return instance;
    }
}

export class PipelineTemplateDescription extends NamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.template.PipelineTemplateDescription';
    'boundTo': BoundPipelineElement[];
    'pipelineTemplateDescription': string;
    'pipelineTemplateId': string;
    'pipelineTemplateName': string;

    static 'fromData'(
        data: PipelineTemplateDescription,
        target?: PipelineTemplateDescription,
    ): PipelineTemplateDescription {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineTemplateDescription();
        super.fromData(data, instance);
        instance.boundTo = __getCopyArrayFn(BoundPipelineElement.fromData)(
            data.boundTo,
        );
        instance.pipelineTemplateName = data.pipelineTemplateName;
        instance.pipelineTemplateId = data.pipelineTemplateId;
        instance.pipelineTemplateDescription = data.pipelineTemplateDescription;
        return instance;
    }
}

export class PipelineTemplateInvocation extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.template.PipelineTemplateInvocation';
    'dataSetId': string;
    'kviName': string;
    'pipelineTemplateDescription': PipelineTemplateDescription;
    'pipelineTemplateId': string;
    'staticProperties': StaticPropertyUnion[];

    static 'fromData'(
        data: PipelineTemplateInvocation,
        target?: PipelineTemplateInvocation,
    ): PipelineTemplateInvocation {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineTemplateInvocation();
        super.fromData(data, instance);
        instance.kviName = data.kviName;
        instance.dataSetId = data.dataSetId;
        instance.pipelineTemplateId = data.pipelineTemplateId;
        instance.pipelineTemplateDescription =
            PipelineTemplateDescription.fromData(
                data.pipelineTemplateDescription,
            );
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        return instance;
    }
}

export class Precision extends EventPropertyQualityDefinition {
    '@class': 'org.apache.streampipes.model.quality.Precision';
    'quantityValue': number;

    static 'fromData'(data: Precision, target?: Precision): Precision {
        if (!data) {
            return data;
        }
        const instance = target || new Precision();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class ProducedMessagesInfo extends MessagesInfo {
    totalProducedMessages: number;
    totalProducedMessagesSincePipelineStart: number;

    static fromData(
        data: ProducedMessagesInfo,
        target?: ProducedMessagesInfo,
    ): ProducedMessagesInfo {
        if (!data) {
            return data;
        }
        const instance = target || new ProducedMessagesInfo();
        super.fromData(data, instance);
        instance.totalProducedMessages = data.totalProducedMessages;
        instance.totalProducedMessagesSincePipelineStart =
            data.totalProducedMessagesSincePipelineStart;
        return instance;
    }
}

export class PropertyRenameRule extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.output.PropertyRenameRule';
    'newRuntimeName': string;
    'runtimeId': string;

    static 'fromData'(
        data: PropertyRenameRule,
        target?: PropertyRenameRule,
    ): PropertyRenameRule {
        if (!data) {
            return data;
        }
        const instance = target || new PropertyRenameRule();
        super.fromData(data, instance);
        instance.runtimeId = data.runtimeId;
        instance.newRuntimeName = data.newRuntimeName;
        return instance;
    }
}

export class PropertyValueSpecification extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.staticproperty.PropertyValueSpecification';
    'maxValue': number;
    'minValue': number;
    'step': number;

    static 'fromData'(
        data: PropertyValueSpecification,
        target?: PropertyValueSpecification,
    ): PropertyValueSpecification {
        if (!data) {
            return data;
        }
        const instance = target || new PropertyValueSpecification();
        super.fromData(data, instance);
        instance.minValue = data.minValue;
        instance.maxValue = data.maxValue;
        instance.step = data.step;
        return instance;
    }
}

export class ProtocolDescription extends NamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.connect.grounding.ProtocolDescription';
    'category': string[];
    'config': StaticPropertyUnion[];
    'sourceType': string;

    static 'fromData'(
        data: ProtocolDescription,
        target?: ProtocolDescription,
    ): ProtocolDescription {
        if (!data) {
            return data;
        }
        const instance = target || new ProtocolDescription();
        super.fromData(data, instance);
        instance.sourceType = data.sourceType;
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        return instance;
    }
}

export class QuantitativeValue extends ValueSpecification {
    '@class': 'org.apache.streampipes.model.schema.QuantitativeValue';
    'maxValue': number;
    'minValue': number;
    'step': number;

    static 'fromData'(
        data: QuantitativeValue,
        target?: QuantitativeValue,
    ): QuantitativeValue {
        if (!data) {
            return data;
        }
        const instance = target || new QuantitativeValue();
        super.fromData(data, instance);
        instance.minValue = data.minValue;
        instance.maxValue = data.maxValue;
        instance.step = data.step;
        return instance;
    }
}

export class RemoveDuplicatesTransformationRuleDescription extends StreamTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription';
    'filterTimeWindow': string;

    static 'fromData'(
        data: RemoveDuplicatesTransformationRuleDescription,
        target?: RemoveDuplicatesTransformationRuleDescription,
    ): RemoveDuplicatesTransformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance =
            target || new RemoveDuplicatesTransformationRuleDescription();
        super.fromData(data, instance);
        instance.filterTimeWindow = data.filterTimeWindow;
        return instance;
    }
}

export class RenameRuleDescription extends SchemaTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription';
    'newRuntimeKey': string;
    'oldRuntimeKey': string;

    static 'fromData'(
        data: RenameRuleDescription,
        target?: RenameRuleDescription,
    ): RenameRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new RenameRuleDescription();
        super.fromData(data, instance);
        instance.oldRuntimeKey = data.oldRuntimeKey;
        instance.newRuntimeKey = data.newRuntimeKey;
        return instance;
    }
}

export class Resolution extends EventPropertyQualityDefinition {
    '@class': 'org.apache.streampipes.model.quality.Resolution';
    'quantityValue': number;

    static 'fromData'(data: Resolution, target?: Resolution): Resolution {
        if (!data) {
            return data;
        }
        const instance = target || new Resolution();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class RuntimeOptionsRequest extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsRequest'
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsResponse';
    'appId': string;
    'belongsTo': string;
    'inputStreams': SpDataStreamUnion[];
    'requestId': string;
    'staticProperties': StaticPropertyUnion[];

    static 'fromData'(
        data: RuntimeOptionsRequest,
        target?: RuntimeOptionsRequest,
    ): RuntimeOptionsRequest {
        if (!data) {
            return data;
        }
        const instance = target || new RuntimeOptionsRequest();
        super.fromData(data, instance);
        instance.requestId = data.requestId;
        instance.appId = data.appId;
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(
            data.inputStreams,
        );
        instance.belongsTo = data.belongsTo;
        return instance;
    }
}

export class RuntimeOptionsResponse extends RuntimeOptionsRequest {
    '@class': 'org.apache.streampipes.model.runtime.RuntimeOptionsResponse';
    'staticProperty': StaticPropertyUnion;

    static 'fromData'(
        data: RuntimeOptionsResponse,
        target?: RuntimeOptionsResponse,
    ): RuntimeOptionsResponse {
        if (!data) {
            return data;
        }
        const instance = target || new RuntimeOptionsResponse();
        super.fromData(data, instance);
        instance.staticProperty = StaticProperty.fromDataUnion(
            data.staticProperty,
        );
        return instance;
    }
}

export class RuntimeResolvableAnyStaticProperty extends AnyStaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty';
    'dependsOn': string[];

    static 'fromData'(
        data: RuntimeResolvableAnyStaticProperty,
        target?: RuntimeResolvableAnyStaticProperty,
    ): RuntimeResolvableAnyStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new RuntimeResolvableAnyStaticProperty();
        super.fromData(data, instance);
        instance.dependsOn = __getCopyArrayFn(__identity<string>())(
            data.dependsOn,
        );
        return instance;
    }
}

export class RuntimeResolvableOneOfStaticProperty extends OneOfStaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty';
    'dependsOn': string[];

    static 'fromData'(
        data: RuntimeResolvableOneOfStaticProperty,
        target?: RuntimeResolvableOneOfStaticProperty,
    ): RuntimeResolvableOneOfStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new RuntimeResolvableOneOfStaticProperty();
        super.fromData(data, instance);
        instance.dependsOn = __getCopyArrayFn(__identity<string>())(
            data.dependsOn,
        );
        return instance;
    }
}

export class RuntimeResolvableTreeInputStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty';
    'dependsOn': string[];
    'nodes': TreeInputNode[];

    static 'fromData'(
        data: RuntimeResolvableTreeInputStaticProperty,
        target?: RuntimeResolvableTreeInputStaticProperty,
    ): RuntimeResolvableTreeInputStaticProperty {
        if (!data) {
            return data;
        }
        const instance =
            target || new RuntimeResolvableTreeInputStaticProperty();
        super.fromData(data, instance);
        instance.dependsOn = __getCopyArrayFn(__identity<string>())(
            data.dependsOn,
        );
        instance.nodes = __getCopyArrayFn(TreeInputNode.fromData)(data.nodes);
        return instance;
    }
}

export class SecretStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.SecretStaticProperty';
    'encrypted': boolean;
    'value': string;

    static 'fromData'(
        data: SecretStaticProperty,
        target?: SecretStaticProperty,
    ): SecretStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new SecretStaticProperty();
        super.fromData(data, instance);
        instance.value = data.value;
        instance.encrypted = data.encrypted;
        return instance;
    }
}

export class TopicDefinition extends UnnamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.grounding.TopicDefinition'
        | 'org.apache.streampipes.model.grounding.SimpleTopicDefinition'
        | 'org.apache.streampipes.model.grounding.WildcardTopicDefinition';
    'actualTopicName': string;

    static 'fromData'(
        data: TopicDefinition,
        target?: TopicDefinition,
    ): TopicDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new TopicDefinition();
        super.fromData(data, instance);
        instance.actualTopicName = data.actualTopicName;
        return instance;
    }

    static 'fromDataUnion'(data: TopicDefinitionUnion): TopicDefinitionUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.grounding.SimpleTopicDefinition':
                return SimpleTopicDefinition.fromData(data);
            case 'org.apache.streampipes.model.grounding.WildcardTopicDefinition':
                return WildcardTopicDefinition.fromData(data);
        }
    }
}

export class SimpleTopicDefinition extends TopicDefinition {
    '@class': 'org.apache.streampipes.model.grounding.SimpleTopicDefinition';

    static 'fromData'(
        data: SimpleTopicDefinition,
        target?: SimpleTopicDefinition,
    ): SimpleTopicDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new SimpleTopicDefinition();
        super.fromData(data, instance);
        return instance;
    }
}

export class SlideToggleStaticProperty extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty';
    'defaultValue': boolean;
    'selected': boolean;

    static 'fromData'(
        data: SlideToggleStaticProperty,
        target?: SlideToggleStaticProperty,
    ): SlideToggleStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new SlideToggleStaticProperty();
        super.fromData(data, instance);
        instance.selected = data.selected;
        instance.defaultValue = data.defaultValue;
        return instance;
    }
}

export class SpDataStream extends NamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.SpDataStream'
        | 'org.apache.streampipes.model.SpDataSet';
    'category': string[];
    'correspondingAdapterId': string;
    'eventGrounding': EventGrounding;
    'eventSchema': EventSchema;
    'hasEventStreamQualities': EventStreamQualityDefinitionUnion[];
    'index': number;
    'measurementCapability': MeasurementCapability[];
    'measurementObject': MeasurementObject[];
    'requiresEventStreamQualities': EventStreamQualityRequirement[];

    static 'fromData'(data: SpDataStream, target?: SpDataStream): SpDataStream {
        if (!data) {
            return data;
        }
        const instance = target || new SpDataStream();
        super.fromData(data, instance);
        instance.hasEventStreamQualities = __getCopyArrayFn(
            EventStreamQualityDefinition.fromDataUnion,
        )(data.hasEventStreamQualities);
        instance.requiresEventStreamQualities = __getCopyArrayFn(
            EventStreamQualityRequirement.fromData,
        )(data.requiresEventStreamQualities);
        instance.eventGrounding = EventGrounding.fromData(data.eventGrounding);
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.measurementCapability = __getCopyArrayFn(
            MeasurementCapability.fromData,
        )(data.measurementCapability);
        instance.measurementObject = __getCopyArrayFn(
            MeasurementObject.fromData,
        )(data.measurementObject);
        instance.index = data.index;
        instance.correspondingAdapterId = data.correspondingAdapterId;
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        return instance;
    }

    static 'fromDataUnion'(data: SpDataStreamUnion): SpDataStreamUnion {
        if (!data) {
            return data;
        }
        switch (data['@class']) {
            case 'org.apache.streampipes.model.SpDataStream':
                return SpDataStream.fromData(data);
            case 'org.apache.streampipes.model.SpDataSet':
                return SpDataSet.fromData(data);
        }
    }
}

export class SpDataSet extends SpDataStream {
    '@class': 'org.apache.streampipes.model.SpDataSet';
    'actualTopicName': string;
    'brokerHostname': string;
    'correspondingPipeline': string;
    'datasetInvocationId': string;
    'selectedEndpointUrl': string;
    'supportedGrounding': EventGrounding;

    static 'fromData'(data: SpDataSet, target?: SpDataSet): SpDataSet {
        if (!data) {
            return data;
        }
        const instance = target || new SpDataSet();
        super.fromData(data, instance);
        instance.supportedGrounding = EventGrounding.fromData(
            data.supportedGrounding,
        );
        instance.datasetInvocationId = data.datasetInvocationId;
        instance.correspondingPipeline = data.correspondingPipeline;
        instance.selectedEndpointUrl = data.selectedEndpointUrl;
        instance.actualTopicName = data.actualTopicName;
        instance.brokerHostname = data.brokerHostname;
        return instance;
    }
}

export class SpLogEntry {
    errorMessage: StreamPipesErrorMessage;
    timestamp: number;

    static fromData(data: SpLogEntry, target?: SpLogEntry): SpLogEntry {
        if (!data) {
            return data;
        }
        const instance = target || new SpLogEntry();
        instance.timestamp = data.timestamp;
        instance.errorMessage = StreamPipesErrorMessage.fromData(
            data.errorMessage,
        );
        return instance;
    }
}

export class SpMetricsEntry {
    lastTimestamp: number;
    messagesIn: { [index: string]: MessageCounter };
    messagesOut: MessageCounter;

    static fromData(
        data: SpMetricsEntry,
        target?: SpMetricsEntry,
    ): SpMetricsEntry {
        if (!data) {
            return data;
        }
        const instance = target || new SpMetricsEntry();
        instance.lastTimestamp = data.lastTimestamp;
        instance.messagesIn = __getCopyObjectFn(MessageCounter.fromData)(
            data.messagesIn,
        );
        instance.messagesOut = MessageCounter.fromData(data.messagesOut);
        return instance;
    }
}

export class SpQueryResult {
    allDataSeries: DataSeries[];
    forId: string;
    headers: string[];
    sourceIndex: number;
    spQueryStatus: SpQueryStatus;
    total: number;

    static fromData(
        data: SpQueryResult,
        target?: SpQueryResult,
    ): SpQueryResult {
        if (!data) {
            return data;
        }
        const instance = target || new SpQueryResult();
        instance.total = data.total;
        instance.headers = __getCopyArrayFn(__identity<string>())(data.headers);
        instance.allDataSeries = __getCopyArrayFn(DataSeries.fromData)(
            data.allDataSeries,
        );
        instance.sourceIndex = data.sourceIndex;
        instance.spQueryStatus = data.spQueryStatus;
        instance.forId = data.forId;
        return instance;
    }
}

export class SpecificAdapterSetDescription extends AdapterSetDescription {
    '@class': 'org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription';

    static 'fromData'(
        data: SpecificAdapterSetDescription,
        target?: SpecificAdapterSetDescription,
    ): SpecificAdapterSetDescription {
        if (!data) {
            return data;
        }
        const instance = target || new SpecificAdapterSetDescription();
        super.fromData(data, instance);
        return instance;
    }
}

export class SpecificAdapterStreamDescription extends AdapterStreamDescription {
    '@class': 'org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription';

    static 'fromData'(
        data: SpecificAdapterStreamDescription,
        target?: SpecificAdapterStreamDescription,
    ): SpecificAdapterStreamDescription {
        if (!data) {
            return data;
        }
        const instance = target || new SpecificAdapterStreamDescription();
        super.fromData(data, instance);
        return instance;
    }
}

export class StaticPropertyAlternative extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternative';
    'selected': boolean;
    'staticProperty': StaticPropertyUnion;

    static 'fromData'(
        data: StaticPropertyAlternative,
        target?: StaticPropertyAlternative,
    ): StaticPropertyAlternative {
        if (!data) {
            return data;
        }
        const instance = target || new StaticPropertyAlternative();
        super.fromData(data, instance);
        instance.selected = data.selected;
        instance.staticProperty = StaticProperty.fromDataUnion(
            data.staticProperty,
        );
        return instance;
    }
}

export class StaticPropertyAlternatives extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives';
    'alternatives': StaticPropertyAlternative[];

    static 'fromData'(
        data: StaticPropertyAlternatives,
        target?: StaticPropertyAlternatives,
    ): StaticPropertyAlternatives {
        if (!data) {
            return data;
        }
        const instance = target || new StaticPropertyAlternatives();
        super.fromData(data, instance);
        instance.alternatives = __getCopyArrayFn(
            StaticPropertyAlternative.fromData,
        )(data.alternatives);
        return instance;
    }
}

export class StaticPropertyGroup extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.StaticPropertyGroup';
    'horizontalRendering': boolean;
    'showLabel': boolean;
    'staticProperties': StaticPropertyUnion[];

    static 'fromData'(
        data: StaticPropertyGroup,
        target?: StaticPropertyGroup,
    ): StaticPropertyGroup {
        if (!data) {
            return data;
        }
        const instance = target || new StaticPropertyGroup();
        super.fromData(data, instance);
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        instance.showLabel = data.showLabel;
        instance.horizontalRendering = data.horizontalRendering;
        return instance;
    }
}

export class StreamPipesApplicationPackage {
    adapters: string[];
    assets: string[];
    dashboardWidgets: string[];
    dashboards: string[];
    dataLakeMeasures: string[];
    dataSources: string[];
    dataViewWidgets: string[];
    dataViews: string[];
    files: string[];
    pipelines: string[];
    requiredAdapterAppIds: string[];
    requiredDataSinkAppIds: string[];
    requiredProcessorAppIds: string[];

    static fromData(
        data: StreamPipesApplicationPackage,
        target?: StreamPipesApplicationPackage,
    ): StreamPipesApplicationPackage {
        if (!data) {
            return data;
        }
        const instance = target || new StreamPipesApplicationPackage();
        instance.requiredProcessorAppIds = __getCopyArrayFn(
            __identity<string>(),
        )(data.requiredProcessorAppIds);
        instance.requiredDataSinkAppIds = __getCopyArrayFn(
            __identity<string>(),
        )(data.requiredDataSinkAppIds);
        instance.requiredAdapterAppIds = __getCopyArrayFn(__identity<string>())(
            data.requiredAdapterAppIds,
        );
        instance.assets = __getCopyArrayFn(__identity<string>())(data.assets);
        instance.adapters = __getCopyArrayFn(__identity<string>())(
            data.adapters,
        );
        instance.dashboards = __getCopyArrayFn(__identity<string>())(
            data.dashboards,
        );
        instance.dashboardWidgets = __getCopyArrayFn(__identity<string>())(
            data.dashboardWidgets,
        );
        instance.dataViews = __getCopyArrayFn(__identity<string>())(
            data.dataViews,
        );
        instance.dataViewWidgets = __getCopyArrayFn(__identity<string>())(
            data.dataViewWidgets,
        );
        instance.dataLakeMeasures = __getCopyArrayFn(__identity<string>())(
            data.dataLakeMeasures,
        );
        instance.dataSources = __getCopyArrayFn(__identity<string>())(
            data.dataSources,
        );
        instance.pipelines = __getCopyArrayFn(__identity<string>())(
            data.pipelines,
        );
        instance.files = __getCopyArrayFn(__identity<string>())(data.files);
        return instance;
    }
}

export class StreamPipesErrorMessage {
    cause: string;
    detail: string;
    fullStackTrace: string;
    level: string;
    title: string;

    static fromData(
        data: StreamPipesErrorMessage,
        target?: StreamPipesErrorMessage,
    ): StreamPipesErrorMessage {
        if (!data) {
            return data;
        }
        const instance = target || new StreamPipesErrorMessage();
        instance.level = data.level;
        instance.title = data.title;
        instance.detail = data.detail;
        instance.cause = data.cause;
        instance.fullStackTrace = data.fullStackTrace;
        return instance;
    }
}

export class SuccessMessage extends Message {
    static fromData(
        data: SuccessMessage,
        target?: SuccessMessage,
    ): SuccessMessage {
        if (!data) {
            return data;
        }
        const instance = target || new SuccessMessage();
        super.fromData(data, instance);
        return instance;
    }
}

export class SupportedProperty extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.staticproperty.SupportedProperty';
    'propertyId': string;
    'value': string;
    'valueRequired': boolean;

    static 'fromData'(
        data: SupportedProperty,
        target?: SupportedProperty,
    ): SupportedProperty {
        if (!data) {
            return data;
        }
        const instance = target || new SupportedProperty();
        super.fromData(data, instance);
        instance.propertyId = data.propertyId;
        instance.valueRequired = data.valueRequired;
        instance.value = data.value;
        return instance;
    }
}

export class TimestampTranfsformationRuleDescription extends ValueTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription';
    'formatString': string;
    'mode': string;
    'multiplier': number;
    'runtimeKey': string;

    static 'fromData'(
        data: TimestampTranfsformationRuleDescription,
        target?: TimestampTranfsformationRuleDescription,
    ): TimestampTranfsformationRuleDescription {
        if (!data) {
            return data;
        }
        const instance =
            target || new TimestampTranfsformationRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        instance.mode = data.mode;
        instance.formatString = data.formatString;
        instance.multiplier = data.multiplier;
        return instance;
    }
}

export class TransformOperation extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.output.TransformOperation';
    'mappingPropertyInternalName': string;
    'sourceStaticProperty': string;
    'targetValue': string;
    'transformationScope': string;

    static 'fromData'(
        data: TransformOperation,
        target?: TransformOperation,
    ): TransformOperation {
        if (!data) {
            return data;
        }
        const instance = target || new TransformOperation();
        super.fromData(data, instance);
        instance.mappingPropertyInternalName = data.mappingPropertyInternalName;
        instance.sourceStaticProperty = data.sourceStaticProperty;
        instance.transformationScope = data.transformationScope;
        instance.targetValue = data.targetValue;
        return instance;
    }
}

export class TransformOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.TransformOutputStrategy';
    'transformOperations': TransformOperation[];

    static 'fromData'(
        data: TransformOutputStrategy,
        target?: TransformOutputStrategy,
    ): TransformOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new TransformOutputStrategy();
        super.fromData(data, instance);
        instance.transformOperations = __getCopyArrayFn(
            TransformOperation.fromData,
        )(data.transformOperations);
        return instance;
    }
}

export class TransportFormat extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.grounding.TransportFormat';
    'rdfType': string[];

    static 'fromData'(
        data: TransportFormat,
        target?: TransportFormat,
    ): TransportFormat {
        if (!data) {
            return data;
        }
        const instance = target || new TransportFormat();
        super.fromData(data, instance);
        instance.rdfType = __getCopyArrayFn(__identity<string>())(data.rdfType);
        return instance;
    }
}

export class TreeInputNode {
    children: TreeInputNode[];
    dataNode: boolean;
    internalNodeName: string;
    nodeName: string;
    selected: boolean;

    static fromData(
        data: TreeInputNode,
        target?: TreeInputNode,
    ): TreeInputNode {
        if (!data) {
            return data;
        }
        const instance = target || new TreeInputNode();
        instance.dataNode = data.dataNode;
        instance.selected = data.selected;
        instance.nodeName = data.nodeName;
        instance.internalNodeName = data.internalNodeName;
        instance.children = __getCopyArrayFn(TreeInputNode.fromData)(
            data.children,
        );
        return instance;
    }
}

export class UnitTransformRuleDescription extends ValueTransformationRuleDescription {
    '@class': 'org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription';
    'fromUnitRessourceURL': string;
    'runtimeKey': string;
    'toUnitRessourceURL': string;

    static 'fromData'(
        data: UnitTransformRuleDescription,
        target?: UnitTransformRuleDescription,
    ): UnitTransformRuleDescription {
        if (!data) {
            return data;
        }
        const instance = target || new UnitTransformRuleDescription();
        super.fromData(data, instance);
        instance.runtimeKey = data.runtimeKey;
        instance.fromUnitRessourceURL = data.fromUnitRessourceURL;
        instance.toUnitRessourceURL = data.toUnitRessourceURL;
        return instance;
    }
}

export class UserDefinedOutputStrategy extends OutputStrategy {
    '@class': 'org.apache.streampipes.model.output.UserDefinedOutputStrategy';
    'eventProperties': EventPropertyUnion[];

    static 'fromData'(
        data: UserDefinedOutputStrategy,
        target?: UserDefinedOutputStrategy,
    ): UserDefinedOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new UserDefinedOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
        return instance;
    }
}

export class UserInfo {
    darkMode: boolean;
    displayName: string;
    roles: string[];
    showTutorial: boolean;
    username: string;

    static fromData(data: UserInfo, target?: UserInfo): UserInfo {
        if (!data) {
            return data;
        }
        const instance = target || new UserInfo();
        instance.username = data.username;
        instance.displayName = data.displayName;
        instance.roles = __getCopyArrayFn(__identity<string>())(data.roles);
        instance.showTutorial = data.showTutorial;
        instance.darkMode = data.darkMode;
        return instance;
    }
}

export class VisualizablePipeline {
    pipelineId: string;
    pipelineName: string;
    schema: EventSchema;
    topic: string;
    visualizationName: string;

    static fromData(
        data: VisualizablePipeline,
        target?: VisualizablePipeline,
    ): VisualizablePipeline {
        if (!data) {
            return data;
        }
        const instance = target || new VisualizablePipeline();
        instance.pipelineId = data.pipelineId;
        instance.schema = EventSchema.fromData(data.schema);
        instance.visualizationName = data.visualizationName;
        instance.topic = data.topic;
        instance.pipelineName = data.pipelineName;
        return instance;
    }
}

export class WildcardTopicDefinition extends TopicDefinition {
    '@class': 'org.apache.streampipes.model.grounding.WildcardTopicDefinition';
    'wildcardTopicMappings': WildcardTopicMapping[];
    'wildcardTopicName': string;

    static 'fromData'(
        data: WildcardTopicDefinition,
        target?: WildcardTopicDefinition,
    ): WildcardTopicDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new WildcardTopicDefinition();
        super.fromData(data, instance);
        instance.wildcardTopicName = data.wildcardTopicName;
        instance.wildcardTopicMappings = __getCopyArrayFn(
            WildcardTopicMapping.fromData,
        )(data.wildcardTopicMappings);
        return instance;
    }
}

export class WildcardTopicMapping extends UnnamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.grounding.WildcardTopicMapping';
    'mappedRuntimeName': string;
    'mappingId': string;
    'selectedMapping': string;
    'topicParameterType': string;

    static 'fromData'(
        data: WildcardTopicMapping,
        target?: WildcardTopicMapping,
    ): WildcardTopicMapping {
        if (!data) {
            return data;
        }
        const instance = target || new WildcardTopicMapping();
        super.fromData(data, instance);
        instance.topicParameterType = data.topicParameterType;
        instance.mappingId = data.mappingId;
        instance.mappedRuntimeName = data.mappedRuntimeName;
        instance.selectedMapping = data.selectedMapping;
        return instance;
    }
}

export type AdapterDescriptionUnion =
    | GenericAdapterSetDescription
    | GenericAdapterStreamDescription
    | SpecificAdapterStreamDescription
    | SpecificAdapterSetDescription;

export type AdapterSetDescriptionUnion =
    | GenericAdapterSetDescription
    | SpecificAdapterSetDescription;

export type AdapterStreamDescriptionUnion = SpecificAdapterStreamDescription;

export type EdgeValidationStatusType = 'COMPLETE' | 'INCOMPLETE' | 'INVALID';

export type EventPropertyQualityDefinitionUnion =
    | Accuracy
    | MeasurementRange
    | Precision
    | Resolution;

export type EventPropertyUnion =
    | EventPropertyList
    | EventPropertyNested
    | EventPropertyPrimitive;

export type EventStreamQualityDefinitionUnion = Frequency | Latency;

export type FieldStatus = 'GOOD' | 'BAD' | 'ATTENTION';

export type MappingPropertyUnion = MappingPropertyNary | MappingPropertyUnary;

export type MeasurementPropertyUnion =
    | EventPropertyQualityDefinition
    | EventStreamQualityDefinition;

export type OneOfStaticPropertyUnion = RuntimeResolvableOneOfStaticProperty;

export type OutputStrategyUnion =
    | AppendOutputStrategy
    | CustomOutputStrategy
    | CustomTransformOutputStrategy
    | FixedOutputStrategy
    | KeepOutputStrategy
    | ListOutputStrategy
    | TransformOutputStrategy
    | UserDefinedOutputStrategy;

export type PipelineHealthStatus = 'OK' | 'REQUIRES_ATTENTION' | 'FAILURE';

export type SelectionStaticPropertyUnion =
    | AnyStaticProperty
    | OneOfStaticProperty;

export type SpDataStreamUnion = SpDataStream | SpDataSet;

export type SpQueryStatus = 'OK' | 'TOO_MUCH_DATA';

export type StaticPropertyType =
    | 'AnyStaticProperty'
    | 'CodeInputStaticProperty'
    | 'CollectionStaticProperty'
    | 'ColorPickerStaticProperty'
    | 'DomainStaticProperty'
    | 'FreeTextStaticProperty'
    | 'FileStaticProperty'
    | 'MappingPropertyUnary'
    | 'MappingPropertyNary'
    | 'MatchingStaticProperty'
    | 'OneOfStaticProperty'
    | 'RuntimeResolvableAnyStaticProperty'
    | 'RuntimeResolvableOneOfStaticProperty'
    | 'RuntimeResolvableTreeInputStaticProperty'
    | 'StaticPropertyGroup'
    | 'StaticPropertyAlternatives'
    | 'StaticPropertyAlternative'
    | 'SecretStaticProperty'
    | 'SlideToggleStaticProperty';

export type StaticPropertyUnion =
    | AnyStaticProperty
    | CodeInputStaticProperty
    | CollectionStaticProperty
    | ColorPickerStaticProperty
    | DomainStaticProperty
    | FileStaticProperty
    | FreeTextStaticProperty
    | MappingPropertyUnary
    | MappingPropertyNary
    | MatchingStaticProperty
    | OneOfStaticProperty
    | RuntimeResolvableAnyStaticProperty
    | RuntimeResolvableOneOfStaticProperty
    | RuntimeResolvableTreeInputStaticProperty
    | SecretStaticProperty
    | StaticPropertyAlternative
    | StaticPropertyAlternatives
    | StaticPropertyGroup
    | SlideToggleStaticProperty;

export type StreamTransformationRuleDescriptionUnion =
    | EventRateTransformationRuleDescription
    | RemoveDuplicatesTransformationRuleDescription;

export type TopicDefinitionUnion =
    | SimpleTopicDefinition
    | WildcardTopicDefinition;

export type TransformationRuleDescriptionUnion =
    | AddTimestampRuleDescription
    | AddValueTransformationRuleDescription
    | TimestampTranfsformationRuleDescription
    | UnitTransformRuleDescription
    | EventRateTransformationRuleDescription
    | RemoveDuplicatesTransformationRuleDescription
    | CreateNestedRuleDescription
    | DeleteRuleDescription
    | RenameRuleDescription
    | MoveRuleDescription
    | ChangeDatatypeTransformationRuleDescription
    | CorrectionValueTransformationRuleDescription;

export type TransportProtocolUnion =
    | JmsTransportProtocol
    | KafkaTransportProtocol
    | MqttTransportProtocol
    | NatsTransportProtocol;

export type ValidationInfoLevel = 'INFO' | 'ERROR';

export type ValueSpecificationUnion = QuantitativeValue | Enumeration;

export type ValueTransformationRuleDescriptionUnion =
    | AddTimestampRuleDescription
    | AddValueTransformationRuleDescription
    | TimestampTranfsformationRuleDescription
    | UnitTransformRuleDescription
    | CorrectionValueTransformationRuleDescription;

function __getCopyArrayFn<T>(itemCopyFn: (item: T) => T): (array: T[]) => T[] {
    return (array: T[]) => __copyArray(array, itemCopyFn);
}

function __copyArray<T>(array: T[], itemCopyFn: (item: T) => T): T[] {
    return array && array.map(item => item && itemCopyFn(item));
}

function __getCopyObjectFn<T>(
    itemCopyFn: (item: T) => T,
): (object: { [index: string]: T }) => { [index: string]: T } {
    return (object: { [index: string]: T }) => __copyObject(object, itemCopyFn);
}

function __copyObject<T>(
    object: { [index: string]: T },
    itemCopyFn: (item: T) => T,
): { [index: string]: T } {
    if (!object) {
        return object;
    }
    const result: any = {};
    for (const key in object) {
        if (object.hasOwnProperty(key)) {
            const value = object[key];
            result[key] = value && itemCopyFn(value);
        }
    }
    return result;
}

function __identity<T>(): (value: T) => T {
    return value => value;
}
