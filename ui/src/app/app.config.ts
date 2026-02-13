/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import { routes } from './app.routes';
import { provideRouter, withHashLocation } from '@angular/router';
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import {
    HTTP_INTERCEPTORS,
    provideHttpClient,
    withInterceptorsFromDi,
} from '@angular/common/http';
import { HttpInterceptorProvider } from './http-interceptor';
import { LOADING_BAR_CONFIG } from '@ngx-loading-bar/core';
import {
    DefaultMatCalendarRangeStrategy,
    MatDatepickerModule,
    MatRangeDateSelectionModel,
} from '@angular/material/datepicker';
import { SafeCss } from './editor/utils/style-sanitizer';
import { SecurePipe } from './services/secure.pipe';
import {
    PaginatorService,
    SortByRuntimeNamePipe,
} from '@streampipes/shared-ui';
import { ShortenPipe } from './core/pipes/shorten.pipe';
import { DisplayRecommendedPipe } from './core-ui/static-properties/filter/display-recommended.pipe';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { provideAnimations } from '@angular/platform-browser/animations';
import { LoadingBarHttpClientModule } from '@ngx-loading-bar/http-client';
import { MarkdownModule } from 'ngx-markdown';
import { TranslateModule } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { NgxEchartsModule } from 'ngx-echarts';
import { MatNativeDateModule } from '@angular/material/core';

export const appConfig: ApplicationConfig = {
    providers: [
        importProvidersFrom(
            MatDatepickerModule,
            MatNativeDateModule,
            LoadingBarHttpClientModule,
            MarkdownModule.forRoot(),
            TranslateModule.forRoot({
                loader: provideTranslateHttpLoader({
                    prefix: './assets/i18n/',
                    suffix: '.json',
                }),
            }),
            NgxEchartsModule.forRoot({
                echarts: () => import('echarts'),
            }),
        ),
        provideHttpClient(withInterceptorsFromDi()),
        provideRouter(routes, withHashLocation()),
        {
            provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
            useValue: { appearance: 'outline', subscriptSizing: 'dynamic' },
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: HttpInterceptorProvider,
            multi: true,
        },
        { provide: LOADING_BAR_CONFIG, useValue: { latencyThreshold: 100 } },
        DefaultMatCalendarRangeStrategy,
        MatRangeDateSelectionModel,
        SafeCss,
        SecurePipe,
        SortByRuntimeNamePipe,
        ShortenPipe,
        DisplayRecommendedPipe,
        {
            provide: MatPaginatorIntl,
            useClass: PaginatorService,
        },
        provideAnimations(),
    ],
};
