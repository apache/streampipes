import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'dashboard-image',
    templateUrl: './dashboard-image.component.html',
    styleUrls: ['./dashboard-image.component.css']
})
export class DashboardImageComponent {

    @Input() imageBase64: string;
    imageData: string;

    constructor() {

    }

    ngOnInit() {
        this.imageData = 'data:image/jpeg;base64,' + this.imageBase64;
    }


}