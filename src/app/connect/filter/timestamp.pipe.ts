import { Pipe, PipeTransform } from '@angular/core';
import {EventProperty} from '../schema-editor/model/EventProperty';

@Pipe({
    name: 'timestampFilter',
    pure: false
})
export class TimestampPipe implements PipeTransform {
    transform(items: EventProperty[], filter: Object): any {
        return items.filter(item => {
            if ("http://schema.org/DateTime" == item.domainProperty) {
                return true;
            } else {
                return false;
            }
        });
    }
}