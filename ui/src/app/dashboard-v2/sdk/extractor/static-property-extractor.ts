import {EventSchema} from "../../../connect/schema-editor/model/EventSchema";
import {StaticProperty} from "../../../connect/model/StaticProperty";
import {MappingPropertyUnary} from "../../../connect/model/MappingPropertyUnary";
import {FreeTextStaticProperty} from "../../../connect/model/FreeTextStaticProperty";

export class StaticPropertyExtractor {

    constructor(private inputSchema: EventSchema,
                private staticProperties: Array<StaticProperty>) {

    }

    mappingPropertyValue(internalId: string): string {
        let sp: MappingPropertyUnary = this.getStaticPropertyByName(internalId) as MappingPropertyUnary;
        return this.removePrefix(sp.selectedProperty);
    }

    singleValueParameter(internalId: string): any {
        let sp: FreeTextStaticProperty = this.getStaticPropertyByName(internalId) as FreeTextStaticProperty;
        return sp.value;
    }

    getStaticPropertyByName(internalId: string): StaticProperty {
        return this.staticProperties.find(sp => sp.internalName == internalId);
    }


    removePrefix(propertyValue: string) {
        return propertyValue.split("::")[1];
    }

}