import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
    selector: 'kvi-created-dialog',
    templateUrl: 'kvi-created.dialog.html',
})
export class KviCreatedDialog {

    constructor(
        public dialogRef: MatDialogRef<KviCreatedDialog>,
        @Inject(MAT_DIALOG_DATA) public data: any) { }

    onNoClick(): void {
        this.dialogRef.close();
    }

    onCloseConfirm() {
        this.dialogRef.close('Confirm');
    }

}