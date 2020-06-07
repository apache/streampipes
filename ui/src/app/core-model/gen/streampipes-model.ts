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
// Generated using typescript-generator version 2.23.603 on 2020-06-07 10:39:36.

export class AbstractStreamPipesEntity {
    "@class": "org.apache.streampipes.model.base.NamedStreamPipesEntity" | "org.apache.streampipes.model.graph.DataSourceDescription" | "org.apache.streampipes.model.SpDataStream" | "org.apache.streampipes.model.SpDataSet" | "org.apache.streampipes.model.base.InvocableStreamPipesEntity" | "org.apache.streampipes.model.graph.DataProcessorInvocation" | "org.apache.streampipes.model.graph.DataSinkInvocation" | "org.apache.streampipes.model.base.UnnamedStreamPipesEntity" | "org.apache.streampipes.model.staticproperty.StaticProperty" | "org.apache.streampipes.model.staticproperty.CodeInputStaticProperty" | "org.apache.streampipes.model.staticproperty.CollectionStaticProperty" | "org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty" | "org.apache.streampipes.model.staticproperty.DomainStaticProperty" | "org.apache.streampipes.model.staticproperty.FileStaticProperty" | "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty" | "org.apache.streampipes.model.staticproperty.MatchingStaticProperty" | "org.apache.streampipes.model.staticproperty.SecretStaticProperty" | "org.apache.streampipes.model.staticproperty.StaticPropertyAlternative" | "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives" | "org.apache.streampipes.model.staticproperty.StaticPropertyGroup" | "org.apache.streampipes.model.staticproperty.SelectionStaticProperty" | "org.apache.streampipes.model.staticproperty.AnyStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty" | "org.apache.streampipes.model.staticproperty.OneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.MappingProperty" | "org.apache.streampipes.model.staticproperty.MappingPropertyUnary" | "org.apache.streampipes.model.staticproperty.MappingPropertyNary" | "org.apache.streampipes.model.ApplicationLink" | "org.apache.streampipes.model.quality.EventStreamQualityRequirement" | "org.apache.streampipes.model.grounding.EventGrounding" | "org.apache.streampipes.model.schema.EventSchema" | "org.apache.streampipes.model.quality.MeasurementCapability" | "org.apache.streampipes.model.quality.MeasurementObject" | "org.apache.streampipes.model.monitoring.ElementStatusInfoSettings" | "org.apache.streampipes.model.output.OutputStrategy" | "org.apache.streampipes.model.output.AppendOutputStrategy" | "org.apache.streampipes.model.output.CustomOutputStrategy" | "org.apache.streampipes.model.output.CustomTransformOutputStrategy" | "org.apache.streampipes.model.output.FixedOutputStrategy" | "org.apache.streampipes.model.output.KeepOutputStrategy" | "org.apache.streampipes.model.output.ListOutputStrategy" | "org.apache.streampipes.model.output.TransformOutputStrategy" | "org.apache.streampipes.model.output.UserDefinedOutputStrategy" | "org.apache.streampipes.model.staticproperty.Option" | "org.apache.streampipes.model.staticproperty.SupportedProperty" | "org.apache.streampipes.model.staticproperty.PropertyValueSpecification" | "org.apache.streampipes.model.quality.MeasurementProperty" | "org.apache.streampipes.model.quality.EventStreamQualityDefinition" | "org.apache.streampipes.model.quality.Frequency" | "org.apache.streampipes.model.quality.Latency" | "org.apache.streampipes.model.quality.EventPropertyQualityDefinition" | "org.apache.streampipes.model.quality.Accuracy" | "org.apache.streampipes.model.quality.MeasurementRange" | "org.apache.streampipes.model.quality.Precision" | "org.apache.streampipes.model.quality.Resolution" | "org.apache.streampipes.model.grounding.TransportProtocol" | "org.apache.streampipes.model.grounding.JmsTransportProtocol" | "org.apache.streampipes.model.grounding.KafkaTransportProtocol" | "org.apache.streampipes.model.grounding.MqttTransportProtocol" | "org.apache.streampipes.model.grounding.TransportFormat" | "org.apache.streampipes.model.schema.EventProperty" | "org.apache.streampipes.model.schema.EventPropertyList" | "org.apache.streampipes.model.schema.EventPropertyNested" | "org.apache.streampipes.model.schema.EventPropertyPrimitive" | "org.apache.streampipes.model.output.PropertyRenameRule" | "org.apache.streampipes.model.grounding.TopicDefinition" | "org.apache.streampipes.model.grounding.SimpleTopicDefinition" | "org.apache.streampipes.model.grounding.WildcardTopicDefinition" | "org.apache.streampipes.model.quality.EventPropertyQualityRequirement" | "org.apache.streampipes.model.output.TransformOperation" | "org.apache.streampipes.model.schema.ValueSpecification" | "org.apache.streampipes.model.schema.QuantitativeValue" | "org.apache.streampipes.model.schema.Enumeration" | "org.apache.streampipes.model.grounding.WildcardTopicMapping";

    static fromData(data: AbstractStreamPipesEntity, target?: AbstractStreamPipesEntity): AbstractStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new AbstractStreamPipesEntity();
        instance["@class"] = data["@class"];
        return instance;
    }
}

export class UnnamedStreamPipesEntity extends AbstractStreamPipesEntity {
    "@class": "org.apache.streampipes.model.base.UnnamedStreamPipesEntity" | "org.apache.streampipes.model.staticproperty.StaticProperty" | "org.apache.streampipes.model.staticproperty.CodeInputStaticProperty" | "org.apache.streampipes.model.staticproperty.CollectionStaticProperty" | "org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty" | "org.apache.streampipes.model.staticproperty.DomainStaticProperty" | "org.apache.streampipes.model.staticproperty.FileStaticProperty" | "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty" | "org.apache.streampipes.model.staticproperty.MatchingStaticProperty" | "org.apache.streampipes.model.staticproperty.SecretStaticProperty" | "org.apache.streampipes.model.staticproperty.StaticPropertyAlternative" | "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives" | "org.apache.streampipes.model.staticproperty.StaticPropertyGroup" | "org.apache.streampipes.model.staticproperty.SelectionStaticProperty" | "org.apache.streampipes.model.staticproperty.AnyStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty" | "org.apache.streampipes.model.staticproperty.OneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.MappingProperty" | "org.apache.streampipes.model.staticproperty.MappingPropertyUnary" | "org.apache.streampipes.model.staticproperty.MappingPropertyNary" | "org.apache.streampipes.model.ApplicationLink" | "org.apache.streampipes.model.quality.EventStreamQualityRequirement" | "org.apache.streampipes.model.grounding.EventGrounding" | "org.apache.streampipes.model.schema.EventSchema" | "org.apache.streampipes.model.quality.MeasurementCapability" | "org.apache.streampipes.model.quality.MeasurementObject" | "org.apache.streampipes.model.monitoring.ElementStatusInfoSettings" | "org.apache.streampipes.model.output.OutputStrategy" | "org.apache.streampipes.model.output.AppendOutputStrategy" | "org.apache.streampipes.model.output.CustomOutputStrategy" | "org.apache.streampipes.model.output.CustomTransformOutputStrategy" | "org.apache.streampipes.model.output.FixedOutputStrategy" | "org.apache.streampipes.model.output.KeepOutputStrategy" | "org.apache.streampipes.model.output.ListOutputStrategy" | "org.apache.streampipes.model.output.TransformOutputStrategy" | "org.apache.streampipes.model.output.UserDefinedOutputStrategy" | "org.apache.streampipes.model.staticproperty.Option" | "org.apache.streampipes.model.staticproperty.SupportedProperty" | "org.apache.streampipes.model.staticproperty.PropertyValueSpecification" | "org.apache.streampipes.model.quality.MeasurementProperty" | "org.apache.streampipes.model.quality.EventStreamQualityDefinition" | "org.apache.streampipes.model.quality.Frequency" | "org.apache.streampipes.model.quality.Latency" | "org.apache.streampipes.model.quality.EventPropertyQualityDefinition" | "org.apache.streampipes.model.quality.Accuracy" | "org.apache.streampipes.model.quality.MeasurementRange" | "org.apache.streampipes.model.quality.Precision" | "org.apache.streampipes.model.quality.Resolution" | "org.apache.streampipes.model.grounding.TransportProtocol" | "org.apache.streampipes.model.grounding.JmsTransportProtocol" | "org.apache.streampipes.model.grounding.KafkaTransportProtocol" | "org.apache.streampipes.model.grounding.MqttTransportProtocol" | "org.apache.streampipes.model.grounding.TransportFormat" | "org.apache.streampipes.model.schema.EventProperty" | "org.apache.streampipes.model.schema.EventPropertyList" | "org.apache.streampipes.model.schema.EventPropertyNested" | "org.apache.streampipes.model.schema.EventPropertyPrimitive" | "org.apache.streampipes.model.output.PropertyRenameRule" | "org.apache.streampipes.model.grounding.TopicDefinition" | "org.apache.streampipes.model.grounding.SimpleTopicDefinition" | "org.apache.streampipes.model.grounding.WildcardTopicDefinition" | "org.apache.streampipes.model.quality.EventPropertyQualityRequirement" | "org.apache.streampipes.model.output.TransformOperation" | "org.apache.streampipes.model.schema.ValueSpecification" | "org.apache.streampipes.model.schema.QuantitativeValue" | "org.apache.streampipes.model.schema.Enumeration" | "org.apache.streampipes.model.grounding.WildcardTopicMapping";
    elementId: string;

