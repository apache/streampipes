import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {ShepherdService} from '../../../services/tour/shepherd.service';
import {RestService} from "../../rest.service";
import {TsonLdSerializerService} from '../../tsonld-serializer.service';

@Component({
    selector: 'sp-dialog-adapter-started-dialog',
    templateUrl: './adapter-export-dialog.html',
    styleUrls: ['./adapter-export-dialog.component.css'],
})
export class AdapterExportDialog {

    private adapterJsonLd;

    constructor(
        public dialogRef: MatDialogRef<AdapterExportDialog>,
        private restService: RestService,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private tsonLdSerializerService: TsonLdSerializerService,
        private ShepherdService: ShepherdService) {

    }

    ngOnInit() {
        delete this.data.adapter['userName'];
        this.tsonLdSerializerService.toJsonLd(this.data.adapter).subscribe(res => {
            this.adapterJsonLd = res;
        });

    }

    download() {
        this.tsonLdSerializerService.toJsonLd(this.data.adapter).subscribe(res => {
            var data = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(res, null, 2));
            var downloader = document.createElement('a');

            downloader.setAttribute('href', data);
            downloader.setAttribute('download', this.data.adapter.label + '-adapter-template.json');
            downloader.click();

        });


    }


    onCloseConfirm() {
        this.dialogRef.close('Confirm');
    }

}