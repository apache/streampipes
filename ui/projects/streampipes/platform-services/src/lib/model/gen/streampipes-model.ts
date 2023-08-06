/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* tslint:disable */
/* eslint-disable */
// @ts-nocheck
// Generated using typescript-generator version 3.1.1185 on 2023-08-06 11:37:37.

export class NamedStreamPipesEntity {
    '@class':
        | 'org.apache.streampipes.model.connect.adapter.AdapterDescription'
        | 'org.apache.streampipes.model.connect.grounding.ProtocolDescription'
        | 'org.apache.streampipes.model.template.PipelineTemplateDescription'
        | 'org.apache.streampipes.model.SpDataStream'
        | 'org.apache.streampipes.model.base.InvocableStreamPipesEntity'
        | 'org.apache.streampipes.model.graph.DataProcessorInvocation'
        | 'org.apache.streampipes.model.graph.DataSinkInvocation';
    '_rev': string;
    'appId': string;
    'connectedTo': string[];
    'description': string;
    'dom': string;
    'elementId': string;
    'iconUrl': string;
    'includedAssets': string[];
    'includedLocales': string[];
    'includesAssets': boolean;
    'includesLocales': boolean;
    'internallyManaged': boolean;
    'name': string;
    /**
     * @deprecated
     */
    'uri': string;

    static 'fromData'(
        data: NamedStreamPipesEntity,
        target?: NamedStreamPipesEntity,
    ): NamedStreamPipesEntity {
        if (!data) {
            return data;
        }
        const instance = target || new NamedStreamPipesEntity();
        instance['@class'] = data['@class'];
        instance._rev = data._rev;
        instance.appId = data.appId;
        instance.connectedTo = __getCopyArrayFn(__identity<string>())(
            data.connectedTo,
        );
        instance.description = data.description;
        instance.dom = data.dom;
        instance.elementId = data.elementId;
        instance.iconUrl = data.iconUrl;
        instance.includedAssets = __getCopyArrayFn(__identity<string>())(
            data.includedAssets,
        );
        instance.includedLocales = __getCopyArrayFn(__identity<string>())(
            data.includedLocales,
        );
        instance.includesAssets = data.includesAssets;
        instance.includesLocales = data.includesLocales;
        instance.internallyManaged = data.internallyManaged;
        instance.name = data.name;
        instance.uri = data.uri;
        return instance;
    }
}

export class AdapterDescription extends NamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.connect.adapter.AdapterDescription';
    'category': string[];
    'config': StaticPropertyUnion[];
    'correspondingDataStreamElementId': string;
    /**
     * @deprecated
     */
    'correspondingServiceGroup': string;
    'createdAt': number;
    'dataStream': SpDataStream;
    'eventGrounding': EventGrounding;
    'eventSchema': EventSchema;
    'icon': string;
    'rules': TransformationRuleDescriptionUnion[];
    'running': boolean;
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
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.correspondingDataStreamElementId =
            data.correspondingDataStreamElementId;
        instance.correspondingServiceGroup = data.correspondingServiceGroup;
        instance.createdAt = data.createdAt;
        instance.dataStream = SpDataStream.fromData(data.dataStream);
        instance.eventGrounding = EventGrounding.fromData(data.eventGrounding);
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.icon = data.icon;
        instance.rules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.rules);
        instance.running = data.running;
        instance.schemaRules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.schemaRules);
        instance.selectedEndpointUrl = data.selectedEndpointUrl;
        instance.streamRules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.streamRules);
        instance.valueRules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.valueRules);
        return instance;
    }
}

export class AdapterEventPreview {
    inputData: string;
    rules: TransformationRuleDescriptionUnion[];

    static fromData(
        data: AdapterEventPreview,
        target?: AdapterEventPreview,
    ): AdapterEventPreview {
        if (!data) {
            return data;
        }
        const instance = target || new AdapterEventPreview();
        instance.inputData = data.inputData;
        instance.rules = __getCopyArrayFn(
            TransformationRuleDescription.fromDataUnion,
        )(data.rules);
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
        instance.code = data.code;
        instance.description = data.description;
        instance.label = data.label;
        return instance;
    }
}

export class TransformationRuleDescription {
    '@class':
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
        instance['@class'] = data['@class'];
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

export class StaticProperty {
    '@class':
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
        instance['@class'] = data['@class'];
        instance.description = data.description;
        instance.index = data.index;
        instance.internalName = data.internalName;
        instance.label = data.label;
        instance.predefined = data.predefined;
        instance.staticPropertyType = data.staticPropertyType;
        instance.valueRequired = data.valueRequired;
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
        instance.horizontalRendering = data.horizontalRendering;
        instance.options = __getCopyArrayFn(Option.fromData)(data.options);
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

export class OutputStrategy {
    '@class':
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
        instance['@class'] = data['@class'];
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
        instance.adapters = __getCopyArrayFn(ExportItem.fromData)(
            data.adapters,
        );
        instance.assetId = data.assetId;
        instance.assetName = data.assetName;
        instance.assets = __getCopyArrayFn(ExportItem.fromData)(data.assets);
        instance.dashboards = __getCopyArrayFn(ExportItem.fromData)(
            data.dashboards,
        );
        instance.dataLakeMeasures = __getCopyArrayFn(ExportItem.fromData)(
            data.dataLakeMeasures,
        );
        instance.dataSources = __getCopyArrayFn(ExportItem.fromData)(
            data.dataSources,
        );
        instance.dataViews = __getCopyArrayFn(ExportItem.fromData)(
            data.dataViews,
        );
        instance.files = __getCopyArrayFn(ExportItem.fromData)(data.files);
        instance.overrideBrokerSettings = data.overrideBrokerSettings;
        instance.pipelines = __getCopyArrayFn(ExportItem.fromData)(
            data.pipelines,
        );
        return instance;
    }
}

export class BoundPipelineElement {
    connectedTo: BoundPipelineElement[];
    pipelineElementTemplate: InvocableStreamPipesEntity;

