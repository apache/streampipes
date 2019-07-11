import { RdfId } from '../../platform-services/tsonld/RdfId';
import { RdfProperty } from '../../platform-services/tsonld/RdfsProperty';
import { RdfsClass } from '../../platform-services/tsonld/RdfsClass';
import { NamedStreamPipesEntity } from "./NamedStreamPipesEntity";
import { URI } from "./URI";
import { StaticProperty } from "./StaticProperty";

@RdfsClass('sp:InvocableStreamPipesEntity')
export class InvocableStreamPipesEntity extends NamedStreamPipesEntity {

    //@RdfProperty('sp:receivesStream')
    //public inputStreams: Array<SpDataStream>;

    @RdfProperty('sp:hasStaticProperty')
    public staticProperties: Array<StaticProperty>;

    @RdfProperty('sp:belongsTo')
    public belongsTo: string;

    @RdfProperty('sp:correspondingPipeline')
    public correspondingPipeline: string;

    constructor(id: string) {
        super(id);
    }

}
