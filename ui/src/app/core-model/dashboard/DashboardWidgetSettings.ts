import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";
import {StaticProperty} from "../../connect/model/StaticProperty";
import {EventSchema} from "../../connect/schema-editor/model/EventSchema";
import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {UnnamedStreamPipesEntity} from "../../connect/model/UnnamedStreamPipesEntity";

@RdfsClass('sp:DashboardWidgetSettings')
export class DashboardWidgetSettings extends UnnamedStreamPipesEntity {

    @RdfProperty('sp:hasDashboardWidgetLabel')
    widgetLabel: string;

    @RdfProperty('sp:hasDashboardWidgetName')
    widgetName:string;

    @RdfProperty('sp:hasStaticProperty')
    config: Array<StaticProperty>;

    @RdfProperty('sp:hasSchema')
    requiredSchema: EventSchema;

    constructor() {
        super();
    }
}