    static fromData(
        data: BoundPipelineElement,
        target?: BoundPipelineElement,
    ): BoundPipelineElement {
        if (!data) {
            return data;
        }
        const instance = target || new BoundPipelineElement();
        instance.connectedTo = __getCopyArrayFn(BoundPipelineElement.fromData)(
            data.connectedTo,
        );
        instance.pipelineElementTemplate = InvocableStreamPipesEntity.fromData(
            data.pipelineElementTemplate,
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.internalName = data.internalName;
        instance.name = data.name;
        instance.superLabel = data.superLabel;
        instance.superLabelId = data.superLabelId;
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
        instance.originalDatatypeXsd = data.originalDatatypeXsd;
        instance.runtimeKey = data.runtimeKey;
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
        instance.codeTemplate = data.codeTemplate;
        instance.language = data.language;
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
        instance.memberType = data.memberType;
        instance.members = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.members,
        );
        instance.staticPropertyTemplate = StaticProperty.fromDataUnion(
            data.staticPropertyTemplate,
        );
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

export class ConfigItem {
    configurationScope: ConfigurationScope;
    description: string;
    key: string;
    password: boolean;
    value: string;
    valueType: string;

    static fromData(data: ConfigItem, target?: ConfigItem): ConfigItem {
        if (!data) {
            return data;
        }
        const instance = target || new ConfigItem();
        instance.configurationScope = data.configurationScope;
        instance.description = data.description;
        instance.key = data.key;
        instance.password = data.password;
        instance.value = data.value;
        instance.valueType = data.valueType;
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
        instance.groupId = data.groupId;
        instance.topicName = data.topicName;
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
        instance.consumedMessagesSincePipelineStart =
            data.consumedMessagesSincePipelineStart;
        instance.lag = data.lag;
        instance.totalMessagesSincePipelineStart =
            data.totalMessagesSincePipelineStart;
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
        instance.correctionValue = data.correctionValue;
        instance.operator = data.operator;
        instance.runtimeKey = data.runtimeKey;
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
        instance.availablePropertyKeys = __getCopyArrayFn(__identity<string>())(
            data.availablePropertyKeys,
        );
        instance.outputRight = data.outputRight;
        instance.selectedPropertyKeys = __getCopyArrayFn(__identity<string>())(
            data.selectedPropertyKeys,
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
        instance.cols = data.cols;
        instance.component = data.component;
        instance.id = data.id;
        instance.name = data.name;
        instance.rows = data.rows;
        instance.settings = __getCopyArrayFn(__identity<string>())(
            data.settings,
        );
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.dashboardGeneralSettings = __getCopyObjectFn(
            __identity<any>(),
        )(data.dashboardGeneralSettings);
        instance.dashboardTimeSettings = __getCopyObjectFn(__identity<any>())(
            data.dashboardTimeSettings,
        );
        instance.description = data.description;
        instance.displayHeader = data.displayHeader;
        instance.id = data.id;
        instance.name = data.name;
        instance.widgets = __getCopyArrayFn(DashboardItem.fromData)(
            data.widgets,
        );
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
        instance.dashboardWidgetSettings = DashboardWidgetSettings.fromData(
            data.dashboardWidgetSettings,
        );
        instance.pipelineId = data.pipelineId;
        instance.visualizationName = data.visualizationName;
        instance.widgetId = data.widgetId;
        instance.widgetType = data.widgetType;
        return instance;
    }
}

export class DashboardWidgetSettings {
    config: StaticPropertyUnion[];
    requiredSchema: EventSchema;
    widgetDescription: string;
    widgetIconName: string;
    widgetLabel: string;
    widgetName: string;

    static fromData(
        data: DashboardWidgetSettings,
        target?: DashboardWidgetSettings,
    ): DashboardWidgetSettings {
        if (!data) {
            return data;
        }
        const instance = target || new DashboardWidgetSettings();
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.requiredSchema = EventSchema.fromData(data.requiredSchema);
        instance.widgetDescription = data.widgetDescription;
        instance.widgetIconName = data.widgetIconName;
        instance.widgetLabel = data.widgetLabel;
        instance.widgetName = data.widgetName;
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
        instance.baseAppearanceConfig = __getCopyObjectFn(__identity<any>())(
            data.baseAppearanceConfig,
        );
        instance.dataConfig = __getCopyObjectFn(__identity<any>())(
            data.dataConfig,
        );
        instance.measureName = data.measureName;
        instance.pipelineId = data.pipelineId;
        instance.visualizationConfig = __getCopyObjectFn(__identity<any>())(
            data.visualizationConfig,
        );
        instance.widgetId = data.widgetId;
        instance.widgetType = data.widgetType;
        return instance;
    }
}

export class DataLakeMeasure {
    '@class': 'org.apache.streampipes.model.datalake.DataLakeMeasure';
    '_rev': string;
    'elementId': string;
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
        instance['@class'] = data['@class'];
        instance._rev = data._rev;
        instance.elementId = data.elementId;
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.measureName = data.measureName;
        instance.pipelineId = data.pipelineId;
        instance.pipelineIsRunning = data.pipelineIsRunning;
        instance.pipelineName = data.pipelineName;
        instance.schemaVersion = data.schemaVersion;
        instance.timestampField = data.timestampField;
        return instance;
    }
}

export class InvocableStreamPipesEntity
    extends NamedStreamPipesEntity
    implements EndpointSelectable
{
    '@class':
        | 'org.apache.streampipes.model.base.InvocableStreamPipesEntity'
        | 'org.apache.streampipes.model.graph.DataProcessorInvocation'
        | 'org.apache.streampipes.model.graph.DataSinkInvocation';
    'belongsTo': string;
    'configured': boolean;
    'correspondingPipeline': string;
    'correspondingUser': string;
    'detachPath': string;
    'inputStreams': SpDataStream[];
    'selectedEndpointUrl': string;
    'staticProperties': StaticPropertyUnion[];
    'statusInfoSettings': ElementStatusInfoSettings;
    'streamRequirements': SpDataStream[];
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
        instance.belongsTo = data.belongsTo;
        instance.configured = data.configured;
        instance.correspondingPipeline = data.correspondingPipeline;
        instance.correspondingUser = data.correspondingUser;
        instance.detachPath = data.detachPath;
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromData)(
            data.inputStreams,
        );
        instance.selectedEndpointUrl = data.selectedEndpointUrl;
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        instance.statusInfoSettings = ElementStatusInfoSettings.fromData(
            data.statusInfoSettings,
        );
        instance.streamRequirements = __getCopyArrayFn(SpDataStream.fromData)(
            data.streamRequirements,
        );
        instance.supportedGrounding = EventGrounding.fromData(
            data.supportedGrounding,
        );
        instance.uncompleted = data.uncompleted;
        return instance;
    }
}

export class DataProcessorInvocation extends InvocableStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.graph.DataProcessorInvocation';
    'category': string[];
    'outputStrategies': OutputStrategyUnion[];
    'outputStream': SpDataStream;
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
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        instance.outputStrategies = __getCopyArrayFn(
            OutputStrategy.fromDataUnion,
        )(data.outputStrategies);
        instance.outputStream = SpDataStream.fromData(data.outputStream);
        instance.pathName = data.pathName;
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
        instance.code = data.code;
        instance.description = data.description;
        instance.label = data.label;
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
        instance.headers = __getCopyArrayFn(__identity<string>())(data.headers);
        instance.rows = __getCopyArrayFn(__getCopyArrayFn(__identity<any>()))(
            data.rows,
        );
        instance.tags = __getCopyObjectFn(__identity<string>())(data.tags);
        instance.total = data.total;
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
        instance.code = data.code;
        instance.description = data.description;
        instance.label = data.label;
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
        instance.notifications = __getCopyArrayFn(Notification.fromData)(
            data.notifications,
        );
        instance.validationStatusType = data.validationStatusType;
        return instance;
    }
}

