import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupStaticProperty} from '../../model/GroupStaticProperty';
import {EventSchema} from '../../schema-editor/model/EventSchema';

@Component({
    selector: 'app-static-group',
    templateUrl: './static-group.component.html',
    styleUrls: ['./static-group.component.css']
})
export class StaticGroupComponent {

    @Input()
    staticProperty: GroupStaticProperty;

    @Input()
    adapterId: string;

    @Input()
    eventSchema: EventSchema;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    private hasInput: Boolean;

    valueChange(inputValue) {
        let property = this.staticProperty.staticProperties.find(property => property.isValid == false);
        if (property == undefined) {
            this.hasInput = true;
        } else {
            this.hasInput = false;
        }
        this.inputEmitter.emit(this.hasInput)
    }

}