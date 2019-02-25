import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { StaticPropertyUtilService } from '../static-property-util.service';
import {StaticFileRestService} from './static-file-rest.service';
import {FileStaticProperty} from '../../model/FileStaticProperty';
import {HttpEventType, HttpResponse} from '@angular/common/http';


@Component({
    selector: 'app-static-file-input',
    templateUrl: './static-file-input.component.html',
    styleUrls: ['./static-file-input.component.css']
})
export class StaticFileInputComponent implements OnInit {


    @Input() staticProperty: StaticProperty;
    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    private inputValue: String;
    private fileName: String;


    private selectedUploadFile: File;

    private hasInput: Boolean;
    private errorMessage = "Please enter a value";

    private uploadStatus = 0;

    constructor(private staticPropertyUtil: StaticPropertyUtilService, private staticFileRestService: StaticFileRestService){

    }

    ngOnInit() {
    }

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;
        this.uploadStatus = 0;
    }

    upload() {
        this.uploadStatus = 0;
        if (this.selectedUploadFile !== undefined) {
            this.staticFileRestService.upload(this.selectedUploadFile).subscribe(
                event => {
                    if (event.type == HttpEventType.UploadProgress) {
                        this.uploadStatus = Math.round(100 * event.loaded / event.total);
                    } else if (event instanceof HttpResponse) {
                        (<FileStaticProperty> (this.staticProperty)).locationPath = event.body.notifications[0].title;
                               this.valueChange(true);
                        console.log('File is completely loaded!');
                    }
                },
                error => {
                    this.valueChange(false);
                },

            );
        }
    }

    valueChange(inputValue) {
        this.inputValue = inputValue;
        if(inputValue == "" || !inputValue) {
            this.hasInput = false;
        }
        else{
            this.hasInput = true;
        }

        this.inputEmitter.emit(this.hasInput);
    }

}