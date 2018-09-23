import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ShepherdService} from '../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './dialog-adapter-started.html',
    styleUrls: ['./adapter-started-dialog.component.css'],
})
export class AdapterStartedDialog {

    constructor(
        public dialogRef: MatDialogRef<AdapterStartedDialog>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private ShepherdService: ShepherdService) { }

    onCloseConfirm() {
        this.dialogRef.close('Confirm');
        this.ShepherdService.trigger("confirm_adapter_started_button");
    }

}