export class ElementComposition {
    description: string;
    name: string;
    sepas: DataProcessorInvocation[];
    streams: SpDataStream[];

    static fromData(
        data: ElementComposition,
        target?: ElementComposition,
    ): ElementComposition {
        if (!data) {
            return data;
        }
        const instance = target || new ElementComposition();
        instance.description = data.description;
        instance.name = data.name;
        instance.sepas = __getCopyArrayFn(DataProcessorInvocation.fromData)(
            data.sepas,
        );
        instance.streams = __getCopyArrayFn(SpDataStream.fromData)(
            data.streams,
        );
        return instance;
    }
}

export class ElementStatusInfoSettings {
    elementIdentifier: string;
    errorTopic: string;
    kafkaHost: string;
    kafkaPort: number;
    statsTopic: string;

    static fromData(
        data: ElementStatusInfoSettings,
        target?: ElementStatusInfoSettings,
    ): ElementStatusInfoSettings {
        if (!data) {
            return data;
        }
        const instance = target || new ElementStatusInfoSettings();
        instance.elementIdentifier = data.elementIdentifier;
        instance.errorTopic = data.errorTopic;
        instance.kafkaHost = data.kafkaHost;
        instance.kafkaPort = data.kafkaPort;
        instance.statsTopic = data.statsTopic;
        return instance;
    }
}

export interface EndpointSelectable {
    correspondingPipeline: string;
    detachPath: string;
    name: string;
    selectedEndpointUrl: string;
}

export class ValueSpecification {
    '@class':
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
        instance['@class'] = data['@class'];
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
        instance.description = data.description;
        instance.label = data.label;
        instance.runtimeValues = __getCopyArrayFn(__identity<string>())(
            data.runtimeValues,
        );
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
        instance.elementName = data.elementName;
        instance.notifications = __getCopyArrayFn(Notification.fromData)(
            data.notifications,
        );
        instance.success = data.success;
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

export class EventGrounding {
    transportFormats: TransportFormat[];
    transportProtocols: TransportProtocolUnion[];

