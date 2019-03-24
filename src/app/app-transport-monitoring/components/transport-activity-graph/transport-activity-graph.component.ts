import {Component, Input} from "@angular/core";
import {ActivityEventModel} from "../../model/activity-event.model";

@Component({
    selector: 'transport-activity-graph',
    templateUrl: './transport-activity-graph.component.html',
    styleUrls: ['./transport-activity-graph.component.css']
})
export class TransportActivityGraphComponent {

    @Input() activityEventModel: ActivityEventModel[];

    constructor() {

    }

    ngOnInit() {
        this.makeDummyData();
    }

    getStyle(activity: ActivityEventModel) {
        return {'position': 'relative', 'left': activity.timestamp / 1000,'width':'1px', 'height':'200px', 'background': this.getBackground(activity) };
    }

    getBackground(activity) {
        if (activity.activity == "normal") {
            return "green";
        } else if (activity.activity == "shake") {
            return "yellow";
        } else {
            return "red";
        }
    }

    makeDummyData() {
        this.activityEventModel = [];
        let currentTimestamp = 0;
        let currentActivity = "normal";
        let frequency = 1000;
        for(let i = 0; i < 300; i++) {
            let nextActivity = this.getRandomActivity(currentActivity);
            this.activityEventModel.push({timestamp: currentTimestamp,  activity: nextActivity});
            currentTimestamp += frequency;
            currentActivity = nextActivity;
        }
    }

    getRandomActivity(currentActivity: string) {
        let random = Math.random();
        if (random < 0.4) {
            return currentActivity;
        } else if (random >=0.4 && random < 0.6) {
            return "normal";
        } else if (random >= 0.6 && random < 0.8) {
            return "shake";
        } else {
            return "fall";
        }
    }

}