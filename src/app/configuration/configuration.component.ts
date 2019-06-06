import {Component, ViewChild} from '@angular/core';

import { ConfigurationService } from './shared/configuration.service';
import { StreampipesPeContainer } from "./shared/streampipes-pe-container.model";
import { StreampipesPeContainerConifgs } from "./shared/streampipes-pe-container-configs";
import {MatPaginator, MatTableDataSource} from "@angular/material";
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
    templateUrl: './configuration.component.html',
    styleUrls: ['./configuration.component.css'],
    animations: [
        trigger('detailExpand', [
            state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
            state('expanded', style({height: '*'})),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ]
})
export class ConfigurationComponent {

    selectedIndex: number = 0;

    constructor() {
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

}