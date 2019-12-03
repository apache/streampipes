import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Dashboard } from '../models/dashboard.model';

@Injectable({
    providedIn: 'root'
})
export class MockDashboardService {

    private readonly DASHBOARDS = 'assets/dashboards.json';

    constructor(protected httpClient: HttpClient) {}

    public getDashboards(): Observable<Dashboard[]> {

        return this.httpClient.get<Dashboard[]>(this.DASHBOARDS);
    }

    public getDashboard(dashboardId: string): Observable<Dashboard>  {

        return this.httpClient.get<Dashboard[]>(this.DASHBOARDS).pipe(
            map((dashboards: Dashboard[]) =>
                dashboards.find(dashboard => dashboard.id === dashboardId)));
    }

}