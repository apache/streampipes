import {StaticPropertyUtilService} from "../static-property-util.service";
import {PropertySelectorService} from "../../../services/property-selector.service";


export abstract class StaticMappingComponent {

    protected firstStreamPropertySelector: string = "s0::";

    constructor(private staticPropertyUtil: StaticPropertyUtilService,
                private PropertySelectorService: PropertySelectorService){

    }

    getName(eventProperty) {
        return eventProperty.label
            ? eventProperty.label
            : eventProperty.runTimeName;
    }

}