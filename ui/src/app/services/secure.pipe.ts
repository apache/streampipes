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

import { Pipe, PipeTransform } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { CurrentUserService } from '@streampipes/shared-ui';

@Pipe({
    name: 'secure',
})
export class SecurePipe implements PipeTransform {
    constructor(
        private http: HttpClient,
        private sanitizer: DomSanitizer,
        private currentUserService: CurrentUserService,
    ) {}

    transform(url): Observable<SafeUrl> {
        return this.http
            .get(url, {
                responseType: 'blob',
                headers: {
                    Authorization:
                        'Bearer ' + this.currentUserService.authToken$.value,
                },
            })
            .pipe(
                map(val =>
                    this.sanitizer.bypassSecurityTrustUrl(
                        URL.createObjectURL(val),
                    ),
                ),
            );
    }
}
