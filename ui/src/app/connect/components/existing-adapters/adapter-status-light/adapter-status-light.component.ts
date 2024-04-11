import { Component, Input } from '@angular/core';

@Component({
    selector: 'sp-adapter-status-light',
    templateUrl: './adapter-status-light.component.html',
    styleUrl: '../../../../../scss/sp/status-light.scss',
})
export class AdapterStatusLightComponent {
    @Input()
    adapterRunning: boolean;
}
