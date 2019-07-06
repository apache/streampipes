import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {AnyStaticProperty} from "./AnyStaticProperty";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";

@RdfsClass('sp:RuntimeResolvableAnyStaticProperty')
export class RuntimeResolvableAnyStaticProperty extends AnyStaticProperty {

    @RdfProperty('sp:dependsOnStaticProperty')
    public dependsOn: string[] = [];

    constructor(id: string) {
        super(id);
    }
}