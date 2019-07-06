import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {RuntimeResolvableOneOfStaticProperty} from "../../model/RuntimeResolvableOneOfStaticProperty";
import {StaticProperty} from "../../model/StaticProperty";
import {RestService} from "../../rest.service";
import {RuntimeOptionsRequest} from "../../model/connect/runtime/RuntimeOptionsRequest";
import {ConfigurationInfo} from "../../model/message/ConfigurationInfo";

@Component({
    selector: 'app-static-runtime-resolvable-oneof-input',
    templateUrl: './static-runtime-resolvable-oneof-input.component.html',
    styleUrls: ['./static-runtime-resolvable-oneof-input.component.css']
})
export class StaticRuntimeResolvableOneOfInputComponent implements OnInit, OnChanges {

    @Input()
    staticProperty: RuntimeResolvableOneOfStaticProperty;

    @Input()
    staticProperties: StaticProperty[];

    @Input()
    adapterId: string;

    @Input()
    completedStaticProperty: ConfigurationInfo;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    showOptions: boolean = false;
    loading: boolean = false;
    dependentStaticProperties: any = new Map();

    constructor(private RestService: RestService) {
    }

    ngOnInit() {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }

        if (this.staticProperty.options.length == 0 && (!this.staticProperty.dependsOn || this.staticProperty.dependsOn.length == 0)) {
            this.loadOptionsFromRestApi();
        } else {
            this.loadSavedProperty();
        }

        if (this.staticProperty.dependsOn && this.staticProperty.dependsOn.length > 0) {
            this.staticProperty.dependsOn.forEach(dp => {
                this.dependentStaticProperties.set(dp, false);
            });
        }
    }

    loadOptionsFromRestApi() {
        var resolvableOptionsParameterRequest = new RuntimeOptionsRequest();
        resolvableOptionsParameterRequest.staticProperties = this.staticProperties;
        resolvableOptionsParameterRequest.requestId = this.staticProperty.internalName;

        this.showOptions = false;
        this.loading = true;
        this.RestService.fetchRemoteOptions(resolvableOptionsParameterRequest, this.adapterId).subscribe(msg => {
            this.staticProperty.options = msg.options;
            if (this.staticProperty.options && this.staticProperty.options.length > 0) {
                this.staticProperty.options[0].selected = true;
            }
            this.loading = false;
            this.loadSavedProperty();
        });
    }

    loadSavedProperty() {
        this.staticProperty.options.forEach(option => {
            if (option.selected) {
                //this.staticProperty.currentSelection = option;
            }
        });
        this.showOptions = true;
    }

    select(id) {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(option => option.id === id).selected = true;
        this.inputEmitter.emit(true)
    }


    ngOnChanges(changes: SimpleChanges): void {
        if (changes['completedStaticProperty']) {
            if (this.completedStaticProperty != undefined) {
                this.dependentStaticProperties.set(this.completedStaticProperty.staticPropertyInternalName, this.completedStaticProperty.configured);
                if (Array.from(this.dependentStaticProperties.values()).every(v => v === true)) {
                    this.loadOptionsFromRestApi();
                }
            }
        }
    }
}