    static fromData(data: UnnamedStreamPipesEntity, target?: UnnamedStreamPipesEntity): UnnamedStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new UnnamedStreamPipesEntity();
        super.fromData(data, instance);
        instance.elementId = data.elementId;
        return instance;
    }
}

export class MeasurementProperty extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.quality.MeasurementProperty" | "org.apache.streampipes.model.quality.EventStreamQualityDefinition" | "org.apache.streampipes.model.quality.Frequency" | "org.apache.streampipes.model.quality.Latency" | "org.apache.streampipes.model.quality.EventPropertyQualityDefinition" | "org.apache.streampipes.model.quality.Accuracy" | "org.apache.streampipes.model.quality.MeasurementRange" | "org.apache.streampipes.model.quality.Precision" | "org.apache.streampipes.model.quality.Resolution";

    static fromData(data: MeasurementProperty, target?: MeasurementProperty): MeasurementProperty {
        if (!data) {
            return data;
        }
        const instance = target || new MeasurementProperty();
        super.fromData(data, instance);
        return instance;
    }

    static fromDataUnion(data: MeasurementPropertyUnion): MeasurementPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.quality.EventPropertyQualityDefinition":
                return EventPropertyQualityDefinition.fromData(data);
            case "org.apache.streampipes.model.quality.EventStreamQualityDefinition":
                return EventStreamQualityDefinition.fromData(data);
        }
    }
}

export class EventPropertyQualityDefinition extends MeasurementProperty {
    "@class": "org.apache.streampipes.model.quality.EventPropertyQualityDefinition" | "org.apache.streampipes.model.quality.Accuracy" | "org.apache.streampipes.model.quality.MeasurementRange" | "org.apache.streampipes.model.quality.Precision" | "org.apache.streampipes.model.quality.Resolution";

    static fromData(data: EventPropertyQualityDefinition, target?: EventPropertyQualityDefinition): EventPropertyQualityDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyQualityDefinition();
        super.fromData(data, instance);
        return instance;
    }

    static fromDataUnion(data: EventPropertyQualityDefinitionUnion): EventPropertyQualityDefinitionUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.quality.Accuracy":
                return Accuracy.fromData(data);
            case "org.apache.streampipes.model.quality.MeasurementRange":
                return MeasurementRange.fromData(data);
            case "org.apache.streampipes.model.quality.Precision":
                return Precision.fromData(data);
            case "org.apache.streampipes.model.quality.Resolution":
                return Resolution.fromData(data);
        }
    }
}

export class Accuracy extends EventPropertyQualityDefinition {
    "@class": "org.apache.streampipes.model.quality.Accuracy";
    quantityValue: number;

    static fromData(data: Accuracy, target?: Accuracy): Accuracy {
        if (!data) {
            return data;
        }
        const instance = target || new Accuracy();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
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

export class StaticProperty extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.staticproperty.StaticProperty" | "org.apache.streampipes.model.staticproperty.CodeInputStaticProperty" | "org.apache.streampipes.model.staticproperty.CollectionStaticProperty" | "org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty" | "org.apache.streampipes.model.staticproperty.DomainStaticProperty" | "org.apache.streampipes.model.staticproperty.FileStaticProperty" | "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty" | "org.apache.streampipes.model.staticproperty.MatchingStaticProperty" | "org.apache.streampipes.model.staticproperty.SecretStaticProperty" | "org.apache.streampipes.model.staticproperty.StaticPropertyAlternative" | "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives" | "org.apache.streampipes.model.staticproperty.StaticPropertyGroup" | "org.apache.streampipes.model.staticproperty.SelectionStaticProperty" | "org.apache.streampipes.model.staticproperty.AnyStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty" | "org.apache.streampipes.model.staticproperty.OneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.MappingProperty" | "org.apache.streampipes.model.staticproperty.MappingPropertyUnary" | "org.apache.streampipes.model.staticproperty.MappingPropertyNary";
    description: string;
    index: number;
    internalName: string;
    label: string;
    predefined: boolean;
    staticPropertyType: StaticPropertyType;
    valueRequired: boolean;

    static fromData(data: StaticProperty, target?: StaticProperty): StaticProperty {
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

    static fromDataUnion(data: StaticPropertyUnion): StaticPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.staticproperty.AnyStaticProperty":
                return AnyStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.CodeInputStaticProperty":
                return CodeInputStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.CollectionStaticProperty":
                return CollectionStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty":
                return ColorPickerStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.DomainStaticProperty":
                return DomainStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.FileStaticProperty":
                return FileStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty":
                return FreeTextStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.MappingPropertyUnary":
                return MappingPropertyUnary.fromData(data);
            case "org.apache.streampipes.model.staticproperty.MappingPropertyNary":
                return MappingPropertyNary.fromData(data);
            case "org.apache.streampipes.model.staticproperty.MatchingStaticProperty":
                return MatchingStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.OneOfStaticProperty":
                return OneOfStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty":
                return RuntimeResolvableAnyStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty":
                return RuntimeResolvableOneOfStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.SecretStaticProperty":
                return SecretStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.StaticPropertyAlternative":
                return StaticPropertyAlternative.fromData(data);
            case "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives":
                return StaticPropertyAlternatives.fromData(data);
            case "org.apache.streampipes.model.staticproperty.StaticPropertyGroup":
                return StaticPropertyGroup.fromData(data);
        }
    }
}

