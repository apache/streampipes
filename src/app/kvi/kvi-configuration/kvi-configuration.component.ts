import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StaticProperty } from '../../connect/model/StaticProperty';
import { DataSetDescription } from '../../connect/model/DataSetDescription';
import { FreeTextStaticProperty } from '../../connect/model/FreeTextStaticProperty';
import { MappingPropertyUnary } from '../../connect/model/MappingPropertyUnary';
import {URI} from '../../connect/model/URI';

@Component({
    selector: 'kvi-configuration',
    templateUrl: './kvi-configuration.component.html',
    styleUrls: ['./kvi-configuration.component.css']
})
export class KviConfigurationComponent {

    @Input() configurations: StaticProperty[] = [];
    @Input() dataSet: DataSetDescription;
    @Output() configuredOperators: EventEmitter<StaticProperty[]> = new EventEmitter<StaticProperty[]>();

    constructor() {
    }

    selectConfiguration(configuration: any) {
        if (configuration !== undefined) {
            for (let config of this.configurations) {
                if (config['id'] == configuration['a']['id']) {
                    // config['mapsTo'] = {"@id": configuration['b']['id']};
                    const tmpURI = new URI(configuration['b']['id']);
                    config['mapsTo'] = tmpURI;
                }
            }
        }
        let allValuesSet = true;
        for (let config of this.configurations) {
            if (config instanceof FreeTextStaticProperty) {
                if (config['value'] == undefined || config['value'] == '') {
                    allValuesSet = false;
                }
            }
            if (config instanceof MappingPropertyUnary) {
                if (config['mapsTo'] == undefined) {
                    allValuesSet = false;
                }
            }
        }
        if (allValuesSet) {
            this.configuredOperators.emit(this.configurations);
        } else {
            this.configuredOperators.emit(undefined);
        }
    }

}