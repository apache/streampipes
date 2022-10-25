import { Component, Input } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import { Pipeline } from '@streampipes/platform-services';

@Component({
    selector: 'sp-can-not-edit-adapter-dialog',
    templateUrl: './can-not-edit-adapter-dialog.component.html',
    styleUrls: ['./can-not-edit-adapter-dialog.component.scss'],
})
export class CanNotEditAdapterDialog {
    @Input()
    pipelines: Pipeline[];

    constructor(public dialogRef: DialogRef<CanNotEditAdapterDialog>) {}

    close() {
        this.dialogRef.close();
    }
}
