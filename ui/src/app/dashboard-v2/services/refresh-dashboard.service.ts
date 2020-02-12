import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";

@Injectable()
export class RefreshDashboardService {

    public refreshSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

    public notify(reload: boolean): void {
        this.refreshSubject.next(reload);
    }
}