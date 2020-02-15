import {Input, OnInit} from "@angular/core";
import {DashboardItem} from "../../../models/dashboard.model";
import {DashboardWidget} from "../../../../core-model/dashboard/DashboardWidget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {Message} from "@stomp/stompjs";
import {Subscription} from "rxjs";
import {GridsterItem, GridsterItemComponent} from "angular-gridster2";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";

export abstract class BaseStreamPipesWidget {

    @Input() widget: DashboardItem;
    @Input() widgetConfig: DashboardWidget;
    @Input() gridsterItem: GridsterItem;
    @Input() gridsterItemComponent: GridsterItemComponent;
    @Input() editMode: boolean;

    static readonly PADDING: number = 20;
    static readonly EDIT_HEADER_HEIGHT: number = 40;

    subscription: Subscription;

    hasSelectableColorSettings: boolean;
    hasTitlePanelSettings: boolean;

    selectedBackgroundColor: string;
    selectedPrimaryTextColor: string;
    selectedSecondaryTextColor: string;
    selectedTitle: string;

    defaultBackgroundColor: string = "#1B1464";
    defaultPrimaryTextColor: string = "#FFFFFF";
    defaultSecondaryTextColor: string = "#39B54A";

    protected constructor(private rxStompService: RxStompService) {
    }

    ngOnInit(): void {
        let extractor: StaticPropertyExtractor = new StaticPropertyExtractor(this.widgetConfig.dashboardWidgetDataConfig.schema, this.widgetConfig.dashboardWidgetSettings.config);
        if (extractor.hasStaticProperty(WidgetConfigBuilder.BACKGROUND_COLOR_KEY)) {
            this.hasSelectableColorSettings = true;
            this.selectedBackgroundColor = extractor.selectedColor(WidgetConfigBuilder.BACKGROUND_COLOR_KEY);
            this.selectedPrimaryTextColor = extractor.selectedColor(WidgetConfigBuilder.PRIMARY_TEXT_COLOR_KEY);
            this.selectedSecondaryTextColor = extractor.selectedColor(WidgetConfigBuilder.SECONDARY_TEXT_COLOR_KEY);
        } else {
            this.selectedBackgroundColor = this.defaultBackgroundColor;
            this.selectedPrimaryTextColor = this.defaultPrimaryTextColor;
            this.selectedSecondaryTextColor = this.defaultSecondaryTextColor;
        }
        if (extractor.hasStaticProperty(WidgetConfigBuilder.TITLE_KEY)) {
            this.hasTitlePanelSettings = true;
            this.selectedTitle = extractor.stringParameter(WidgetConfigBuilder.TITLE_KEY);
        }

        this.extractConfig(extractor);
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