import 'legacy/stomp';

declare const Stomp: any;

export class AppCtrl {

    $state: any;

    constructor($state) {
        this.$state = $state;

    }

    go(path, payload?) {
        if (payload === undefined) {
            this.$state.go(path);
        } else {
            this.$state.go(path, payload);
        }
    }
};


