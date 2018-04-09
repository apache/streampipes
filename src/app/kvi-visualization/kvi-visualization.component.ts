import { Component } from '@angular/core';
import { KviVisualizationService } from './shared/kvi-visualization.service';

@Component({
    templateUrl: './kvi-visualization.component.html',
    styleUrls: ['./kvi-visualization.component.css']
})
export class KviVisualizationComponent {

    kviData;

    constructor(private kviVisualizationService: KviVisualizationService) {
        this.kviData = this.kviVisualizationService.getKviData();
    }

}