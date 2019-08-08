import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AlternativesStaticProperty} from '../../model/AlternativesStaticProperty';
import {EventSchema} from '../../schema-editor/model/EventSchema';

@Component({
    selector: 'app-static-alternatives',
    templateUrl: './static-alternatives.component.html',
    styleUrls: ['./static-alternatives.component.css']
})
export class StaticAlternativesComponent implements OnInit {


    @Input()
    staticProperty: AlternativesStaticProperty;

    @Input()
    adapterId: string;

    @Input()
    eventSchema: EventSchema;

    @Output() inputEmitter: EventEmitter<Boolean> = new EventEmitter<Boolean>();

    private hasInput: Boolean;
    private errorMessage = "Please select a option";

    ngOnInit(): void {
        this.staticProperty.alternatives.forEach( alternative => alternative.selected = false);
        // TODO: Remove hack
        if (this.staticProperty.alternatives.length === 1) {
            this.hasInput = true;
            this.inputEmitter.emit(this.hasInput);
        }
    }

    valueChange(inputValue) {
        // TODO: Remove hack
        if (this.staticProperty.alternatives.length === 1) {
            this.hasInput = true;
        } else {

            this.hasInput = false;
            let alternative = this.staticProperty.alternatives.find(alternative => alternative.selected == true);
            if (alternative !== undefined) {
                if (alternative.staticProperty !== undefined) {
                    this.hasInput = alternative.staticProperty.isValid;
                } else {
                    this.hasInput = true;
                }
            }
        }
        this.inputEmitter.emit(this.hasInput);
    }

    radioSelectionChange(event) {
        this.staticProperty.alternatives.forEach(alternative => {
            if (alternative.id == event.value.id) {
                alternative.selected = true;
            } else {
                alternative.selected = false;
            }
        });
        this.valueChange(event);
    }

}