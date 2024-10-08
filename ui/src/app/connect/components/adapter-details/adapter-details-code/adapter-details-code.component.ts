import { Component, OnInit } from '@angular/core';
import { SpAbstractAdapterDetailsDirective } from '../abstract-adapter-details.directive';

@Component({
    selector: 'sp-adapter-details-code',
    templateUrl: './adapter-details-code.component.html',
})
export class AdapterDetailsCodeComponent
    extends SpAbstractAdapterDetailsDirective
    implements OnInit
{
    ngOnInit() {
        super.onInit();
    }

    onAdapterLoaded(): void {}
}