    static fromData(
        data: EventGrounding,
        target?: EventGrounding,
    ): EventGrounding {
        if (!data) {
            return data;
        }
        const instance = target || new EventGrounding();
        instance.transportFormats = __getCopyArrayFn(TransportFormat.fromData)(
            data.transportFormats,
        );
        instance.transportProtocols = __getCopyArrayFn(
            TransportProtocol.fromDataUnion,
        )(data.transportProtocols);
        return instance;
    }
}

export class EventProperty {
    '@class':
        | 'org.apache.streampipes.model.schema.EventPropertyList'
        | 'org.apache.streampipes.model.schema.EventPropertyNested'
        | 'org.apache.streampipes.model.schema.EventPropertyPrimitive';
    'description': string;
    'domainProperties': string[];
    'elementId': string;
    'index': number;
    'label': string;
    'propertyScope': string;
    'required': boolean;
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
        instance['@class'] = data['@class'];
        instance.description = data.description;
        instance.domainProperties = __getCopyArrayFn(__identity<string>())(
            data.domainProperties,
        );
        instance.elementId = data.elementId;
        instance.index = data.index;
        instance.label = data.label;
        instance.propertyScope = data.propertyScope;
        instance.required = data.required;
        instance.runtimeId = data.runtimeId;
        instance.runtimeName = data.runtimeName;
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
        instance.measurementUnit = data.measurementUnit;
        instance.runtimeType = data.runtimeType;
        instance.valueSpecification = ValueSpecification.fromDataUnion(
            data.valueSpecification,
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

export class EventSchema {
    eventProperties: EventPropertyUnion[];

    static fromData(data: EventSchema, target?: EventSchema): EventSchema {
        if (!data) {
            return data;
        }
        const instance = target || new EventSchema();
        instance.eventProperties = __getCopyArrayFn(
            EventProperty.fromDataUnion,
        )(data.eventProperties);
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
        instance.label = data.label;
        instance.resourceId = data.resourceId;
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
        instance.additionalInfo = data.additionalInfo;
        instance.changesRequired = data.changesRequired;
        instance.fieldStatus = data.fieldStatus;
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
        instance.createdAt = data.createdAt;
        instance.createdByUser = data.createdByUser;
        instance.fileId = data.fileId;
        instance.filetype = data.filetype;
        instance.internalFilename = data.internalFilename;
        instance.lastModified = data.lastModified;
        instance.originalFilename = data.originalFilename;
        instance.rev = data.rev;
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
        instance.htmlAllowed = data.htmlAllowed;
        instance.htmlFontFormat = data.htmlFontFormat;
        instance.mapsTo = data.mapsTo;
        instance.multiLine = data.multiLine;
        instance.placeholdersSupported = data.placeholdersSupported;
        instance.requiredDatatype = data.requiredDatatype;
        instance.requiredDomainProperty = data.requiredDomainProperty;
        instance.value = data.value;
        instance.valueSpecification = PropertyValueSpecification.fromData(
            data.valueSpecification,
        );
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
        instance.consumedStreams = __getCopyArrayFn(__identity<string>())(
            data.consumedStreams,
        );
        instance.functionId = FunctionId.fromData(data.functionId);
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

export class GuessSchema {
    '@class': 'org.apache.streampipes.model.connect.guess.GuessSchema';
    'eventPreview': string[];
    'eventSchema': EventSchema;
    'fieldStatusInfo': { [index: string]: FieldStatusInfo };

    static 'fromData'(data: GuessSchema, target?: GuessSchema): GuessSchema {
        if (!data) {
            return data;
        }
        const instance = target || new GuessSchema();
        instance['@class'] = data['@class'];
        instance.eventPreview = __getCopyArrayFn(__identity<string>())(
            data.eventPreview,
        );
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.fieldStatusInfo = __getCopyObjectFn(FieldStatusInfo.fromData)(
            data.fieldStatusInfo,
        );
        return instance;
    }
}

export class TransportProtocol {
    '@class':
        | 'org.apache.streampipes.model.grounding.JmsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.KafkaTransportProtocol'
        | 'org.apache.streampipes.model.grounding.MqttTransportProtocol'
        | 'org.apache.streampipes.model.grounding.NatsTransportProtocol'
        | 'org.apache.streampipes.model.grounding.PulsarTransportProtocol';
    'brokerHostname': string;
    'elementId': string;
    'topicDefinition': TopicDefinitionUnion;

    static 'fromData'(
        data: TransportProtocol,
        target?: TransportProtocol,
    ): TransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new TransportProtocol();
        instance['@class'] = data['@class'];
        instance.brokerHostname = data.brokerHostname;
        instance.elementId = data.elementId;
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
            case 'org.apache.streampipes.model.grounding.PulsarTransportProtocol':
                return PulsarTransportProtocol.fromData(data);
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
        instance.acks = data.acks;
        instance.batchSize = data.batchSize;
        instance.groupId = data.groupId;
        instance.kafkaPort = data.kafkaPort;
        instance.lingerMs = data.lingerMs;
        instance.maxRequestSize = data.maxRequestSize;
        instance.messageMaxBytes = data.messageMaxBytes;
        instance.offset = data.offset;
        instance.zookeeperHost = data.zookeeperHost;
        instance.zookeeperPort = data.zookeeperPort;
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.categoryId = data.categoryId;
        instance.color = data.color;
        instance.internalName = data.internalName;
        instance.name = data.name;
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
        instance.mapsFromOptions = __getCopyArrayFn(__identity<string>())(
            data.mapsFromOptions,
        );
        instance.propertyScope = data.propertyScope;
        instance.requirementSelector = data.requirementSelector;
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
        instance.counter = data.counter;
        instance.lastTimestamp = data.lastTimestamp;
        return instance;
    }
}

export class MessagingSettings {
    acks: number;
    batchSize: number;
    jmsHost: string;
    jmsPort: number;
    kafkaHost: string;
    kafkaPort: number;
    lingerMs: number;
    messageMaxBytes: number;
    mqttHost: string;
    mqttPort: number;
    natsHost: string;
    natsPort: number;
    prioritizedFormats: SpDataFormat[];
    prioritizedProtocols: SpProtocol[];
    pulsarUrl: string;
    supportedProtocols: string[];
    zookeeperHost: string;
    zookeeperPort: number;

    static fromData(
        data: MessagingSettings,
        target?: MessagingSettings,
    ): MessagingSettings {
        if (!data) {
            return data;
        }
        const instance = target || new MessagingSettings();
        instance.acks = data.acks;
        instance.batchSize = data.batchSize;
        instance.jmsHost = data.jmsHost;
        instance.jmsPort = data.jmsPort;
        instance.kafkaHost = data.kafkaHost;
        instance.kafkaPort = data.kafkaPort;
        instance.lingerMs = data.lingerMs;
        instance.messageMaxBytes = data.messageMaxBytes;
        instance.mqttHost = data.mqttHost;
        instance.mqttPort = data.mqttPort;
        instance.natsHost = data.natsHost;
        instance.natsPort = data.natsPort;
        instance.prioritizedFormats = __getCopyArrayFn(
            __identity<SpDataFormat>(),
        )(data.prioritizedFormats);
        instance.prioritizedProtocols = __getCopyArrayFn(
            __identity<SpProtocol>(),
        )(data.prioritizedProtocols);
        instance.pulsarUrl = data.pulsarUrl;
        instance.supportedProtocols = __getCopyArrayFn(__identity<string>())(
            data.supportedProtocols,
        );
        instance.zookeeperHost = data.zookeeperHost;
        instance.zookeeperPort = data.zookeeperPort;
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
        instance.newRuntimeKey = data.newRuntimeKey;
        instance.oldRuntimeKey = data.oldRuntimeKey;
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
        instance.additionalInformation = data.additionalInformation;
        instance.description = data.description;
        instance.title = data.title;
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

export class Option {
    elementId: string;
    internalName: string;
    name: string;
    selected: boolean;

    static fromData(data: Option, target?: Option): Option {
        if (!data) {
            return data;
        }
        const instance = target || new Option();
        instance.elementId = data.elementId;
        instance.internalName = data.internalName;
        instance.name = data.name;
        instance.selected = data.selected;
        return instance;
    }
}

/**
 * @deprecated since 0.92.0, for removal
 */
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
        instance.measureName = data.measureName;
        instance.pipelineId = data.pipelineId;
        instance.pipelineName = data.pipelineName;
        instance.schema = EventSchema.fromData(data.schema);
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.actions = __getCopyArrayFn(DataSinkInvocation.fromData)(
            data.actions,
        );
        instance.createdAt = data.createdAt;
        instance.createdByUser = data.createdByUser;
        instance.healthStatus = data.healthStatus;
        instance.pipelineCategories = __getCopyArrayFn(__identity<string>())(
            data.pipelineCategories,
        );
        instance.pipelineNotifications = __getCopyArrayFn(__identity<string>())(
            data.pipelineNotifications,
        );
        instance.publicElement = data.publicElement;
        instance.restartOnSystemReboot = data.restartOnSystemReboot;
        instance.running = data.running;
        instance.startedAt = data.startedAt;
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.comments = __getCopyArrayFn(PipelineCanvasComment.fromData)(
            data.comments,
        );
        instance.pipelineElementMetadata = __getCopyObjectFn(
            PipelineElementMetadata.fromData,
        )(data.pipelineElementMetadata);
        instance.pipelineId = data.pipelineId;
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.categoryDescription = data.categoryDescription;
        instance.categoryName = data.categoryName;
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
        instance.status = EdgeValidationStatus.fromData(data.status);
        instance.targetId = data.targetId;
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
        instance.customName = data.customName;
        instance.position = CanvasPosition.fromData(data.position);
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
        instance.consumedMessageInfoExists = data.consumedMessageInfoExists;
        instance.consumedMessagesInfos = __getCopyArrayFn(
            ConsumedMessagesInfo.fromData,
        )(data.consumedMessagesInfos);
        instance.pipelineElementId = data.pipelineElementId;
        instance.pipelineElementName = data.pipelineElementName;
        instance.producedMessageInfoExists = data.producedMessageInfoExists;
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
        instance.count = data.count;
        instance.description = data.description;
        instance.elementId = data.elementId;
        instance.name = data.name;
        instance.weight = data.weight;
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
        instance._id = data._id;
        instance._rev = data._rev;
        instance.basePipelineElementAppId = data.basePipelineElementAppId;
        instance.templateConfigs = __getCopyObjectFn(
            PipelineElementTemplateConfig.fromData,
        )(data.templateConfigs);
        instance.templateDescription = data.templateDescription;
        instance.templateName = data.templateName;
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
        instance.displayed = data.displayed;
        instance.editable = data.editable;
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
    inputStreams: SpDataStream[];
    outputStrategies: OutputStrategyUnion[];
    outputStream: SpDataStream;
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
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromData)(
            data.inputStreams,
        );
        instance.outputStrategies = __getCopyArrayFn(
            OutputStrategy.fromDataUnion,
        )(data.outputStrategies);
        instance.outputStream = SpDataStream.fromData(data.outputStream);
        instance.pipelineElementValid = data.pipelineElementValid;
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
        instance.validationInfos = __getCopyArrayFn(
            PipelineElementValidationInfo.fromData,
        )(data.validationInfos);
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
        instance.edgeValidations = __getCopyArrayFn(
            PipelineEdgeValidation.fromData,
        )(data.edgeValidations);
        instance.pipelineModifications = __getCopyArrayFn(
            PipelineModification.fromData,
        )(data.pipelineModifications);
        instance.pipelineValid = data.pipelineValid;
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
        instance.createdAt = data.createdAt;
        instance.pipelineElementMonitoringInfo = __getCopyArrayFn(
            PipelineElementMonitoringInfo.fromData,
        )(data.pipelineElementMonitoringInfo);
        instance.pipelineId = data.pipelineId;
        instance.startedAt = data.startedAt;
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
        instance.elementStatus = __getCopyArrayFn(
            PipelineElementStatus.fromData,
        )(data.elementStatus);
        instance.pipelineId = data.pipelineId;
        instance.pipelineName = data.pipelineName;
        instance.success = data.success;
        instance.title = data.title;
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
        instance.message = data.message;
        instance.messageType = data.messageType;
        instance.pipelineId = data.pipelineId;
        instance.timestamp = data.timestamp;
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
        instance.pipelineTemplateDescription = data.pipelineTemplateDescription;
        instance.pipelineTemplateId = data.pipelineTemplateId;
        instance.pipelineTemplateName = data.pipelineTemplateName;
        return instance;
    }
}

export class PipelineTemplateInvocation {
    '@class': 'org.apache.streampipes.model.template.PipelineTemplateInvocation';
    'dataStreamId': string;
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
        instance['@class'] = data['@class'];
        instance.dataStreamId = data.dataStreamId;
        instance.kviName = data.kviName;
        instance.pipelineTemplateDescription =
            PipelineTemplateDescription.fromData(
                data.pipelineTemplateDescription,
            );
        instance.pipelineTemplateId = data.pipelineTemplateId;
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
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

export class PropertyRenameRule {
    newRuntimeName: string;
    runtimeId: string;

    static fromData(
        data: PropertyRenameRule,
        target?: PropertyRenameRule,
    ): PropertyRenameRule {
        if (!data) {
            return data;
        }
        const instance = target || new PropertyRenameRule();
        instance.newRuntimeName = data.newRuntimeName;
        instance.runtimeId = data.runtimeId;
        return instance;
    }
}

export class PropertyValueSpecification {
    maxValue: number;
    minValue: number;
    step: number;

    static fromData(
        data: PropertyValueSpecification,
        target?: PropertyValueSpecification,
    ): PropertyValueSpecification {
        if (!data) {
            return data;
        }
        const instance = target || new PropertyValueSpecification();
        instance.maxValue = data.maxValue;
        instance.minValue = data.minValue;
        instance.step = data.step;
        return instance;
    }
}

/**
 * @deprecated since 0.93.0, for removal
 */
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
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        instance.config = __getCopyArrayFn(StaticProperty.fromDataUnion)(
            data.config,
        );
        instance.sourceType = data.sourceType;
        return instance;
    }
}

export class PulsarTransportProtocol extends TransportProtocol {
    '@class': 'org.apache.streampipes.model.grounding.PulsarTransportProtocol';

