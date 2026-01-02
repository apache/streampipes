import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
    AdapterDescription,
    PlatformServicesCommons,
    ScriptMetadata,
} from '@streampipes/platform-services';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ConnectScriptLanguagesService {
    private http = inject(HttpClient);
    private platformServicesCommons = inject(PlatformServicesCommons);

    getAll(
        adapterDescription: AdapterDescription,
    ): Observable<ScriptMetadata[]> {
        return this.http.post<ScriptMetadata[]>(
            this.baseUrl,
            adapterDescription,
        );
    }

    get baseUrl(): string {
        return `${this.platformServicesCommons.apiBasePath}/connect/master/script-languages`;
    }
}
