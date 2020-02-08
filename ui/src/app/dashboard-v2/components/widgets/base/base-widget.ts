import {EventSchema} from "../../../../connect/schema-editor/model/EventSchema";
import {StaticProperty} from "../../../../connect/model/StaticProperty";
import {Input, OnInit} from "@angular/core";
import {DashboardItem} from "../../../models/dashboard.model";
import {DashboardWidget} from "../../../../core-model/dashboard/DashboardWidget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {Message} from "@stomp/stompjs";

export abstract class BaseStreamPipesWidget {

    @Input() widget: DashboardItem;
    @Input() widgetConfig: DashboardWidget;

    protected constructor(private rxStompService: RxStompService) {
    }

    ngOnInit(): void {
        this.extractConfig(new StaticPropertyExtractor(this.widgetConfig.dashboardWidgetSettings.requiredSchema, this.widgetConfig.dashboardWidgetSettings.config));
        this.rxStompService.watch("/topic/" +this.widgetConfig.dashboardWidgetDataConfig.topic).subscribe((message: Message) => {
            this.onEvent(JSON.parse(message.body));
        });
    }

    protected abstract extractConfig(extractor: StaticPropertyExtractor);

    protected abstract onEvent(event: any);
}