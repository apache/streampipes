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
package org.apache.streampipes.model.client.assetdashboard;

public class CanvasAttributes {

  private float x;
  private float y;
  private float width;
  private float height;
  private float scaleX = 1;
  private float scaleY = 1;
  private float rotation = 0;
  private String fill;
  private String text;
  private String align;
  private String fontSize;

  private String id;
  private String visualizationId;

  private String brokerUrl;
  private String topic;
  private String name;

  private String hyperlink;
  private boolean newWindow;
  private String fontStyle;

  public CanvasAttributes() {
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getScaleX() {
    return scaleX;
  }

  public void setScaleX(float scaleX) {
    this.scaleX = scaleX;
  }

  public float getScaleY() {
    return scaleY;
  }

  public void setScaleY(float scaleY) {
    this.scaleY = scaleY;
  }

  public String getFill() {
    return fill;
  }

  public void setFill(String fill) {
    this.fill = fill;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVisualizationId() {
    return visualizationId;
  }

  public void setVisualizationId(String visualizationId) {
    this.visualizationId = visualizationId;
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public String getBrokerUrl() {
    return brokerUrl;
  }

  public void setBrokerUrl(String brokerUrl) {
    this.brokerUrl = brokerUrl;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlign() {
    return align;
  }

  public void setAlign(String align) {
    this.align = align;
  }

  public String getFontSize() {
    return fontSize;
  }

  public void setFontSize(String fontSize) {
    this.fontSize = fontSize;
  }

  public String getHyperlink() {
    return hyperlink;
  }

  public void setHyperlink(String hyperlink) {
    this.hyperlink = hyperlink;
  }

  public boolean isNewWindow() {
    return newWindow;
  }

  public void setNewWindow(boolean newWindow) {
    this.newWindow = newWindow;
  }

  public String getFontStyle() {
    return fontStyle;
  }

  public void setFontStyle(String fontStyle) {
    this.fontStyle = fontStyle;
  }
}
