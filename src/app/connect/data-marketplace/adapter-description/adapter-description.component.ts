import {Component, Input} from '@angular/core';
import {AdapterDescription} from '../../model/connect/AdapterDescription';

@Component({
    selector: 'sp-adapter-description',
    templateUrl: './adapter-description.component.html',
    styleUrls: ['./adapter-description.component.css']
})
export class AdapterDescriptionComponent {

    @Input() adapter: AdapterDescription;

}