export class SelectionStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.SelectionStaticProperty" | "org.apache.streampipes.model.staticproperty.AnyStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty" | "org.apache.streampipes.model.staticproperty.OneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty";
    horizontalRendering: boolean;
    options: Option[];

    static fromData(data: SelectionStaticProperty, target?: SelectionStaticProperty): SelectionStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new SelectionStaticProperty();
        super.fromData(data, instance);
        instance.options = __getCopyArrayFn(Option.fromData)(data.options);
        instance.horizontalRendering = data.horizontalRendering;
        return instance;
    }

    static fromDataUnion(data: SelectionStaticPropertyUnion): SelectionStaticPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.staticproperty.AnyStaticProperty":
                return AnyStaticProperty.fromData(data);
            case "org.apache.streampipes.model.staticproperty.OneOfStaticProperty":
                return OneOfStaticProperty.fromData(data);
        }
    }
}

export class AnyStaticProperty extends SelectionStaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.AnyStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty";

    static fromData(data: AnyStaticProperty, target?: AnyStaticProperty): AnyStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new AnyStaticProperty();
        super.fromData(data, instance);
        return instance;
    }
}

export class OutputStrategy extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.output.OutputStrategy" | "org.apache.streampipes.model.output.AppendOutputStrategy" | "org.apache.streampipes.model.output.CustomOutputStrategy" | "org.apache.streampipes.model.output.CustomTransformOutputStrategy" | "org.apache.streampipes.model.output.FixedOutputStrategy" | "org.apache.streampipes.model.output.KeepOutputStrategy" | "org.apache.streampipes.model.output.ListOutputStrategy" | "org.apache.streampipes.model.output.TransformOutputStrategy" | "org.apache.streampipes.model.output.UserDefinedOutputStrategy";
    name: string;
    renameRules: PropertyRenameRule[];

    static fromData(data: OutputStrategy, target?: OutputStrategy): OutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new OutputStrategy();
        super.fromData(data, instance);
        instance.name = data.name;
        instance.renameRules = __getCopyArrayFn(PropertyRenameRule.fromData)(data.renameRules);
        return instance;
    }

    static fromDataUnion(data: OutputStrategyUnion): OutputStrategyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.output.AppendOutputStrategy":
                return AppendOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.CustomOutputStrategy":
                return CustomOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.CustomTransformOutputStrategy":
                return CustomTransformOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.FixedOutputStrategy":
                return FixedOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.KeepOutputStrategy":
                return KeepOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.ListOutputStrategy":
                return ListOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.TransformOutputStrategy":
                return TransformOutputStrategy.fromData(data);
            case "org.apache.streampipes.model.output.UserDefinedOutputStrategy":
                return UserDefinedOutputStrategy.fromData(data);
        }
    }
}

export class AppendOutputStrategy extends OutputStrategy {
    "@class": "org.apache.streampipes.model.output.AppendOutputStrategy";
    eventProperties: EventPropertyUnion[];

    static fromData(data: AppendOutputStrategy, target?: AppendOutputStrategy): AppendOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new AppendOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(EventProperty.fromDataUnion)(data.eventProperties);
        return instance;
    }
}

export class ApplicationLink extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.ApplicationLink";
    applicationDescription: string;
    applicationIconUrl: string;
    applicationLinkType: string;
    applicationName: string;
    applicationUrl: string;

    static fromData(data: ApplicationLink, target?: ApplicationLink): ApplicationLink {
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

export class CodeInputStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.CodeInputStaticProperty";
    codeTemplate: string;
    language: string;
    value: string;

    static fromData(data: CodeInputStaticProperty, target?: CodeInputStaticProperty): CodeInputStaticProperty {
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
    "@class": "org.apache.streampipes.model.staticproperty.CollectionStaticProperty";
    memberType: string;
    members: StaticPropertyUnion[];
    staticPropertyTemplate: StaticPropertyUnion;

    static fromData(data: CollectionStaticProperty, target?: CollectionStaticProperty): CollectionStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new CollectionStaticProperty();
        super.fromData(data, instance);
        instance.staticPropertyTemplate = StaticProperty.fromDataUnion(data.staticPropertyTemplate);
        instance.members = __getCopyArrayFn(StaticProperty.fromDataUnion)(data.members);
        instance.memberType = data.memberType;
        return instance;
    }
}

export class ColorPickerStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty";
    selectedColor: string;

    static fromData(data: ColorPickerStaticProperty, target?: ColorPickerStaticProperty): ColorPickerStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new ColorPickerStaticProperty();
        super.fromData(data, instance);
        instance.selectedColor = data.selectedColor;
        return instance;
    }
}

export class CustomOutputStrategy extends OutputStrategy {
    "@class": "org.apache.streampipes.model.output.CustomOutputStrategy";
    availablePropertyKeys: string[];
    outputRight: boolean;
    selectedPropertyKeys: string[];

    static fromData(data: CustomOutputStrategy, target?: CustomOutputStrategy): CustomOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new CustomOutputStrategy();
        super.fromData(data, instance);
        instance.selectedPropertyKeys = __getCopyArrayFn(__identity<string>())(data.selectedPropertyKeys);
        instance.outputRight = data.outputRight;
        instance.availablePropertyKeys = __getCopyArrayFn(__identity<string>())(data.availablePropertyKeys);
        return instance;
    }
}

export class CustomTransformOutputStrategy extends OutputStrategy {
    "@class": "org.apache.streampipes.model.output.CustomTransformOutputStrategy";
    eventProperties: EventPropertyUnion[];

    static fromData(data: CustomTransformOutputStrategy, target?: CustomTransformOutputStrategy): CustomTransformOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new CustomTransformOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(EventProperty.fromDataUnion)(data.eventProperties);
        return instance;
    }
}

export class NamedStreamPipesEntity extends AbstractStreamPipesEntity {
    "@class": "org.apache.streampipes.model.base.NamedStreamPipesEntity" | "org.apache.streampipes.model.graph.DataSourceDescription" | "org.apache.streampipes.model.SpDataStream" | "org.apache.streampipes.model.SpDataSet" | "org.apache.streampipes.model.base.InvocableStreamPipesEntity" | "org.apache.streampipes.model.graph.DataProcessorInvocation" | "org.apache.streampipes.model.graph.DataSinkInvocation";
    appId: string;
    applicationLinks: ApplicationLink[];
    connectedTo: string[];
    description: string;
    dom: string;
    elementId: string;
    iconUrl: string;
    includedAssets: string[];
    includedLocales: string[];
    includesAssets: boolean;
    includesLocales: boolean;
    name: string;
    uri: string;

    static fromData(data: NamedStreamPipesEntity, target?: NamedStreamPipesEntity): NamedStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new NamedStreamPipesEntity();
        super.fromData(data, instance);
        instance.name = data.name;
        instance.description = data.description;
        instance.iconUrl = data.iconUrl;
        instance.elementId = data.elementId;
        instance.appId = data.appId;
        instance.includesAssets = data.includesAssets;
        instance.includesLocales = data.includesLocales;
        instance.includedAssets = __getCopyArrayFn(__identity<string>())(data.includedAssets);
        instance.includedLocales = __getCopyArrayFn(__identity<string>())(data.includedLocales);
        instance.applicationLinks = __getCopyArrayFn(ApplicationLink.fromData)(data.applicationLinks);
        instance.connectedTo = __getCopyArrayFn(__identity<string>())(data.connectedTo);
        instance.dom = data.dom;
        instance.uri = data.uri;
        return instance;
    }
}

