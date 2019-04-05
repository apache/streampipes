import {Component, EventEmitter, Output} from "@angular/core";

@Component({
    selector: 'app-pallet3d',
    templateUrl: './app-pallet3d.component.html',
    styleUrls: ['./app-pallet3d.component.css']
})
export class AppPallet3dComponent {

    selectedIndex: number = 0;
    @Output() appOpened = new EventEmitter<boolean>();

    ngOnInit() {
        this.appOpened.emit(true);
    }

    selectedIndexChange(index: number) {
        this.selectedIndex = index;
    }

}