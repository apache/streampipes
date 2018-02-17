export class ConfigurationCtrl {

    constructor(ConfigurationRestService) {
        this.services = {};
        this.ConfigurationRestService = ConfigurationRestService;
        this.getConfigurations();
    }

    getConfigurations() {
        this.ConfigurationRestService.get().then(services => {
            for (let service of services.data) {
                for (let config of service.configs) {
                    if (config.valueType === 'xs:integer') {
                        config.value = parseInt(config.value);
                    } else if (config.valueType === 'xs:double') {
                        config.value = parseFloat('xs:double');
                    } else if (config.valueType === 'xs:boolean') {
                        config.value = (config.value === 'true');
                    }
                }
            }
            this.services = services.data;
        });
    }

    updateConfigurations(serviceDetails) {
        this.ConfigurationRestService.update(serviceDetails).then(response => {

        });
    }

}

ConfigurationCtrl.$inject = ['ConfigurationRestService'];