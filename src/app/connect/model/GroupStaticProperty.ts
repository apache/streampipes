import {RdfsClass} from '../../platform-services/tsonld/RdfsClass';
import {StaticProperty} from './StaticProperty';
import {RdfProperty} from '../../platform-services/tsonld/RdfsProperty';

@RdfsClass('sp:StaticPropertyGroup')
export class GroupStaticProperty extends StaticProperty {

    @RdfProperty('sp:hasStaticProperty')
    private staticProperties: StaticProperty[];

    @RdfProperty('sp:showLabel')
    private showLabel: boolean;

    constructor(id: string) {
        super();
        this.id = id;
    }

}