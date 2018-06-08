import { Component } from '@angular/core';
import {MatTabChangeEvent} from "@angular/material";

@Component({
    templateUrl: './info.component.html',
    styleUrls: ['./info.component.css']
})
export class InfoComponent {

    currentTabIndex: number = 0;

    constructor() {

    }

    tabChanged(tabChangeEvent: MatTabChangeEvent): void {
        this.currentTabIndex = tabChangeEvent.index;
    }



}