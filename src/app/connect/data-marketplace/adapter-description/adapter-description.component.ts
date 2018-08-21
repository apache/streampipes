import {Component, Input} from '@angular/core';
import {AdapterDescription} from '../../model/connect/AdapterDescription';

@Component({
    selector: 'sp-adapter-description',
    templateUrl: './adapter-description.component.html',
    styleUrls: ['./adapter-description.component.css']
})
export class AdapterDescriptionComponent {

    @Input() adapter: AdapterDescription;

    isDataStreamDescription(): boolean {
        return this.adapter.constructor.name.includes("AdapterStreamDescription");
    }

    isDataSetDescription(): boolean {
        return this.adapter.constructor.name.includes("AdapterSetDescription");
    }

    isGenericDescription(): boolean {
        return this.adapter.id.includes('generic');
    }

    isSpecificDescription(): boolean {
        return this.adapter.id.includes('specific');
    }

}