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

package org.streampipes.connect.adapters.coindesk.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Time {

    @SerializedName("updated")
    private String mUpdated;
    @SerializedName("updatedISO")
    private String mUpdatedISO;
    @SerializedName("updateduk")
    private String mUpdateduk;

    public String getUpdated() {
        return mUpdated;
    }

    public void setUpdated(String updated) {
        mUpdated = updated;
    }

    public String getUpdatedISO() {
        return mUpdatedISO;
    }

    public void setUpdatedISO(String updatedISO) {
        mUpdatedISO = updatedISO;
    }

    public String getUpdateduk() {
        return mUpdateduk;
    }

    public void setUpdateduk(String updateduk) {
        mUpdateduk = updateduk;
    }

}
