import {Component, Input} from '@angular/core';
import {DatalakeRestService} from '../../../core-services/datalake/datalake-rest.service';
import {HttpEventType} from '@angular/common/http';

@Component({
    selector: 'sp-datalake-data-download',
    templateUrl: './datalake-dataDownload.component.html',
    styleUrls: ['./datalake-dataDownload.component.css']
})
export class DatalakeDataDownloadcomponent {

    @Input() index: string;

    downloadFormat: string = 'csv';
    isDownloading: boolean = false;

    constructor(private restService: DatalakeRestService) {

    }

    downloadData() {
        this.isDownloading = true;
        this.restService.getFile(this.index, this.downloadFormat).subscribe(event => {
            // progress
            if (event.type === HttpEventType.DownloadProgress) {
                console.log(event.loaded);
            }

            // finished
            if (event.type === HttpEventType.Response) {
                console.log(event);

                this.isDownloading = false;

                var element = document.createElement('a');
                element.setAttribute('href', 'data:text/' + this.downloadFormat + ';charset=utf-8,' + encodeURIComponent(String(event.body)));

                element.style.display = 'none';
                document.body.appendChild(element);

                element.click();

                document.body.removeChild(element);

            }
        })
    }

}