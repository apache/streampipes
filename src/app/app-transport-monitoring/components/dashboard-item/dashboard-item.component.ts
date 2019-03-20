import {Component, Input} from "@angular/core";


@Component({
    selector: 'dashboard-item',
    templateUrl: './dashboard-item.component.html',
    styleUrls: ['./dashboard-item.component.css']
})
export class DashboardItemComponent {

    @Input() panelTitle: string;

    constructor() {

    }

    ngOnInit() {

    }


}