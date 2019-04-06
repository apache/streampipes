import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Rx";

@Injectable()
export class AppPallet3dRestService {


    path = "/api/pointcloud";


    constructor(private http: HttpClient) {
    }

    getPalletInfo():Observable<any> {
        return this.http.get(this.path);
    }
}