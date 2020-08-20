import {Observable} from "rxjs";
import {
  RuntimeOptionsRequest,
  RuntimeOptionsResponse
} from "../../../core-model/gen/streampipes-model";
import {map} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";
import {AuthStatusService} from "../../../services/auth-status.service";
import {PlatformServicesCommons} from "../../../platform-services/apis/commons.service";
import {Injectable} from "@angular/core";

@Injectable()
export class RuntimeResolvableService {

  constructor(private http: HttpClient,
              private authStatusService: AuthStatusService,
              private platformServicesCommons: PlatformServicesCommons) {

  }

  fetchRemoteOptionsForAdapter(resolvableOptionsParameterRequest: RuntimeOptionsRequest, adapterId: string): Observable<RuntimeOptionsResponse> {
    let url: string = "/streampipes-connect/api/v1/"
        + this.authStatusService.email
        + "/master/resolvable/"
        + encodeURIComponent(adapterId)
        + "/configurations";
    return this.fetchRemoteOptions(url, resolvableOptionsParameterRequest);
  }

  fetchRemoteOptionsForPipelineElement(resolvableOptionsParameterRequest: RuntimeOptionsRequest): Observable<RuntimeOptionsResponse> {
    let url: string = this.platformServicesCommons.authUserBasePath() + "/pe/options";
    return this.fetchRemoteOptions(url, resolvableOptionsParameterRequest);
  }

  fetchRemoteOptions(url, resolvableOptionsParameterRequest) {
    resolvableOptionsParameterRequest["@class"] = "org.apache.streampipes.model.runtime.RuntimeOptionsRequest";
    return this.http.post(url, resolvableOptionsParameterRequest)
        .pipe(map(response => {
          return RuntimeOptionsResponse.fromData(response as RuntimeOptionsResponse);
        }));
  }

}