export class SetupCtrl {

    $location: any;
    RestApi: any;
    $mdToast:any;
    installationFinished: any;
    installationSuccessful: any;
    installationResults: any;
    loading: any;
    showAdvancedSettings: any;
    setup: any;
    setupForm: any;

    constructor($location, RestApi, $mdToast) {
        this.$location = $location;
        this.RestApi = RestApi;
        this.$mdToast = $mdToast;

        this.installationFinished = false;
        this.installationSuccessful = false;
        this.installationResults = [{}];
        this.loading = false;
        this.showAdvancedSettings = false;

        this.setup = {
            couchDbHost: '',
            sesameHost: '',
            kafkaHost: '',
            zookeeperHost: '',
            jmsHost: '',
            adminEmail: '',
            adminPassword: '',
        };
    }




    configure() {
        this.loading = true;
        this.RestApi.setupInstall(this.setup).success(data => {
            this.installationResults = data;

            this.RestApi.configured()
                .then(response => {
                    if (response.data.configured) {
                        this.installationFinished = true;
                        this.loading = false;
                    }
                }).error(data => {
                this.loading = false;
                this.showToast("Fatal error, contact administrator");
            });
        });
    }

    showToast(string) {
        this.$mdToast.show(
            this.$mdToast.simple()
                .content(string)
                .position("right")
                .hideDelay(3000)
        );
    };

    addPod(podUrls) {
        if (podUrls == undefined) podUrls = [];
        podUrls.push("localhost");
    }

    removePod(podUrls, index) {
        podUrls.splice(index, 1);
    }
};

//SetupCtrl.$inject = ['$location', 'RestApi', '$mdToast'];
