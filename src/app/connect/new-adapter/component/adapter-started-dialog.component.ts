import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ShepherdService} from '../../../services/tour/shepherd.service';
import {RestService} from "../../rest.service";
import {StatusMessage} from "../../model/message/StatusMessage";

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './dialog-adapter-started.html',
    styleUrls: ['./adapter-started-dialog.component.css'],
})
export class AdapterStartedDialog {

    private adapterInstalled: boolean = false;
    private adapterStatus: StatusMessage;

    constructor(
        public dialogRef: MatDialogRef<AdapterStartedDialog>,
        private restService: RestService,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private ShepherdService: ShepherdService) { }

    ngOnInit() {
        this.startAdapter();
    }

    startAdapter() {
        this.restService.addAdapter(this.data.adapter).subscribe(x => {
            this.adapterInstalled = true;
            this.adapterStatus = x;
        });
    }

    onCloseConfirm() {
        this.dialogRef.close('Confirm');
        this.ShepherdService.trigger("confirm_adapter_started_button");
    }

}