export class InvocableStreamPipesEntity extends NamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.base.InvocableStreamPipesEntity" | "org.apache.streampipes.model.graph.DataProcessorInvocation" | "org.apache.streampipes.model.graph.DataSinkInvocation";
    belongsTo: string;
    configured: boolean;
    correspondingPipeline: string;
    correspondingUser: string;
    inputStreams: SpDataStreamUnion[];
    staticProperties: StaticPropertyUnion[];
    statusInfoSettings: ElementStatusInfoSettings;
    streamRequirements: SpDataStreamUnion[];
    supportedGrounding: EventGrounding;
    uncompleted: boolean;

    static fromData(data: InvocableStreamPipesEntity, target?: InvocableStreamPipesEntity): InvocableStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new InvocableStreamPipesEntity();
        super.fromData(data, instance);
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(data.inputStreams);
        instance.staticProperties = __getCopyArrayFn(StaticProperty.fromDataUnion)(data.staticProperties);
        instance.belongsTo = data.belongsTo;
        instance.statusInfoSettings = ElementStatusInfoSettings.fromData(data.statusInfoSettings);
        instance.supportedGrounding = EventGrounding.fromData(data.supportedGrounding);
        instance.correspondingPipeline = data.correspondingPipeline;
        instance.correspondingUser = data.correspondingUser;
        instance.streamRequirements = __getCopyArrayFn(SpDataStream.fromDataUnion)(data.streamRequirements);
        instance.configured = data.configured;
        instance.uncompleted = data.uncompleted;
        return instance;
    }
}

export class DataProcessorInvocation extends InvocableStreamPipesEntity {
    "@class": "org.apache.streampipes.model.graph.DataProcessorInvocation";
    category: string[];
    outputStrategies: OutputStrategyUnion[];
    outputStream: SpDataStreamUnion;
    pathName: string;

    static fromData(data: DataProcessorInvocation, target?: DataProcessorInvocation): DataProcessorInvocation {
        if (!data) {
            return data;
        }
        const instance = target || new DataProcessorInvocation();
        super.fromData(data, instance);
        instance.outputStream = SpDataStream.fromDataUnion(data.outputStream);
        instance.outputStrategies = __getCopyArrayFn(OutputStrategy.fromDataUnion)(data.outputStrategies);
        instance.pathName = data.pathName;
        instance.category = __getCopyArrayFn(__identity<string>())(data.category);
        return instance;
    }
}

export class DataProcessorType {
    code: string;
    description: string;
    label: string;

    static fromData(data: DataProcessorType, target?: DataProcessorType): DataProcessorType {
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

export class DataSinkInvocation extends InvocableStreamPipesEntity {
    "@class": "org.apache.streampipes.model.graph.DataSinkInvocation";
    category: string[];

    static fromData(data: DataSinkInvocation, target?: DataSinkInvocation): DataSinkInvocation {
        if (!data) {
            return data;
        }
        const instance = target || new DataSinkInvocation();
        super.fromData(data, instance);
        instance.category = __getCopyArrayFn(__identity<string>())(data.category);
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
    "@class": "org.apache.streampipes.model.graph.DataSourceDescription";
    correspondingSourceId: string;
    spDataStreams: SpDataStreamUnion[];

    static fromData(data: DataSourceDescription, target?: DataSourceDescription): DataSourceDescription {
        if (!data) {
            return data;
        }
        const instance = target || new DataSourceDescription();
        super.fromData(data, instance);
        instance.spDataStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(data.spDataStreams);
        instance.correspondingSourceId = data.correspondingSourceId;
        return instance;
    }
}

export class DomainStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.DomainStaticProperty";
    requiredClass: string;
    supportedProperties: SupportedProperty[];

    static fromData(data: DomainStaticProperty, target?: DomainStaticProperty): DomainStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new DomainStaticProperty();
        super.fromData(data, instance);
        instance.requiredClass = data.requiredClass;
        instance.supportedProperties = __getCopyArrayFn(SupportedProperty.fromData)(data.supportedProperties);
        return instance;
    }
}

export class ElementComposition {
    description: string;
    name: string;
    sepas: DataProcessorInvocation[];
    streams: SpDataStreamUnion[];

    static fromData(data: ElementComposition, target?: ElementComposition): ElementComposition {
        if (!data) {
            return data;
        }
        const instance = target || new ElementComposition();
        instance.sepas = __getCopyArrayFn(DataProcessorInvocation.fromData)(data.sepas);
        instance.streams = __getCopyArrayFn(SpDataStream.fromDataUnion)(data.streams);
        instance.name = data.name;
        instance.description = data.description;
        return instance;
    }
}

export class ElementStatusInfoSettings extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.monitoring.ElementStatusInfoSettings";
    elementIdentifier: string;
    errorTopic: string;
    kafkaHost: string;
    kafkaPort: number;
    statsTopic: string;

    static fromData(data: ElementStatusInfoSettings, target?: ElementStatusInfoSettings): ElementStatusInfoSettings {
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
    "@class": "org.apache.streampipes.model.schema.ValueSpecification" | "org.apache.streampipes.model.schema.QuantitativeValue" | "org.apache.streampipes.model.schema.Enumeration";

    static fromData(data: ValueSpecification, target?: ValueSpecification): ValueSpecification {
        if (!data) {
            return data;
        }
        const instance = target || new ValueSpecification();
        super.fromData(data, instance);
        return instance;
    }

    static fromDataUnion(data: ValueSpecificationUnion): ValueSpecificationUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.schema.QuantitativeValue":
                return QuantitativeValue.fromData(data);
            case "org.apache.streampipes.model.schema.Enumeration":
                return Enumeration.fromData(data);
        }
    }
}

export class Enumeration extends ValueSpecification {
    "@class": "org.apache.streampipes.model.schema.Enumeration";
    description: string;
    label: string;
    runtimeValues: string[];

    static fromData(data: Enumeration, target?: Enumeration): Enumeration {
        if (!data) {
            return data;
        }
        const instance = target || new Enumeration();
        super.fromData(data, instance);
        instance.label = data.label;
        instance.description = data.description;
        instance.runtimeValues = __getCopyArrayFn(__identity<string>())(data.runtimeValues);
        return instance;
    }
}

export class EventGrounding extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.grounding.EventGrounding";
    transportFormats: TransportFormat[];
    transportProtocol: TransportProtocolUnion;
    transportProtocols: TransportProtocolUnion[];

    static fromData(data: EventGrounding, target?: EventGrounding): EventGrounding {
        if (!data) {
            return data;
        }
        const instance = target || new EventGrounding();
        super.fromData(data, instance);
        instance.transportProtocols = __getCopyArrayFn(TransportProtocol.fromDataUnion)(data.transportProtocols);
        instance.transportFormats = __getCopyArrayFn(TransportFormat.fromData)(data.transportFormats);
        instance.transportProtocol = TransportProtocol.fromDataUnion(data.transportProtocol);
        return instance;
    }
}