    static 'fromData'(
        data: PulsarTransportProtocol,
        target?: PulsarTransportProtocol,
    ): PulsarTransportProtocol {
        if (!data) {
            return data;
        }
        const instance = target || new PulsarTransportProtocol();
        super.fromData(data, instance);
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
        instance.maxValue = data.maxValue;
        instance.minValue = data.minValue;
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
        instance.newRuntimeKey = data.newRuntimeKey;
        instance.oldRuntimeKey = data.oldRuntimeKey;
        return instance;
    }
}

export class RuntimeOptionsRequest {
    '@class':
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsRequest'
        | 'org.apache.streampipes.model.runtime.RuntimeOptionsResponse';
    'appId': string;
    'belongsTo': string;
    'inputStreams': SpDataStream[];
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
        instance['@class'] = data['@class'];
        instance.appId = data.appId;
        instance.belongsTo = data.belongsTo;
        instance.inputStreams = __getCopyArrayFn(SpDataStream.fromData)(
            data.inputStreams,
        );
        instance.requestId = data.requestId;
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
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
    'latestFetchedNodes': TreeInputNode[];
    'multiSelection': boolean;
    'nextBaseNodeToResolve': string;
    'nodes': TreeInputNode[];
    'resolveDynamically': boolean;
    'selectedNodesInternalNames': string[];

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
        instance.latestFetchedNodes = __getCopyArrayFn(TreeInputNode.fromData)(
            data.latestFetchedNodes,
        );
        instance.multiSelection = data.multiSelection;
        instance.nextBaseNodeToResolve = data.nextBaseNodeToResolve;
        instance.nodes = __getCopyArrayFn(TreeInputNode.fromData)(data.nodes);
        instance.resolveDynamically = data.resolveDynamically;
        instance.selectedNodesInternalNames = __getCopyArrayFn(
            __identity<string>(),
        )(data.selectedNodesInternalNames);
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
        instance.encrypted = data.encrypted;
        instance.value = data.value;
        return instance;
    }
}

export class ShortUserInfo {
    displayName: string;
    email: string;
    principalId: string;

