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

import {Injectable} from "@angular/core";
import {PlatformServicesCommons} from "../platform-services/apis/commons.service";
import {HttpClient} from "@angular/common/http";
import {RawUserApiToken, User} from "../core-model/gen/streampipes-model-client";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {Message} from "../core-model/gen/streampipes-model";

@Injectable()
export class ProfileService {

  constructor(private http: HttpClient,
              private platformServicesCommons: PlatformServicesCommons) {

  }

  getUserProfile(): Observable<User> {
    return this.http.get(this.platformServicesCommons.authUserBasePath()).pipe(map(response => {
      return User.fromData(response as any);
    }));
  }

  updateUserProfile(userData: User): Observable<Message> {
    return this.http.put(this.platformServicesCommons.authUserBasePath(), userData).pipe(map(response => {
      return Message.fromData(response as any);
    }))
  }

  updateAppearanceMode(darkMode: boolean): Observable<Message> {
    return this.http.put(`${this.platformServicesCommons.authUserBasePath()}/appearance/mode/${darkMode}`, {}).pipe(map(response => {
      return Message.fromData(response as any);
    }))
  }

  requestNewApiToken(baseToken: RawUserApiToken): Observable<RawUserApiToken> {
    return this.http.post(this.platformServicesCommons.authUserBasePath() + "/tokens", baseToken)
        .pipe(map(response => {
          return RawUserApiToken.fromData(response as any);
        }))
  }
}
