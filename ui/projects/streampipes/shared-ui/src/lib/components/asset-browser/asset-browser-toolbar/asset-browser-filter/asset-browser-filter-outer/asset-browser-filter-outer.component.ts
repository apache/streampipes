import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'sp-asset-browser-filter-outer',
    templateUrl: 'asset-browser-filter-outer.component.html',
    styleUrls: ['../asset-browser-filter.component.scss'],
})
export class AssetBrowserFilterOuterComponent {
    @Input()
    selectedItems: any[];

    @Input()
    allItems: any[];

    @Input()
    title: string = '';

    @Output()
    selectAllEmitter = new EventEmitter();

    @Output()
    deselectAllEmitter = new EventEmitter();
}