export class EventProperty extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.schema.EventProperty" | "org.apache.streampipes.model.schema.EventPropertyList" | "org.apache.streampipes.model.schema.EventPropertyNested" | "org.apache.streampipes.model.schema.EventPropertyPrimitive";
    description: string;
    domainProperties: string[];
    eventPropertyQualities: EventPropertyQualityDefinitionUnion[];
    index: number;
    label: string;
    propertyScope: string;
    required: boolean;
    requiresEventPropertyQualities: EventPropertyQualityRequirement[];
    runtimeId: string;
    runtimeName: string;

    static fromData(data: EventProperty, target?: EventProperty): EventProperty {
        if (!data) {
            return data;
        }
        const instance = target || new EventProperty();
        super.fromData(data, instance);
        instance.label = data.label;
        instance.description = data.description;
        instance.runtimeName = data.runtimeName;
        instance.required = data.required;
        instance.domainProperties = __getCopyArrayFn(__identity<string>())(data.domainProperties);
        instance.eventPropertyQualities = __getCopyArrayFn(EventPropertyQualityDefinition.fromDataUnion)(data.eventPropertyQualities);
        instance.requiresEventPropertyQualities = __getCopyArrayFn(EventPropertyQualityRequirement.fromData)(data.requiresEventPropertyQualities);
        instance.propertyScope = data.propertyScope;
        instance.index = data.index;
        instance.runtimeId = data.runtimeId;
        return instance;
    }

    static fromDataUnion(data: EventPropertyUnion): EventPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.schema.EventPropertyList":
                return EventPropertyList.fromData(data);
            case "org.apache.streampipes.model.schema.EventPropertyNested":
                return EventPropertyNested.fromData(data);
            case "org.apache.streampipes.model.schema.EventPropertyPrimitive":
                return EventPropertyPrimitive.fromData(data);
        }
    }
}

export class EventPropertyList extends EventProperty {
    "@class": "org.apache.streampipes.model.schema.EventPropertyList";
    eventProperty: EventPropertyUnion;

    static fromData(data: EventPropertyList, target?: EventPropertyList): EventPropertyList {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyList();
        super.fromData(data, instance);
        instance.eventProperty = EventProperty.fromDataUnion(data.eventProperty);
        return instance;
    }
}

export class EventPropertyNested extends EventProperty {
    "@class": "org.apache.streampipes.model.schema.EventPropertyNested";
    eventProperties: EventPropertyUnion[];

    static fromData(data: EventPropertyNested, target?: EventPropertyNested): EventPropertyNested {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyNested();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(EventProperty.fromDataUnion)(data.eventProperties);
        return instance;
    }
}

export class EventPropertyPrimitive extends EventProperty {
    "@class": "org.apache.streampipes.model.schema.EventPropertyPrimitive";
    measurementUnit: string;
    runtimeType: string;
    valueSpecification: ValueSpecificationUnion;

    static fromData(data: EventPropertyPrimitive, target?: EventPropertyPrimitive): EventPropertyPrimitive {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyPrimitive();
        super.fromData(data, instance);
        instance.runtimeType = data.runtimeType;
        instance.measurementUnit = data.measurementUnit;
        instance.valueSpecification = ValueSpecification.fromDataUnion(data.valueSpecification);
        return instance;
    }
}

export class EventPropertyQualityRequirement extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.quality.EventPropertyQualityRequirement";
    maximumPropertyQuality: EventPropertyQualityDefinitionUnion;
    minimumPropertyQuality: EventPropertyQualityDefinitionUnion;

    static fromData(data: EventPropertyQualityRequirement, target?: EventPropertyQualityRequirement): EventPropertyQualityRequirement {
        if (!data) {
            return data;
        }
        const instance = target || new EventPropertyQualityRequirement();
        super.fromData(data, instance);
        instance.minimumPropertyQuality = EventPropertyQualityDefinition.fromDataUnion(data.minimumPropertyQuality);
        instance.maximumPropertyQuality = EventPropertyQualityDefinition.fromDataUnion(data.maximumPropertyQuality);
        return instance;
    }
}

export class EventSchema extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.schema.EventSchema";
    eventProperties: EventPropertyUnion[];

    static fromData(data: EventSchema, target?: EventSchema): EventSchema {
        if (!data) {
            return data;
        }
        const instance = target || new EventSchema();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(EventProperty.fromDataUnion)(data.eventProperties);
        return instance;
    }
}

export class EventStreamQualityDefinition extends MeasurementProperty {
    "@class": "org.apache.streampipes.model.quality.EventStreamQualityDefinition" | "org.apache.streampipes.model.quality.Frequency" | "org.apache.streampipes.model.quality.Latency";

    static fromData(data: EventStreamQualityDefinition, target?: EventStreamQualityDefinition): EventStreamQualityDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new EventStreamQualityDefinition();
        super.fromData(data, instance);
        return instance;
    }

    static fromDataUnion(data: EventStreamQualityDefinitionUnion): EventStreamQualityDefinitionUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.quality.Frequency":
                return Frequency.fromData(data);
            case "org.apache.streampipes.model.quality.Latency":
                return Latency.fromData(data);
        }
    }
}

export class EventStreamQualityRequirement extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.quality.EventStreamQualityRequirement";
    maximumStreamQuality: EventStreamQualityDefinitionUnion;
    minimumStreamQuality: EventStreamQualityDefinitionUnion;

    static fromData(data: EventStreamQualityRequirement, target?: EventStreamQualityRequirement): EventStreamQualityRequirement {
        if (!data) {
            return data;
        }
        const instance = target || new EventStreamQualityRequirement();
        super.fromData(data, instance);
        instance.minimumStreamQuality = EventStreamQualityDefinition.fromDataUnion(data.minimumStreamQuality);
        instance.maximumStreamQuality = EventStreamQualityDefinition.fromDataUnion(data.maximumStreamQuality);
        return instance;
    }
}

export class FileStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.FileStaticProperty";
    endpointUrl: string;
    locationPath: string;

    static fromData(data: FileStaticProperty, target?: FileStaticProperty): FileStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new FileStaticProperty();
        super.fromData(data, instance);
        instance.endpointUrl = data.endpointUrl;
        instance.locationPath = data.locationPath;
        return instance;
    }
}

export class FixedOutputStrategy extends OutputStrategy {
    "@class": "org.apache.streampipes.model.output.FixedOutputStrategy";
    eventProperties: EventPropertyUnion[];

    static fromData(data: FixedOutputStrategy, target?: FixedOutputStrategy): FixedOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new FixedOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(EventProperty.fromDataUnion)(data.eventProperties);
        return instance;
    }
}

export class FreeTextStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.FreeTextStaticProperty";
    htmlAllowed: boolean;
    mapsTo: string;
    multiLine: boolean;
    placeholdersSupported: boolean;
    requiredDatatype: string;
    requiredDomainProperty: string;
    value: string;
    valueSpecification: PropertyValueSpecification;

    static fromData(data: FreeTextStaticProperty, target?: FreeTextStaticProperty): FreeTextStaticProperty {
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
        instance.placeholdersSupported = data.placeholdersSupported;
        instance.valueSpecification = PropertyValueSpecification.fromData(data.valueSpecification);
        return instance;
    }
}

export class Frequency extends EventStreamQualityDefinition {
    "@class": "org.apache.streampipes.model.quality.Frequency";
    quantityValue: number;

    static fromData(data: Frequency, target?: Frequency): Frequency {
        if (!data) {
            return data;
        }
        const instance = target || new Frequency();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class TransportProtocol extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.grounding.TransportProtocol" | "org.apache.streampipes.model.grounding.JmsTransportProtocol" | "org.apache.streampipes.model.grounding.KafkaTransportProtocol" | "org.apache.streampipes.model.grounding.MqttTransportProtocol";
    brokerHostname: string;
    topicDefinition: TopicDefinitionUnion;

    static fromData(data: TransportProtocol, target?: TransportProtocol): TransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new TransportProtocol();
        super.fromData(data, instance);
        instance.brokerHostname = data.brokerHostname;
        instance.topicDefinition = TopicDefinition.fromDataUnion(data.topicDefinition);
        return instance;
    }

