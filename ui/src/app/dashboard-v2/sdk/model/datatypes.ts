import {Vocabulary} from "./vocabulary";

export class Datatypes {

    static readonly Integer = new Datatypes(Vocabulary.XSD, "integer");
    static readonly Long = new Datatypes(Vocabulary.XSD, "long");
    static readonly Float = new Datatypes(Vocabulary.XSD, "float");
    static readonly Boolean = new Datatypes(Vocabulary.XSD, "boolean");
    static readonly String = new Datatypes(Vocabulary.XSD, "string");
    static readonly Double = new Datatypes(Vocabulary.XSD, "double");
    static readonly Number = new Datatypes(Vocabulary.SO, "Number");

    private constructor(private readonly vocabulary: string, private readonly entity: string ) {

    }

    toUri() {
        return this.vocabulary + this.entity;
    }


}