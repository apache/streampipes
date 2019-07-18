import {RdfsClass} from '../../../../platform-services/tsonld/RdfsClass';
import {RdfId} from '../../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../../platform-services/tsonld/RdfsProperty';
import {StaticProperty} from "../../StaticProperty";

@RdfsClass('sp:RuntimeOptionsRequest')
export class RuntimeOptionsRequest  {

    @RdfId
    public id: string;

    @RdfProperty('sp:hasRequestId')
    public requestId: string;

    @RdfProperty('sp:hasStaticProperty')
    public staticProperties: StaticProperty[] = [];

    constructor() {
        this.id = "http://streampipes.org/runtimeOptionsRequest/" + Math.floor(Math.random() * 10000000) + 1;
        //this.runtimeKey = runtimeKey;
    }

}
