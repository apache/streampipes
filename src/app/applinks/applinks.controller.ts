export class AppLinksCtrl {

    RestApi: any;
    $window: any;
    applicationLinks: any;

    constructor(RestApi, $window) {
        this.RestApi = RestApi;
        this.$window = $window;
        this.applicationLinks = [];
    }

    $onInit() {
        this.loadApplicationLinks();
    }

    loadApplicationLinks() {
        this.RestApi.getApplicationLinks()
            .then(applicationLinks => {
                this.applicationLinks = applicationLinks.data;
            });
    }

    openApp(applicationUrl) {
        this.$window.open(applicationUrl, "_blank");
    }

}

AppLinksCtrl.$inject = ['RestApi', '$window'];