export class CollapsibleController {

    constructor() {
        this.hide = true;
    }
    
    toggleVisibility() {
        this.hide = !this.hide;
    }

    removeProperty(list, ctr) {
        list.splice(ctr, 1);
    }
}