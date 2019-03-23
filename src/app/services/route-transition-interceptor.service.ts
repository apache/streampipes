import {AuthService} from "./auth.service";
import {AuthStatusService} from "./auth-status.service";

export class RouteTransitionInterceptorService {

    AuthService: AuthService;
    AuthStatusService: AuthStatusService;
    $q: any;

    publicPages: string[] = ["login", "register", "setup"];

    constructor(AuthService, AuthStatusService, $q) {
        this.AuthService = AuthService;
        this.AuthStatusService = AuthStatusService;
        this.$q = $q;
    }

    onTransitionStarted(transitionInfo) {
        return new Promise(resolve => {
            this.AuthService.checkConfiguration().then(() => {
                if (this.AuthStatusService.configured) {
                    this.AuthService.checkAuthentication().then(() => {
                        if (this.isProtectedPage(transitionInfo.$to().name) && !(this.AuthStatusService.authenticated)) {
                            resolve(transitionInfo.router.stateService.target('login'));
                        } else {
                            if (this.AuthStatusService.authenticated && (transitionInfo.$to().name === 'login'
                                || transitionInfo.$to().name === 'setup')) {
                                resolve(transitionInfo.router.stateService.target('streampipes'));
                            } else {
                                if (transitionInfo.$to().name === 'setup') {
                                    resolve(transitionInfo.router.stateService.target('streampipes'));
                                } else {
                                    resolve(true);
                                }
                            }
                        }
                    })
                } else {
                    if (transitionInfo.$to().name == 'setup') {
                        resolve(true);
                    } else {
                        resolve(transitionInfo.router.stateService.target('setup'));
                    }
                }
            });
        });
    }

    isProtectedPage(target) {
        return !(this.publicPages.some(p => p === target));
    }

}