    static fromData(
        data: ShortUserInfo,
        target?: ShortUserInfo,
    ): ShortUserInfo {
        if (!data) {
            return data;
        }
        const instance = target || new ShortUserInfo();
        instance.displayName = data.displayName;
        instance.email = data.email;
        instance.principalId = data.principalId;
        return instance;
    }
}

export class TopicDefinition {
    '@class':
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
        instance['@class'] = data['@class'];
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
        instance.defaultValue = data.defaultValue;
        instance.selected = data.selected;
        return instance;
    }
}

export class SpDataStream extends NamedStreamPipesEntity {
    '@class': 'org.apache.streampipes.model.SpDataStream';
    'category': string[];
    'correspondingAdapterId': string;
    'eventGrounding': EventGrounding;
    'eventSchema': EventSchema;
    'index': number;

    static 'fromData'(data: SpDataStream, target?: SpDataStream): SpDataStream {
        if (!data) {
            return data;
        }
        const instance = target || new SpDataStream();
        super.fromData(data, instance);
        instance.category = __getCopyArrayFn(__identity<string>())(
            data.category,
        );
        instance.correspondingAdapterId = data.correspondingAdapterId;
        instance.eventGrounding = EventGrounding.fromData(data.eventGrounding);
        instance.eventSchema = EventSchema.fromData(data.eventSchema);
        instance.index = data.index;
        return instance;
    }
}

