
export class LoginCtrl {

    $timeout: any;
    $log: any;
    $location: any;
    $state: any;
    $stateParams: any;
    $window: any;
    RestApi: any;
    AuthStatusService: any;
    loading: any;
    authenticationFailed: any;
    credentials: any;

    constructor($timeout, $log, $location, $state, $stateParams, RestApi, $window, AuthStatusService) {
        this.$timeout = $timeout;
        this.$log = $log;
        this.$location = $location;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$window = $window;
        this.RestApi = RestApi;
        this.AuthStatusService = AuthStatusService;

        this.loading = false;
        this.authenticationFailed = false;
    }


    openDocumentation(){
        this.$window.open('https://docs.streampipes.org', '_blank');
    };

    logIn() {
        this.authenticationFailed = false;
        this.loading = true;
        this.RestApi.login(this.credentials)
            .then(response => { // success
                    this.loading = false;
                    if (response.data.success) {
                        this.AuthStatusService.username = response.data.info.authc.principal.username;
                        this.AuthStatusService.email = response.data.info.authc.principal.email;
                        this.AuthStatusService.token = response.data.token;
                        this.AuthStatusService.authenticated = true;

                        this.$state.go("streampipes");
                    }
                    else {
                        this.AuthStatusService.authenticated = false;
                        this.authenticationFailed = true;
                    }

                }, response => { // error
                    this.loading = false;
                    this.AuthStatusService.authenticated = false;
                    this.authenticationFailed = true;
                }
            )
    };
};

//LoginCtrl.$inject = ['$timeout', '$log', '$location', '$state', '$stateParams', 'RestApi', '$window', 'AuthStatusService'];