    static fromDataUnion(data: TransportProtocolUnion): TransportProtocolUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.grounding.JmsTransportProtocol":
                return JmsTransportProtocol.fromData(data);
            case "org.apache.streampipes.model.grounding.KafkaTransportProtocol":
                return KafkaTransportProtocol.fromData(data);
            case "org.apache.streampipes.model.grounding.MqttTransportProtocol":
                return MqttTransportProtocol.fromData(data);
        }
    }
}

export class JmsTransportProtocol extends TransportProtocol {
    "@class": "org.apache.streampipes.model.grounding.JmsTransportProtocol";
    port: number;

    static fromData(data: JmsTransportProtocol, target?: JmsTransportProtocol): JmsTransportProtocol {
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
    "@class": "org.apache.streampipes.model.grounding.KafkaTransportProtocol";
    acks: string;
    batchSize: string;
    groupId: string;
    kafkaPort: number;
    lingerMs: number;
    messageMaxBytes: string;
    offset: string;
    zookeeperHost: string;
    zookeeperPort: number;

    static fromData(data: KafkaTransportProtocol, target?: KafkaTransportProtocol): KafkaTransportProtocol {
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
        instance.acks = data.acks;
        instance.batchSize = data.batchSize;
        instance.offset = data.offset;
        instance.groupId = data.groupId;
        return instance;
    }
}

export class KeepOutputStrategy extends OutputStrategy {
    "@class": "org.apache.streampipes.model.output.KeepOutputStrategy";
    eventName: string;
    keepBoth: boolean;