export class SpDataStreamContainer {
    '@class': 'org.apache.streampipes.model.SpDataStreamContainer';
    'list': SpDataStream[];

    static 'fromData'(
        data: SpDataStreamContainer,
        target?: SpDataStreamContainer,
    ): SpDataStreamContainer {
        if (!data) {
            return data;
        }
        const instance = target || new SpDataStreamContainer();
        instance['@class'] = data['@class'];
        instance.list = __getCopyArrayFn(SpDataStream.fromData)(data.list);
        return instance;
    }
}

export class SpLogEntry {
    errorMessage: SpLogMessage;
    timestamp: number;

    static fromData(data: SpLogEntry, target?: SpLogEntry): SpLogEntry {
        if (!data) {
            return data;
        }
        const instance = target || new SpLogEntry();
        instance.errorMessage = SpLogMessage.fromData(data.errorMessage);
        instance.timestamp = data.timestamp;
        return instance;
    }
}

export class SpLogMessage {
    cause: string;
    detail: string;
    fullStackTrace: string;
    level: SpLogLevel;
    title: string;

    static fromData(data: SpLogMessage, target?: SpLogMessage): SpLogMessage {
        if (!data) {
            return data;
        }
        const instance = target || new SpLogMessage();
        instance.cause = data.cause;
        instance.detail = data.detail;
        instance.fullStackTrace = data.fullStackTrace;
        instance.level = data.level;
        instance.title = data.title;
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
        instance.allDataSeries = __getCopyArrayFn(DataSeries.fromData)(
            data.allDataSeries,
        );
        instance.forId = data.forId;
        instance.headers = __getCopyArrayFn(__identity<string>())(data.headers);
        instance.sourceIndex = data.sourceIndex;
        instance.spQueryStatus = data.spQueryStatus;
        instance.total = data.total;
        return instance;
    }
}

export class SpServiceConfiguration {
    configs: ConfigItem[];
    rev: string;
    serviceGroup: string;
    serviceName: string;

    static fromData(
        data: SpServiceConfiguration,
        target?: SpServiceConfiguration,
    ): SpServiceConfiguration {
        if (!data) {
            return data;
        }
        const instance = target || new SpServiceConfiguration();
        instance.configs = __getCopyArrayFn(ConfigItem.fromData)(data.configs);
        instance.rev = data.rev;
        instance.serviceGroup = data.serviceGroup;
        instance.serviceName = data.serviceName;
        return instance;
    }
}

export class SpServiceRegistration {
    firstTimeSeenUnhealthy: number;
    healthCheckPath: string;
    healthy: boolean;
    host: string;
    port: number;
    rev: string;
    scheme: string;
    svcGroup: string;
    svcId: string;
    tags: SpServiceTag[];

    static fromData(
        data: SpServiceRegistration,
        target?: SpServiceRegistration,
    ): SpServiceRegistration {
        if (!data) {
            return data;
        }
        const instance = target || new SpServiceRegistration();
        instance.firstTimeSeenUnhealthy = data.firstTimeSeenUnhealthy;
        instance.healthCheckPath = data.healthCheckPath;
        instance.healthy = data.healthy;
        instance.host = data.host;
        instance.port = data.port;
        instance.rev = data.rev;
        instance.scheme = data.scheme;
        instance.svcGroup = data.svcGroup;
        instance.svcId = data.svcId;
        instance.tags = __getCopyArrayFn(SpServiceTag.fromData)(data.tags);
        return instance;
    }
}

export class SpServiceTag {
    prefix: SpServiceTagPrefix;
    value: string;

    static fromData(data: SpServiceTag, target?: SpServiceTag): SpServiceTag {
        if (!data) {
            return data;
        }
        const instance = target || new SpServiceTag();
        instance.prefix = data.prefix;
        instance.value = data.value;
        return instance;
    }
}

export class StaticPropertyAlternative extends StaticProperty {
    '@class': 'org.apache.streampipes.model.staticproperty.StaticPropertyAlternative';
    'elementId': string;
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
        instance.elementId = data.elementId;
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
        instance.horizontalRendering = data.horizontalRendering;
        instance.showLabel = data.showLabel;
        instance.staticProperties = __getCopyArrayFn(
            StaticProperty.fromDataUnion,
        )(data.staticProperties);
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
        instance.adapters = __getCopyArrayFn(__identity<string>())(
            data.adapters,
        );
        instance.assets = __getCopyArrayFn(__identity<string>())(data.assets);
        instance.dashboardWidgets = __getCopyArrayFn(__identity<string>())(
            data.dashboardWidgets,
        );
        instance.dashboards = __getCopyArrayFn(__identity<string>())(
            data.dashboards,
        );
        instance.dataLakeMeasures = __getCopyArrayFn(__identity<string>())(
            data.dataLakeMeasures,
        );
        instance.dataSources = __getCopyArrayFn(__identity<string>())(
            data.dataSources,
        );
        instance.dataViewWidgets = __getCopyArrayFn(__identity<string>())(
            data.dataViewWidgets,
        );
        instance.dataViews = __getCopyArrayFn(__identity<string>())(
            data.dataViews,
        );
        instance.files = __getCopyArrayFn(__identity<string>())(data.files);
        instance.pipelines = __getCopyArrayFn(__identity<string>())(
            data.pipelines,
        );
        instance.requiredAdapterAppIds = __getCopyArrayFn(__identity<string>())(
            data.requiredAdapterAppIds,
        );
        instance.requiredDataSinkAppIds = __getCopyArrayFn(
            __identity<string>(),
        )(data.requiredDataSinkAppIds);
        instance.requiredProcessorAppIds = __getCopyArrayFn(
            __identity<string>(),
        )(data.requiredProcessorAppIds);
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

export class SupportedProperty {
    propertyId: string;
    value: string;
    valueRequired: boolean;

