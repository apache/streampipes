import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FreeTextStaticProperty } from '../../model/FreeTextStaticProperty';
import { StaticProperty } from '../../model/StaticProperty';
import { StaticPropertyUtilService } from '../static-property-util.service';
import {StaticFileRestService} from './static-file-rest.service';
import {FileStaticProperty} from '../../model/FileStaticProperty';


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

    constructor(private staticPropertyUtil: StaticPropertyUtilService, private staticFileRestService: StaticFileRestService){

    }

    ngOnInit() {
    }

    handleFileInput(files: any) {
        this.selectedUploadFile = files[0];
        this.fileName = this.selectedUploadFile.name;
    }

    upload() {
        if (this.selectedUploadFile !== undefined) {
            this.staticFileRestService.upload(this.selectedUploadFile).subscribe(
                result => {
                    (<FileStaticProperty> (this.staticProperty)).locationPath = result.notifications[0].title;
                    this.valueChange(true);
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