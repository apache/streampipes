import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';

@RdfsClass('sp:StaticPropertyGroup')
export class GroupStaticProperty extends StaticProperty {

    @RdfProperty('sp:hasStaticProperty')
    public staticProperties: StaticProperty[];

    @RdfProperty('sp:showLabel')
    public showLabel: boolean;

    constructor(id: string) {
        super();
        this.id = id;
    }

}