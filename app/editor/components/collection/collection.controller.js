export class CollectionController {

    constructor() {

    }

    addMember(sp) {
        sp.properties.members.push(angular.copy(sp.properties.members[0]));
        sp.properties.members[sp.properties.members.length - 1].properties.elementName =
            sp.properties.members[sp.properties.members.length - 1].properties.elementName.concat(this.makeId());
    }

    removeMember(sp, index) {
        sp.properties.members.splice(index, 1);
    }

    makeId() {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < 5; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
}