    static fromData(data: KeepOutputStrategy, target?: KeepOutputStrategy): KeepOutputStrategy {
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

export class Latency extends EventStreamQualityDefinition {
    "@class": "org.apache.streampipes.model.quality.Latency";
    quantityValue: number;

    static fromData(data: Latency, target?: Latency): Latency {
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
    "@class": "org.apache.streampipes.model.output.ListOutputStrategy";
    propertyName: string;

    static fromData(data: ListOutputStrategy, target?: ListOutputStrategy): ListOutputStrategy {
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
    "@class": "org.apache.streampipes.model.staticproperty.MappingProperty" | "org.apache.streampipes.model.staticproperty.MappingPropertyUnary" | "org.apache.streampipes.model.staticproperty.MappingPropertyNary";
    mapsFromOptions: string[];
    propertyScope: string;
    requirementSelector: string;

    static fromData(data: MappingProperty, target?: MappingProperty): MappingProperty {
        if (!data) {
            return data;
        }
        const instance = target || new MappingProperty();
        super.fromData(data, instance);
        instance.requirementSelector = data.requirementSelector;
        instance.mapsFromOptions = __getCopyArrayFn(__identity<string>())(data.mapsFromOptions);
        instance.propertyScope = data.propertyScope;
        return instance;
    }

    static fromDataUnion(data: MappingPropertyUnion): MappingPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.staticproperty.MappingPropertyNary":
                return MappingPropertyNary.fromData(data);
            case "org.apache.streampipes.model.staticproperty.MappingPropertyUnary":
                return MappingPropertyUnary.fromData(data);
        }
    }
}

export class MappingPropertyNary extends MappingProperty {
    "@class": "org.apache.streampipes.model.staticproperty.MappingPropertyNary";
    selectedProperties: string[];

    static fromData(data: MappingPropertyNary, target?: MappingPropertyNary): MappingPropertyNary {
        if (!data) {
            return data;
        }
        const instance = target || new MappingPropertyNary();
        super.fromData(data, instance);
        instance.selectedProperties = __getCopyArrayFn(__identity<string>())(data.selectedProperties);
        return instance;
    }
}

export class MappingPropertyUnary extends MappingProperty {
    "@class": "org.apache.streampipes.model.staticproperty.MappingPropertyUnary";
    selectedProperty: string;

    static fromData(data: MappingPropertyUnary, target?: MappingPropertyUnary): MappingPropertyUnary {
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
    "@class": "org.apache.streampipes.model.staticproperty.MatchingStaticProperty";
    matchLeft: string;
    matchRight: string;

    static fromData(data: MatchingStaticProperty, target?: MatchingStaticProperty): MatchingStaticProperty {
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
    "@class": "org.apache.streampipes.model.quality.MeasurementCapability";
    capability: string;

    static fromData(data: MeasurementCapability, target?: MeasurementCapability): MeasurementCapability {
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
    "@class": "org.apache.streampipes.model.quality.MeasurementObject";
    measuresObject: string;

    static fromData(data: MeasurementObject, target?: MeasurementObject): MeasurementObject {
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
    "@class": "org.apache.streampipes.model.quality.MeasurementRange";
    maxValue: number;
    minValue: number;

    static fromData(data: MeasurementRange, target?: MeasurementRange): MeasurementRange {
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
        instance.notifications = __getCopyArrayFn(Notification.fromData)(data.notifications);
        return instance;
    }
}

export class MqttTransportProtocol extends TransportProtocol {
    "@class": "org.apache.streampipes.model.grounding.MqttTransportProtocol";
    port: number;

    static fromData(data: MqttTransportProtocol, target?: MqttTransportProtocol): MqttTransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new MqttTransportProtocol();
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
    "@class": "org.apache.streampipes.model.staticproperty.OneOfStaticProperty" | "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty";

    static fromData(data: OneOfStaticProperty, target?: OneOfStaticProperty): OneOfStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new OneOfStaticProperty();
        super.fromData(data, instance);
        return instance;
    }

    static fromDataUnion(data: OneOfStaticPropertyUnion): OneOfStaticPropertyUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty":
                return RuntimeResolvableOneOfStaticProperty.fromData(data);
        }
    }
}

export class Option extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.staticproperty.Option";
    internalName: string;
    name: string;
    selected: boolean;

    static fromData(data: Option, target?: Option): Option {
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

export class Pipeline extends ElementComposition {
    _id: string;
    _rev: string;
    actions: DataSinkInvocation[];
    createdAt: number;
    createdByUser: string;
    pipelineCategories: string[];
    publicElement: boolean;
    running: boolean;
    startedAt: number;

    static fromData(data: Pipeline, target?: Pipeline): Pipeline {
        if (!data) {
            return data;
        }
        const instance = target || new Pipeline();
        super.fromData(data, instance);
        instance.actions = __getCopyArrayFn(DataSinkInvocation.fromData)(data.actions);
        instance.running = data.running;
        instance.startedAt = data.startedAt;
        instance.createdAt = data.createdAt;
        instance.publicElement = data.publicElement;
        instance.createdByUser = data.createdByUser;
        instance.pipelineCategories = __getCopyArrayFn(__identity<string>())(data.pipelineCategories);
        instance._id = data._id;
        instance._rev = data._rev;
        return instance;
    }
}

export class PipelineModification {
    domId: string;
    elementId: string;
    inputStreams: SpDataStreamUnion[];
    outputStrategies: OutputStrategyUnion[];
    staticProperties: StaticPropertyUnion[];

    static fromData(data: PipelineModification, target?: PipelineModification): PipelineModification {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineModification();
        instance.domId = data.domId;
        instance.elementId = data.elementId;
        instance.staticProperties = __getCopyArrayFn(StaticProperty.fromDataUnion)(data.staticProperties);
        instance.outputStrategies = __getCopyArrayFn(OutputStrategy.fromDataUnion)(data.outputStrategies);
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromDataUnion)(data.inputStreams);
        return instance;
    }
}

export class PipelineModificationMessage extends Message {
    pipelineModifications: PipelineModification[];

    static fromData(data: PipelineModificationMessage, target?: PipelineModificationMessage): PipelineModificationMessage {
        if (!data) {
            return data;
        }
        const instance = target || new PipelineModificationMessage();
        super.fromData(data, instance);
        instance.pipelineModifications = __getCopyArrayFn(PipelineModification.fromData)(data.pipelineModifications);
        return instance;
    }
}

export class Precision extends EventPropertyQualityDefinition {
    "@class": "org.apache.streampipes.model.quality.Precision";
    quantityValue: number;

    static fromData(data: Precision, target?: Precision): Precision {
        if (!data) {
            return data;
        }
        const instance = target || new Precision();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class PropertyRenameRule extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.output.PropertyRenameRule";
    newRuntimeName: string;
    runtimeId: string;

    static fromData(data: PropertyRenameRule, target?: PropertyRenameRule): PropertyRenameRule {
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
    "@class": "org.apache.streampipes.model.staticproperty.PropertyValueSpecification";
    maxValue: number;
    minValue: number;
    step: number;

    static fromData(data: PropertyValueSpecification, target?: PropertyValueSpecification): PropertyValueSpecification {
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

export class QuantitativeValue extends ValueSpecification {
    "@class": "org.apache.streampipes.model.schema.QuantitativeValue";
    maxValue: number;
    minValue: number;
    step: number;

    static fromData(data: QuantitativeValue, target?: QuantitativeValue): QuantitativeValue {
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

export class Resolution extends EventPropertyQualityDefinition {
    "@class": "org.apache.streampipes.model.quality.Resolution";
    quantityValue: number;

    static fromData(data: Resolution, target?: Resolution): Resolution {
        if (!data) {
            return data;
        }
        const instance = target || new Resolution();
        super.fromData(data, instance);
        instance.quantityValue = data.quantityValue;
        return instance;
    }
}

export class RuntimeResolvableAnyStaticProperty extends AnyStaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty";
    dependsOn: string[];

    static fromData(data: RuntimeResolvableAnyStaticProperty, target?: RuntimeResolvableAnyStaticProperty): RuntimeResolvableAnyStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new RuntimeResolvableAnyStaticProperty();
        super.fromData(data, instance);
        instance.dependsOn = __getCopyArrayFn(__identity<string>())(data.dependsOn);
        return instance;
    }
}

export class RuntimeResolvableOneOfStaticProperty extends OneOfStaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty";
    dependsOn: string[];

    static fromData(data: RuntimeResolvableOneOfStaticProperty, target?: RuntimeResolvableOneOfStaticProperty): RuntimeResolvableOneOfStaticProperty {
        if (!data) {
            return data;
        }
        const instance = target || new RuntimeResolvableOneOfStaticProperty();
        super.fromData(data, instance);
        instance.dependsOn = __getCopyArrayFn(__identity<string>())(data.dependsOn);
        return instance;
    }
}

export class SecretStaticProperty extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.SecretStaticProperty";
    encrypted: boolean;
    value: string;

    static fromData(data: SecretStaticProperty, target?: SecretStaticProperty): SecretStaticProperty {
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
    "@class": "org.apache.streampipes.model.grounding.TopicDefinition" | "org.apache.streampipes.model.grounding.SimpleTopicDefinition" | "org.apache.streampipes.model.grounding.WildcardTopicDefinition";
    actualTopicName: string;

    static fromData(data: TopicDefinition, target?: TopicDefinition): TopicDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new TopicDefinition();
        super.fromData(data, instance);
        instance.actualTopicName = data.actualTopicName;
        return instance;
    }

    static fromDataUnion(data: TopicDefinitionUnion): TopicDefinitionUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.grounding.SimpleTopicDefinition":
                return SimpleTopicDefinition.fromData(data);
            case "org.apache.streampipes.model.grounding.WildcardTopicDefinition":
                return WildcardTopicDefinition.fromData(data);
        }
    }
}

export class SimpleTopicDefinition extends TopicDefinition {
    "@class": "org.apache.streampipes.model.grounding.SimpleTopicDefinition";

    static fromData(data: SimpleTopicDefinition, target?: SimpleTopicDefinition): SimpleTopicDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new SimpleTopicDefinition();
        super.fromData(data, instance);
        return instance;
    }
}

export class SpDataStream extends NamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.SpDataStream" | "org.apache.streampipes.model.SpDataSet";
    category: string[];
    eventGrounding: EventGrounding;
    eventSchema: EventSchema;
    hasEventStreamQualities: EventStreamQualityDefinitionUnion[];
    index: number;
    measurementCapability: MeasurementCapability[];
    measurementObject: MeasurementObject[];
    requiresEventStreamQualities: EventStreamQualityRequirement[];

    static fromData(data: SpDataStream, target?: SpDataStream): SpDataStream {
        if (!data) {
            return data;
        }
        const instance = target || new SpDataStream();
        super.fromData(data, instance);
        instance.hasEventStreamQualities = __getCopyArrayFn(EventStreamQualityDefinition.fromDataUnion)(data.hasEventStreamQualities);
        instance.requiresEventStreamQualities = __getCopyArrayFn(EventStreamQualityRequirement.fromData)(data.requiresEventStreamQualities);
        instance.eventGrounding = EventGrounding.fromData(data.eventGrounding);
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.measurementCapability = __getCopyArrayFn(MeasurementCapability.fromData)(data.measurementCapability);
        instance.measurementObject = __getCopyArrayFn(MeasurementObject.fromData)(data.measurementObject);
        instance.index = data.index;
        instance.category = __getCopyArrayFn(__identity<string>())(data.category);
        return instance;
    }

    static fromDataUnion(data: SpDataStreamUnion): SpDataStreamUnion {
        if (!data) {
            return data;
        }
        switch (data["@class"]) {
            case "org.apache.streampipes.model.SpDataStream":
                return SpDataStream.fromData(data);
            case "org.apache.streampipes.model.SpDataSet":
                return SpDataSet.fromData(data);
        }
    }
}

export class SpDataSet extends SpDataStream {
    "@class": "org.apache.streampipes.model.SpDataSet";
    actualTopicName: string;
    brokerHostname: string;
    correspondingPipeline: string;
    datasetInvocationId: string;
    supportedGrounding: EventGrounding;

    static fromData(data: SpDataSet, target?: SpDataSet): SpDataSet {
        if (!data) {
            return data;
        }
        const instance = target || new SpDataSet();
        super.fromData(data, instance);
        instance.supportedGrounding = EventGrounding.fromData(data.supportedGrounding);
        instance.datasetInvocationId = data.datasetInvocationId;
        instance.correspondingPipeline = data.correspondingPipeline;
        instance.brokerHostname = data.brokerHostname;
        instance.actualTopicName = data.actualTopicName;
        return instance;
    }
}

export class StaticPropertyAlternative extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.StaticPropertyAlternative";
    selected: boolean;
    staticProperty: StaticPropertyUnion;

    static fromData(data: StaticPropertyAlternative, target?: StaticPropertyAlternative): StaticPropertyAlternative {
        if (!data) {
            return data;
        }
        const instance = target || new StaticPropertyAlternative();
        super.fromData(data, instance);
        instance.selected = data.selected;
        instance.staticProperty = StaticProperty.fromDataUnion(data.staticProperty);
        return instance;
    }
}

export class StaticPropertyAlternatives extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives";
    alternatives: StaticPropertyAlternative[];

