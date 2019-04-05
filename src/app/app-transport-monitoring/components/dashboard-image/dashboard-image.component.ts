import {Component, EventEmitter, Input, Output, SecurityContext} from '@angular/core';
import {ParcelInfoEventModel} from "../../model/parcel-info-event.model";
import {DomSanitizer, SafeStyle, SafeValue} from "@angular/platform-browser";

@Component({
    selector: 'dashboard-image',
    templateUrl: './dashboard-image.component.html',
    styleUrls: ['./dashboard-image.component.scss']
})
export class DashboardImageComponent {

    //@Input() parcelInfoEventModel: ParcelInfoEventModel[];
    imageData: any[];

    currentIndex: number = 0;

    sanitizer: DomSanitizer;

    constructor(sanitizer: DomSanitizer) {
        this.sanitizer = sanitizer;
        this.imageData = [];
    }

    ngOnInit() {

    }

    @Input()
    set parcelInfoEventModel(parcelInfoEventModel: ParcelInfoEventModel[]) {
        this.imageData = [];
        let index = this.getIndex(parcelInfoEventModel)
        //parcelInfoEventModel.forEach(parcelInfo => {
            this.imageData.push('data:image/jpeg;base64,' + parcelInfoEventModel[index].segmentationImage);
        //});
    }

    getIndex(parcelInfoEventModel: ParcelInfoEventModel[]) {
        return parcelInfoEventModel.length > 1 ? 1 : 0;
    }

    getSanitizedImageUrl(imageUrl) {
        this.sanitizer.sanitize(SecurityContext.STYLE, `url(${imageUrl})`);
    }

    sanitize(image: string): any {
        return this.sanitizer.bypassSecurityTrustStyle(`url(${image})`);
    }
}