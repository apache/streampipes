import {Component, EventEmitter, Output} from '@angular/core';

@Component({
    selector: 'app-transport-monitoring',
    templateUrl: './app-transport-monitoring.component.html',
    styleUrls: ['./app-transport-monitoring.component.css']
})
export class AppTransportMonitoringComponent {


    selectedIndex: number = 0;
    @Output() appOpened = new EventEmitter<boolean>();

    incomingExpanded: boolean = true;
    transportExpanded: boolean = true;
    outgoingExpanded: boolean = true;

    constructor() {

    }

    ngOnInit() {
        this.appOpened.emit(true);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }


}