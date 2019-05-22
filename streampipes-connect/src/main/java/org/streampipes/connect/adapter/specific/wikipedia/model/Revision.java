
package org.streampipes.connect.adapter.specific.wikipedia.model;

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
