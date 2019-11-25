/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapters.wikipedia.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Revision {

    @SerializedName("new")
    private Long mNew;
    @SerializedName("old")
    private Long mOld;

    public Long getNew() {
        return mNew;
    }

    public void setNew(Long newRevision) {
        mNew = newRevision;
    }

    public Long getOld() {
        return mOld;
    }

    public void setOld(Long old) {
        mOld = old;
    }

}
