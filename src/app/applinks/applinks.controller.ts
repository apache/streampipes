export class AppLinksCtrl {

    RestApi: any;
    $window: any;
    applicationLinks: any;

    constructor(RestApi, $window) {
        this.RestApi = RestApi;
        this.$window = $window;
        this.applicationLinks = [];
        this.loadApplicationLinks();
    }

    loadApplicationLinks() {
        this.RestApi.getApplicationLinks()
            .success(applicationLinks => {
                this.applicationLinks = applicationLinks;
            })
            .error(error => {
                console.log(error);
            });
    }

    openApp(applicationUrl) {
        this.$window.open(applicationUrl, "_blank");
    }

}

AppLinksCtrl.$inject = ['RestApi', '$window'];