    static fromData(data: StaticPropertyAlternatives, target?: StaticPropertyAlternatives): StaticPropertyAlternatives {
        if (!data) {
            return data;
        }
        const instance = target || new StaticPropertyAlternatives();
        super.fromData(data, instance);
        instance.alternatives = __getCopyArrayFn(StaticPropertyAlternative.fromData)(data.alternatives);
        return instance;
    }
}

export class StaticPropertyGroup extends StaticProperty {
    "@class": "org.apache.streampipes.model.staticproperty.StaticPropertyGroup";
    horizontalRendering: boolean;
    showLabel: boolean;
    staticProperties: StaticPropertyUnion[];

    static fromData(data: StaticPropertyGroup, target?: StaticPropertyGroup): StaticPropertyGroup {
        if (!data) {
            return data;
        }
        const instance = target || new StaticPropertyGroup();
        super.fromData(data, instance);
        instance.staticProperties = __getCopyArrayFn(StaticProperty.fromDataUnion)(data.staticProperties);
        instance.showLabel = data.showLabel;
        instance.horizontalRendering = data.horizontalRendering;
        return instance;
    }
}

export class SupportedProperty extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.staticproperty.SupportedProperty";
    propertyId: string;
    value: string;
    valueRequired: boolean;

    static fromData(data: SupportedProperty, target?: SupportedProperty): SupportedProperty {
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

export class TransformOperation extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.output.TransformOperation";
    mappingPropertyInternalName: string;
    sourceStaticProperty: string;
    targetValue: string;
    transformationScope: string;

    static fromData(data: TransformOperation, target?: TransformOperation): TransformOperation {
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
    "@class": "org.apache.streampipes.model.output.TransformOutputStrategy";
    transformOperations: TransformOperation[];

    static fromData(data: TransformOutputStrategy, target?: TransformOutputStrategy): TransformOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new TransformOutputStrategy();
        super.fromData(data, instance);
        instance.transformOperations = __getCopyArrayFn(TransformOperation.fromData)(data.transformOperations);
        return instance;
    }
}

export class TransportFormat extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.grounding.TransportFormat";
    rdfType: string[];

    static fromData(data: TransportFormat, target?: TransportFormat): TransportFormat {
        if (!data) {
            return data;
        }
        const instance = target || new TransportFormat();
        super.fromData(data, instance);
        instance.rdfType = __getCopyArrayFn(__identity<string>())(data.rdfType);
        return instance;
    }
}

export class UserDefinedOutputStrategy extends OutputStrategy {
    "@class": "org.apache.streampipes.model.output.UserDefinedOutputStrategy";
    eventProperties: EventPropertyUnion[];

    static fromData(data: UserDefinedOutputStrategy, target?: UserDefinedOutputStrategy): UserDefinedOutputStrategy {
        if (!data) {
            return data;
        }
        const instance = target || new UserDefinedOutputStrategy();
        super.fromData(data, instance);
        instance.eventProperties = __getCopyArrayFn(EventProperty.fromDataUnion)(data.eventProperties);
        return instance;
    }
}

export class WildcardTopicDefinition extends TopicDefinition {
    "@class": "org.apache.streampipes.model.grounding.WildcardTopicDefinition";
    wildcardTopicMappings: WildcardTopicMapping[];
    wildcardTopicName: string;

    static fromData(data: WildcardTopicDefinition, target?: WildcardTopicDefinition): WildcardTopicDefinition {
        if (!data) {
            return data;
        }
        const instance = target || new WildcardTopicDefinition();
        super.fromData(data, instance);
        instance.wildcardTopicName = data.wildcardTopicName;
        instance.wildcardTopicMappings = __getCopyArrayFn(WildcardTopicMapping.fromData)(data.wildcardTopicMappings);
        return instance;
    }
}

export class WildcardTopicMapping extends UnnamedStreamPipesEntity {
    "@class": "org.apache.streampipes.model.grounding.WildcardTopicMapping";
    mappedRuntimeName: string;
    mappingId: string;
    selectedMapping: string;
    topicParameterType: string;

    static fromData(data: WildcardTopicMapping, target?: WildcardTopicMapping): WildcardTopicMapping {
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

export type EventPropertyQualityDefinitionUnion = Accuracy | MeasurementRange | Precision | Resolution;

export type EventPropertyUnion = EventPropertyList | EventPropertyNested | EventPropertyPrimitive;

export type EventStreamQualityDefinitionUnion = Frequency | Latency;

export type MappingPropertyUnion = MappingPropertyNary | MappingPropertyUnary;

export type MeasurementPropertyUnion = EventPropertyQualityDefinition | EventStreamQualityDefinition;

export type OneOfStaticPropertyUnion = RuntimeResolvableOneOfStaticProperty;

export type OutputStrategyUnion = AppendOutputStrategy | CustomOutputStrategy | CustomTransformOutputStrategy | FixedOutputStrategy | KeepOutputStrategy | ListOutputStrategy | TransformOutputStrategy | UserDefinedOutputStrategy;

export type SelectionStaticPropertyUnion = AnyStaticProperty | OneOfStaticProperty;

export type SpDataStreamUnion = SpDataStream | SpDataSet;

export type StaticPropertyType = "AnyStaticProperty" | "CollectionStaticProperty" | "ColorPickerStaticProperty" | "DomainStaticProperty" | "FreeTextStaticProperty" | "FileStaticProperty" | "MappingPropertyUnary" | "MappingPropertyNary" | "MatchingStaticProperty" | "OneOfStaticProperty" | "RuntimeResolvableAnyStaticProperty" | "RuntimeResolvableOneOfStaticProperty" | "StaticPropertyGroup" | "StaticPropertyAlternatives" | "StaticPropertyAlternative" | "SecretStaticProperty" | "CodeInputStaticProperty";

export type StaticPropertyUnion = AnyStaticProperty | CodeInputStaticProperty | CollectionStaticProperty | ColorPickerStaticProperty | DomainStaticProperty | FileStaticProperty | FreeTextStaticProperty | MappingPropertyUnary | MappingPropertyNary | MatchingStaticProperty | OneOfStaticProperty | RuntimeResolvableAnyStaticProperty | RuntimeResolvableOneOfStaticProperty | SecretStaticProperty | StaticPropertyAlternative | StaticPropertyAlternatives | StaticPropertyGroup;

export type TopicDefinitionUnion = SimpleTopicDefinition | WildcardTopicDefinition;

export type TransportProtocolUnion = JmsTransportProtocol | KafkaTransportProtocol | MqttTransportProtocol;

export type ValueSpecificationUnion = QuantitativeValue | Enumeration;

function __getCopyArrayFn<T>(itemCopyFn: (item: T) => T): (array: T[]) => T[] {
    return (array: T[]) => __copyArray(array, itemCopyFn);
}

function __copyArray<T>(array: T[], itemCopyFn: (item: T) => T): T[] {
    return array && array.map(item => item && itemCopyFn(item));
}

function __getCopyObjectFn<T>(itemCopyFn: (item: T) => T): (object: { [index: string]: T }) => { [index: string]: T } {
    return (object: { [index: string]: T }) => __copyObject(object, itemCopyFn);
}

function __copyObject<T>(object: { [index: string]: T }, itemCopyFn: (item: T) => T): { [index: string]: T } {
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
