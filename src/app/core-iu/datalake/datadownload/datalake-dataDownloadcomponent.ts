import {Component, Input} from '@angular/core';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';
import {HttpEventType} from '@angular/common/http';
import {el} from '@angular/platform-browser/testing/src/browser_util';
import {MatSnackBar} from '@angular/material';

@Component({
    selector: 'sp-datalake-data-download',
    templateUrl: './datalake-dataDownload.component.html',
    styleUrls: ['./datalake-dataDownload.component.css']
})
export class DatalakeDataDownloadcomponent {

    @Input() index: string;

    downloadFormat: string = 'csv';
    isDownloading: boolean = false;
    downloadedMBs: number = 0;

    constructor(private restService: DatalakeRestService, private snackBar: MatSnackBar) {

    }

    downloadData() {
        this.isDownloading = true;
        this.downloadedMBs = 0;
        this.restService.getFile(this.index, this.downloadFormat).subscribe(event => {
            // progress
            if (event.type === HttpEventType.DownloadProgress) {
                this.downloadedMBs = event.loaded / 1024 / 1014
            }

            // finished
            if (event.type === HttpEventType.Response) {
/*
                this.isDownloading = false;

                var element = document.createElement('a');
             //   element.setAttribute('href', 'data:text/' + this.downloadFormat + ';charset=utf-8,' + encodeURIComponent(String(event.body)));
                element.setAttribute('href', 'data:text/plain' + ';charset=utf-8,' + encodeURIComponent(String(event.body)));

                element.style.display = 'none';
                document.body.appendChild(element);

                element.click();

                document.body.removeChild(element);
*/

                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style.display = "display: none";

                var url = window.URL.createObjectURL(new Blob([String(event.body)], { type: 'data:text/' + this.downloadFormat + ';charset=utf-8' }));
                a.href = url;
                a.download = 'spDatalake.' + this.downloadFormat;
                a.click();
                window.URL.revokeObjectURL(url)
                this.isDownloading = false;

            }

            if (event['status'] === 500) {
                this.isDownloading = false;
                this.openSnackBar("Error while downloading data!")
            }
        })
    }


    openSnackBar(message: string) {
        this.snackBar.open(message, 'Close', {
            duration: 2000,
        });
    }

}