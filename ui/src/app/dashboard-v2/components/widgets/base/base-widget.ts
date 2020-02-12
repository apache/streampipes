import {Input, OnInit} from "@angular/core";
import {DashboardItem} from "../../../models/dashboard.model";
import {DashboardWidget} from "../../../../core-model/dashboard/DashboardWidget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {Message} from "@stomp/stompjs";
import {Subscription} from "rxjs";
import {GridsterItem} from "angular-gridster2";

export abstract class BaseStreamPipesWidget {

    @Input() widget: DashboardItem;
    @Input() widgetConfig: DashboardWidget;
    @Input() gridsterItem: GridsterItem;

    subscription: Subscription;

    protected constructor(private rxStompService: RxStompService) {
    }

    ngOnInit(): void {
        this.extractConfig(new StaticPropertyExtractor(this.widgetConfig.dashboardWidgetDataConfig.schema, this.widgetConfig.dashboardWidgetSettings.config));
        this.subscription = this.rxStompService.watch("/topic/" +this.widgetConfig.dashboardWidgetDataConfig.topic).subscribe((message: Message) => {
            this.onEvent(JSON.parse(message.body));
        });
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    protected abstract extractConfig(extractor: StaticPropertyExtractor);

    protected abstract onEvent(event: any);
}