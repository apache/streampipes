export class DomainPropertyMatch {

    match(domainPropertyRequirement: string, domainPropertyOffer: string) {
        if (domainPropertyRequirement == undefined) {
            return true;
        } else {
            return domainPropertyRequirement == domainPropertyOffer;
        }
    }
}