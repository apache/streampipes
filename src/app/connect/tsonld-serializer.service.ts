import { Injectable } from '@angular/core';
import { AnyStaticProperty } from './model/AnyStaticProperty';
import { OneOfStaticProperty } from './model/OneOfStaticProperty';
import { ProtocolDescription } from './model/connect/grounding/ProtocolDescription';
import { ProtocolDescriptionList } from './model/connect/grounding/ProtocolDescriptionList';
import { FreeTextStaticProperty } from './model/FreeTextStaticProperty';
import { FileStaticProperty } from './model/FileStaticProperty';
import { FormatDescriptionList } from './model/connect/grounding/FormatDescriptionList';
import { FormatDescription } from './model/connect/grounding/FormatDescription';
import { AdapterDescriptionList } from './model/connect/AdapterDescriptionList';
import { Option } from './model/Option';
import { AdapterDescription } from './model/connect/AdapterDescription';
import { AdapterSetDescription } from './model/connect/AdapterSetDescription';
import { AdapterStreamDescription } from './model/connect/AdapterStreamDescription';
import { DataSetDescription } from './model/DataSetDescription';
import { EventSchema } from './schema-editor/model/EventSchema';
import { EventProperty } from './schema-editor/model/EventProperty';
import { EventPropertyNested } from './schema-editor/model/EventPropertyNested';
import { EventPropertyList } from './schema-editor/model/EventPropertyList';
import { EventPropertyPrimitive } from './schema-editor/model/EventPropertyPrimitive';
import { DomainPropertyProbability } from './schema-editor/model/DomainPropertyProbability';
import { DomainPropertyProbabilityList } from './schema-editor/model/DomainPropertyProbabilityList';
import { GuessSchema } from './schema-editor/model/GuessSchema';
import { URI } from './model/URI';
import { RenameRuleDescription } from './model/connect/rules/RenameRuleDescription';
import { DeleteRuleDescription } from './model/connect/rules/DeleteRuleDescription';
import { AddNestedRuleDescription } from './model/connect/rules/AddNestedRuleDescription';
import { RemoveDuplicatesRuleDescription } from './model/connect/rules/RemoveDuplicatesRuleDescription';
import { AddTimestampRuleDescription } from './model/connect/rules/AddTimestampRuleDescription';
import { AddValueTransformationRuleDescription } from './model/connect/rules/AddValueTransformationRuleDescription';
import { MoveRuleDescription } from './model/connect/rules/MoveRuleDesctiption';
import { UnitTransformRuleDescription } from './model/connect/rules/UnitTransformRuleDescription';
import {TsonLd} from './tsonld';
import {Observable} from 'rxjs/Observable';
import {TransformationRuleDescription} from './model/connect/rules/TransformationRuleDescription';
import {GenericAdapterSetDescription} from './model/connect/GenericAdapterSetDescription';
import {SpecificAdapterSetDescription} from './model/connect/SpecificAdapterSetDescription';
import {GenericAdapterStreamDescription} from './model/connect/GenericAdapterStreamDescription';
import {SpecificAdapterStreamDescription} from './model/connect/SpecificAdapterStreamDescription';
import {MessageLd} from './model/message/MessageLd';
import {NotificationLd} from './model/message/NotificationLd';
import {SuccessMessageLd} from './model/message/SuccessMessage';
import {ErrorMessageLd} from './model/message/ErrorMessage';
import {TimestampTransformationRuleDescription} from './model/connect/rules/TimestampTransformationRuleDescription';

@Injectable()
export class TsonLdSerializerService {

    private getTsonLd(): any {
        const tsonld = new TsonLd();
        tsonld.addClassMapping(ProtocolDescription);
        tsonld.addClassMapping(ProtocolDescriptionList);
        tsonld.addClassMapping(FreeTextStaticProperty);
        tsonld.addClassMapping(FileStaticProperty);
        tsonld.addClassMapping(FormatDescriptionList);
        tsonld.addClassMapping(FormatDescription);
        tsonld.addClassMapping(AdapterDescriptionList);
        tsonld.addClassMapping(AdapterDescription);
        tsonld.addClassMapping(AdapterSetDescription);
        tsonld.addClassMapping(SpecificAdapterSetDescription);
        tsonld.addClassMapping(GenericAdapterSetDescription);
        tsonld.addClassMapping(AdapterStreamDescription);
        tsonld.addClassMapping(GenericAdapterStreamDescription);
        tsonld.addClassMapping(SpecificAdapterStreamDescription);
        tsonld.addClassMapping(DataSetDescription);
        tsonld.addClassMapping(EventSchema);
        tsonld.addClassMapping(EventProperty);
        tsonld.addClassMapping(EventPropertyNested);
        tsonld.addClassMapping(EventPropertyList);
        tsonld.addClassMapping(EventPropertyPrimitive);
        tsonld.addClassMapping(DomainPropertyProbability);
        tsonld.addClassMapping(DomainPropertyProbabilityList);
        tsonld.addClassMapping(GuessSchema);
        tsonld.addClassMapping(URI);
        tsonld.addClassMapping(RenameRuleDescription);
        tsonld.addClassMapping(DeleteRuleDescription);
        tsonld.addClassMapping(AddNestedRuleDescription);
        tsonld.addClassMapping(TransformationRuleDescription);
        tsonld.addClassMapping(RemoveDuplicatesRuleDescription);
        tsonld.addClassMapping(AddTimestampRuleDescription);
        tsonld.addClassMapping(AddValueTransformationRuleDescription);
        tsonld.addClassMapping(MoveRuleDescription);
        tsonld.addClassMapping(UnitTransformRuleDescription);
        tsonld.addClassMapping(Option);
        tsonld.addClassMapping(AnyStaticProperty);
        tsonld.addClassMapping(OneOfStaticProperty);
        tsonld.addClassMapping(TimestampTransformationRuleDescription);
        tsonld.addClassMapping(NotificationLd);
        tsonld.addClassMapping(MessageLd);
        tsonld.addClassMapping(SuccessMessageLd);
        tsonld.addClassMapping(ErrorMessageLd);

        tsonld.addContext('sp', 'https://streampipes.org/vocabulary/v1/');
        tsonld.addContext('spi', 'urn:streampipes.org:spi:');
        tsonld.addContext('foaf', 'http://xmlns.com/foaf/0.1/');


        return tsonld;
    }

    public toJsonLd(o: any): Observable<{}> {
        return this.getTsonLd().toflattenJsonLd(o);
    }

    public fromJsonLd(o: any, type: string): any {
        return this.getTsonLd().fromJsonLdType(o, type);
    }

}