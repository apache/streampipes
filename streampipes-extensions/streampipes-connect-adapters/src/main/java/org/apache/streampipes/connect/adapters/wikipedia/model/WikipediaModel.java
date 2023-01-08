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

package org.apache.streampipes.connect.adapters.wikipedia.model;

import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class WikipediaModel {

  @SerializedName("bot")
  private Boolean mBot;
  @SerializedName("comment")
  private String mComment;
  @SerializedName("id")
  private Long mId;
  @SerializedName("length")
  private Length mLength;
  @SerializedName("meta")
  private Meta mMeta;
  @SerializedName("minor")
  private Boolean mMinor;
  @SerializedName("namespace")
  private Long mNamespace;
  @SerializedName("parsedcomment")
  private String mParsedcomment;
  @SerializedName("patrolled")
  private Boolean mPatrolled;
  @SerializedName("revision")
  private Revision mRevision;
  @SerializedName("server_name")
  private String mServerName;
  @SerializedName("server_script_path")
  private String mServerScriptPath;
  @SerializedName("server_url")
  private String mServerUrl;
  @SerializedName("timestamp")
  private Long mTimestamp;
  @SerializedName("title")
  private String mTitle;
  @SerializedName("type")
  private String mType;
  @SerializedName("user")
  private String mUser;
  @SerializedName("wiki")
  private String mWiki;

  public Boolean getBot() {
    return mBot;
  }

  public void setBot(Boolean bot) {
    mBot = bot;
  }

  public String getComment() {
    return mComment;
  }

  public void setComment(String comment) {
    mComment = comment;
  }

  public Long getId() {
    return mId;
  }

  public void setId(Long id) {
    mId = id;
  }

  public Length getLength() {
    return mLength;
  }

  public void setLength(Length length) {
    mLength = length;
  }

  public Meta getMeta() {
    return mMeta;
  }

  public void setMeta(Meta meta) {
    mMeta = meta;
  }

  public Boolean getMinor() {
    return mMinor;
  }

  public void setMinor(Boolean minor) {
    mMinor = minor;
  }

  public Long getNamespace() {
    return mNamespace;
  }

  public void setNamespace(Long namespace) {
    mNamespace = namespace;
  }

  public String getParsedcomment() {
    return mParsedcomment;
  }

  public void setParsedcomment(String parsedcomment) {
    mParsedcomment = parsedcomment;
  }

  public Boolean getPatrolled() {
    return mPatrolled;
  }

  public void setPatrolled(Boolean patrolled) {
    mPatrolled = patrolled;
  }

  public Revision getRevision() {
    return mRevision;
  }

  public void setRevision(Revision revision) {
    mRevision = revision;
  }

  public String getServerName() {
    return mServerName;
  }

  public void setServerName(String serverName) {
    mServerName = serverName;
  }

  public String getServerScriptPath() {
    return mServerScriptPath;
  }

  public void setServerScriptPath(String serverScriptPath) {
    mServerScriptPath = serverScriptPath;
  }

  public String getServerUrl() {
    return mServerUrl;
  }

  public void setServerUrl(String serverUrl) {
    mServerUrl = serverUrl;
  }

  public Long getTimestamp() {
    return mTimestamp;
  }

  public void setTimestamp(Long timestamp) {
    mTimestamp = timestamp;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    mTitle = title;
  }

  public String getType() {
    return mType;
  }

  public void setType(String type) {
    mType = type;
  }

  public String getUser() {
    return mUser;
  }

  public void setUser(String user) {
    mUser = user;
  }

  public String getWiki() {
    return mWiki;
  }

  public void setWiki(String wiki) {
    mWiki = wiki;
  }

}
