import {RestApi} from "./rest-api.service";
import {AuthStatusService} from "./auth-status.service";

export class AuthService {

    AuthStatusService: AuthStatusService;
    $rootScope: any;
    $location: any;
    $state: any;
    RestApi: RestApi;

    constructor($rootScope, $location, $state, RestApi, AuthStatusService) {
        this.AuthStatusService = AuthStatusService;
        this.$rootScope = $rootScope;
        this.$location = $location;
        this.$state = $state;
        this.RestApi = RestApi;
    }

    checkConfiguration() {
        return this.RestApi.configured().then(response => {
            if (response.data.configured) {
                this.AuthStatusService.configured = true;

                this.$rootScope.appConfig = response.data.appConfig;
            } else {
                this.AuthStatusService.configured = false;
            }
        });
    }

    checkAuthentication() {
        return this.RestApi.getAuthc().then(response => {
            if (response.data.success) {
                this.AuthStatusService.username = response.data.info.authc.principal.username;
                this.AuthStatusService.email = response.data.info.authc.principal.email;
                this.AuthStatusService.authenticated = true;
                this.AuthStatusService.token = response.data.token;

                // this.RestApi.getNotifications()
                //     .then(notifications => {
                //         this.$rootScope.unreadNotifications = notifications.data;
                //     });
            } else {
                this.AuthStatusService.authenticated = false;
            }
        })
    }
}