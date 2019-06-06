import {Component, ViewChild} from "@angular/core";
import {ConfigurationService} from "../shared/configuration.service";
import {MessagingSettings} from "../shared/messaging-settings.model";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';

@Component({
    selector: 'messaging-configuration',
    templateUrl: './messaging-configuration.component.html',
    styleUrls: ['./messaging-configuration.component.css']
})
export class MessagingConfigurationComponent {

    private messagingSettings: MessagingSettings;
    private loadingCompleted: boolean = false;

    constructor(private configurationService: ConfigurationService) {

    }

    ngOnInit() {
        this.getMessagingSettings();
    }

    getMessagingSettings() {
        this.configurationService.getMessagingSettings().subscribe(response => {
            this.messagingSettings = response;
            this.loadingCompleted = true;
        });
    }

    updateMessagingSettings() {
        this.configurationService.updateMessagingSettings(this.messagingSettings).subscribe(response => this.getMessagingSettings());
    }

    drop(event: CdkDragDrop<string[]>) {
        moveItemInArray(this.messagingSettings.prioritizedFormats, event.previousIndex, event.currentIndex);
    }
}