    static fromData(
        data: SupportedProperty,
        target?: SupportedProperty,
    ): SupportedProperty {
        if (!data) {
            return data;
        }
        const instance = target || new SupportedProperty();
        instance.propertyId = data.propertyId;
        instance.value = data.value;
        instance.valueRequired = data.valueRequired;
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
        instance.formatString = data.formatString;
        instance.mode = data.mode;
        instance.multiplier = data.multiplier;
        instance.runtimeKey = data.runtimeKey;
        return instance;
    }
}

export class TransformOperation {
    mappingPropertyInternalName: string;
    sourceStaticProperty: string;
    targetValue: string;
    transformationScope: string;

    static fromData(
        data: TransformOperation,
        target?: TransformOperation,
    ): TransformOperation {
        if (!data) {
            return data;
        }
        const instance = target || new TransformOperation();
        instance.mappingPropertyInternalName = data.mappingPropertyInternalName;
        instance.sourceStaticProperty = data.sourceStaticProperty;
        instance.targetValue = data.targetValue;
        instance.transformationScope = data.transformationScope;
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

export class TransportFormat {
    rdfType: string[];

    static fromData(
        data: TransportFormat,
        target?: TransportFormat,
    ): TransportFormat {
        if (!data) {
            return data;
        }
        const instance = target || new TransportFormat();
        instance.rdfType = __getCopyArrayFn(__identity<string>())(data.rdfType);
        return instance;
    }
}

export class TreeInputNode {
    children: TreeInputNode[];
    dataNode: boolean;
    internalNodeName: string;
    nodeMetadata: { [index: string]: any };
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
        instance.children = __getCopyArrayFn(TreeInputNode.fromData)(
            data.children,
        );
        instance.dataNode = data.dataNode;
        instance.internalNodeName = data.internalNodeName;
        instance.nodeMetadata = __getCopyObjectFn(__identity<any>())(
            data.nodeMetadata,
        );
        instance.nodeName = data.nodeName;
        instance.selected = data.selected;
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
        instance.fromUnitRessourceURL = data.fromUnitRessourceURL;
        instance.runtimeKey = data.runtimeKey;
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
        instance.darkMode = data.darkMode;
        instance.displayName = data.displayName;
        instance.roles = __getCopyArrayFn(__identity<string>())(data.roles);
        instance.showTutorial = data.showTutorial;
        instance.username = data.username;
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
        instance.pipelineName = data.pipelineName;
        instance.schema = EventSchema.fromData(data.schema);
        instance.topic = data.topic;
        instance.visualizationName = data.visualizationName;
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
        instance.wildcardTopicMappings = __getCopyArrayFn(
            WildcardTopicMapping.fromData,
        )(data.wildcardTopicMappings);
        instance.wildcardTopicName = data.wildcardTopicName;
        return instance;
    }
}

export class WildcardTopicMapping {
    mappedRuntimeName: string;
    mappingId: string;
    selectedMapping: string;
    topicParameterType: string;

    static fromData(
        data: WildcardTopicMapping,
        target?: WildcardTopicMapping,
    ): WildcardTopicMapping {
        if (!data) {
            return data;
        }
        const instance = target || new WildcardTopicMapping();
        instance.mappedRuntimeName = data.mappedRuntimeName;
        instance.mappingId = data.mappingId;
        instance.selectedMapping = data.selectedMapping;
        instance.topicParameterType = data.topicParameterType;
        return instance;
    }
}

export type ConfigurationScope =
    | 'CONTAINER_STARTUP_CONFIG'
    | 'CONTAINER_GLOBAL_CONFIG'
    | 'PIPELINE_ELEMENT_CONFIG';

export type EdgeValidationStatusType = 'COMPLETE' | 'INCOMPLETE' | 'INVALID';

export type EventPropertyUnion =
    | EventPropertyList
    | EventPropertyNested
    | EventPropertyPrimitive;

export type FieldStatus = 'GOOD' | 'BAD' | 'ATTENTION';

export type MappingPropertyUnion = MappingPropertyNary | MappingPropertyUnary;

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

export type SpDataFormat = 'CBOR' | 'JSON' | 'FST' | 'SMILE';

export type SpLogLevel = 'INFO' | 'WARN' | 'ERROR';

export type SpProtocol = 'KAFKA' | 'JMS' | 'MQTT' | 'NATS' | 'PULSAR';

export type SpQueryStatus = 'OK' | 'TOO_MUCH_DATA';

export type SpServiceTagPrefix =
    | 'SYSTEM'
    | 'SP_GROUP'
    | 'ADAPTER'
    | 'DATA_STREAM'
    | 'DATA_PROCESSOR'
    | 'DATA_SINK'
    | 'DATA_SET';

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
    | NatsTransportProtocol
    | PulsarTransportProtocol;

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
