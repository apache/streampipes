import {RdfsClass} from "../../platform-services/tsonld/RdfsClass";
import {StaticProperty} from "./StaticProperty";
import {RdfProperty} from "../../platform-services/tsonld/RdfsProperty";

@RdfsClass('sp:ColorPickerStaticProperty')
export class ColorPickerStaticProperty extends StaticProperty {

    @RdfProperty('sp:hasSelectedColor')
    public selectedColor: string;

    constructor() {
        super();
    }

}