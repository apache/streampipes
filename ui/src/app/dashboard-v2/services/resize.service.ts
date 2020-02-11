import {BehaviorSubject} from "rxjs";
import {Injectable} from "@angular/core";
import {GridsterInfo} from "../models/gridster-info.model";

@Injectable()
export class ResizeService {

    public resizeSubject: BehaviorSubject<GridsterInfo> = new BehaviorSubject<GridsterInfo>(null);

    public notify(info: GridsterInfo): void {
        this.resizeSubject.next(info);
    }
}