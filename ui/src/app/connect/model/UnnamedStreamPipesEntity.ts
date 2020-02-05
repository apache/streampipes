import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {RdfId} from "../../platform-services/tsonld/RdfId";

@RdfsClass('sp:UnnamedStreamPipesEntity')
export class UnnamedStreamPipesEntity {

    private prefix = "urn:streampipes.org:spi:";
    private chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @RdfId
    public id: string;

    constructor() {
        this.id = this.prefix + this.randomString(6);
    }

    randomString(length) {
        let result = '';
        for (let i = length; i > 0; --i) result += this.chars[Math.floor(Math.random() * this.chars.length)];
        return result;
    }

}