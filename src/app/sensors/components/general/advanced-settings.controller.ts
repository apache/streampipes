export class AdvancedSettingsController {

    visible: any;

    constructor() {
        this.visible = false;
    }
    
    showLabel() {
        return this.visible == true ? "Hide" : "Show";
    }

    advancedSettingsVisible() {
        return this.visible;
    }

    toggleAdvancedSettingsVisibility() {
        this.visible = !this.visible;
    }
}