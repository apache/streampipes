export class ConsulServiceController {
    constructor() {
        this.showConfiguration = false;
    }

    updateConfiguration() {
        this.onUpdate({serviceDetails: this.serviceDetails});
    }

    toggleConfiguration() {
        this.showConfiguration = !this.showConfiguration;
    }
}