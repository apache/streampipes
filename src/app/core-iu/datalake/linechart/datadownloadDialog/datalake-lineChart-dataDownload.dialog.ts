import {Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef, MatStepper} from '@angular/material';
import {HttpEventType} from '@angular/common/http';
import {DatalakeRestService} from '../../../../core-services/datalake/datalake-rest.service';

@Component({
    selector: 'sp-datalake-lineChart-dataDownload-dialog',
    templateUrl: 'datalake-lineChart-dataDownload.dialog.html',
    styleUrls: ['./datalake-lineChart-dataDownload.dialog.css']
})
export class DatalakeLineChartDataDownloadDialog {


    downloadFormat: string = 'csv';
    selectedData: string = 'visible';
    downloadFinish: boolean = false;
    downloadedMBs: number = undefined;

    @ViewChild('stepper') stepper: MatStepper;

    downloadHttpRequestSubscribtion;


    customStartDate = new Date();
    customEndDate = new Date(this.customStartDate.getTime() + 60000 * 60 * 24);


    constructor(public dialogRef: MatDialogRef<DatalakeLineChartDataDownloadDialog>,
                @Inject(MAT_DIALOG_DATA) public data, private restService: DatalakeRestService,) {

    }

    createFile(data, format) {
        var a = document.createElement("a");
        document.body.appendChild(a);
        a.style.display = "display: none";

        var url = window.URL.createObjectURL(new Blob([String(data)], { type: 'data:text/' + format + ';charset=utf-8' }));
        a.href = url;
        a.download = 'spDatalake.' + this.downloadFormat;
        a.click();
        window.URL.revokeObjectURL(url)
    }


    downloadData() {
        this.nextStep();
        switch (this.selectedData) {
            case "visible":
                if (this.data.yAxesKeys === undefined) {
                    this.createFile('', this.downloadFormat)
                } else {

                    if (this.downloadFormat === "json") {
                        let visibleData = [];
                        this.data.data.forEach(elem => {
                            let tmp = {"time": elem[this.data.xAxesKey]}
                            this.data.yAxesKeys.forEach(key => {
                                if (elem[key] !== undefined) {
                                    tmp[key] = elem[key]
                                }
                            });
                            visibleData.push(tmp)
                        });
                        this.createFile(JSON.stringify(visibleData), 'json')
                    } else {
                        //CSV
                        let resultCsv: string = '';

                        //header
                        resultCsv += this.data.xAxesKey;
                        this.data.yAxesKeys.forEach(key => {
                            resultCsv += ';';
                            resultCsv += key;
                        });


                        //content
                        this.data.data.forEach(elem => {
                            resultCsv += '\n';
                            resultCsv += elem[this.data.xAxesKey];
                            this.data.yAxesKeys.forEach(key => {
                                resultCsv += ';';
                                if (elem[key] !== undefined) {
                                    resultCsv += elem[key]
                                }
                            })
                        });
                        this.createFile(resultCsv, 'csv')
                    }
                }
                this.downloadFinish = true;
                break;
            case "all":
                this.performRequest(this.restService.downloadRowData(this.data.index, this.downloadFormat));
                break;
            case "customInterval":
                this.performRequest(this.restService.downloadRowDataTimeInterval(this.data.index, this.downloadFormat,
                    this.customStartDate.getTime(), this.customEndDate.getTime()));

        }
    }

    performRequest(request) {
        this.downloadHttpRequestSubscribtion = request.subscribe(event => {
            // progress
            if (event.type === HttpEventType.DownloadProgress) {
                this.downloadedMBs = event.loaded / 1024 / 1014
            }

            // finished
            if (event.type === HttpEventType.Response) {
                this.createFile(event.body, this.downloadFormat)
                this.downloadFinish = true
            }
        });
    }

    cancelDownload() {
        this.downloadHttpRequestSubscribtion.unsubscribe();
        this.exitDialog();
    }

    exitDialog(): void {
        this.dialogRef.close();
    }

    nextStep() {
        this.stepper.next();
    }

    previousStep() {
        this.stepper.previous();
    }

}