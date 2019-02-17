import * as angular from 'angular';

export class CollectionController {

    staticProperty: any;

    constructor() {

    }

    $onInit() {
        console.log(this.staticProperty);
    }

    addMember(sp) {
        console.log(sp);
        let newMember = angular.copy(this.staticProperty.properties.staticPropertyTemplate);
        newMember.properties.elementId = newMember.properties.elementId.concat(this.makeId());
        sp.properties.members.push(newMember);
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