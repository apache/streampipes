import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './dialog-adapter-started.html',
})
export class AdapterStartedDialog {

    constructor(
        public dialogRef: MatDialogRef<AdapterStartedDialog>,
        @Inject(MAT_DIALOG_DATA) public data: any) { }

    onCloseConfirm() {
        this.dialogRef.close('Confirm');
        // TODO refresh page
        //window.location.reload();
    }

    // onCloseCancel() {
    //     this.dialogRef.close('Cancel');
    // }

}