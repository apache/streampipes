import {Datatypes} from "../model/datatypes";

export class DatatypeMatch {

    match(datatypeRequirement: string, datatypeOffer: string) {
        if (datatypeRequirement == undefined) {
            return true;
        } else {
            return (datatypeRequirement == datatypeOffer) || this.subClassOf(datatypeOffer, datatypeRequirement);
        }
    }

    subClassOf(offer: string, requirement: string): boolean {
        if (!(requirement === (Datatypes.Number.toUri()))){
          return false;
        }
        else {
            if (offer === Datatypes.Integer.toUri()
                || offer === Datatypes.Long.toUri()
                || offer === Datatypes.Double.toUri()
                || offer === Datatypes.Float.toUri())
                return true;
        }
        return false;
    }
}