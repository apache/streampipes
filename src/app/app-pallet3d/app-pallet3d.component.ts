import {Component, EventEmitter, Output, ViewChild} from "@angular/core";

import PackWidget from './services/pack-widget.service';
import {AppPallet3dRestService} from "./services/pallet3d-rest.service";

@Component({
    selector: 'app-pallet3d',
    templateUrl: './app-pallet3d.component.html',
    styleUrls: ['./app-pallet3d.component.css']
})
export class AppPallet3dComponent {

    constructor(private appPallet3dRestService: AppPallet3dRestService) {}

    selectedIndex: number = 0;
    size: any;
    @Output() appOpened = new EventEmitter<boolean>();

    @ViewChild('palletContainer') input;

    ngOnInit() {
        this.appOpened.emit(true);

        setInterval(() => {
            this.appPallet3dRestService.getPalletInfo().subscribe(res => {
                if(res["item"] !== undefined) {
                    this.size = res
                }
            });

            if(this.size !== undefined) {
                var scene = new PackWidget.Scene({ color: 0xC19A6B });
                var renderer = new PackWidget.Renderer(scene, this.input.nativeElement, { width: 1200, height: 800, controls: { autoRotate: true}} );

                var palette = this.size;

                renderer.addBox(palette);

                renderer.fitToScreen(1, 1, 0.5);
            }
        }, 5000);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

}