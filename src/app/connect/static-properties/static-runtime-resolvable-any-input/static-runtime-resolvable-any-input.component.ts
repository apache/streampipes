import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RuntimeResolvableAnyStaticProperty} from "../../model/RuntimeResolvableAnyStaticProperty";

@Component({
    selector: 'app-static-runtime-resolvable-any-input',
    templateUrl: './static-runtime-resolvable-any-input.component.html',
    styleUrls: ['./static-runtime-resolvable-any-input.component.css']
})
export class StaticRuntimeResolvableAnyInputComponent implements OnInit {

    @Input()
    staticProperty: RuntimeResolvableAnyStaticProperty;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    constructor() { }

    ngOnInit() {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
    }

    select(id) {
        for (let option of this.staticProperty.options) {
            option.selected = false;
        }
        this.staticProperty.options.find(option => option.id === id).selected = true;
        this.inputEmitter.emit(true)
    }

}
