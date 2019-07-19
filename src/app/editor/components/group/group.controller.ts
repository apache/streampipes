export class GroupController {

    staticProperty: any;
    selectedElement: any;

    constructor() {
    }

    $onInit() {
        console.log("group");
        console.log(this.selectedElement);
        console.log(this.staticProperty);
    }

}