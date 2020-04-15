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

import {RdfId} from '../../../platform-services/tsonld/RdfId';
import {RdfProperty} from '../../../platform-services/tsonld/RdfsProperty';
import {RdfsClass} from '../../../platform-services/tsonld/RdfsClass';


@RdfsClass('sp:EventProperty')
export abstract class EventProperty {

  private static serialVersioUID = 7079045979946059387;
  protected static prefix = 'urn:fzi.de:sepa:';

  propertyID: string; // one time value to identify property!!
  parent: EventProperty;
  child?: EventProperty;

  propertySelector:string;

  propertyNumber: number; // what the user sees in the UI

  @RdfId
  public id: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#label')
  public label: string;

  @RdfProperty('http://www.w3.org/2000/01/rdf-schema#description')
  public description: string;

  @RdfProperty('sp:hasRuntimeName')
  public runtimeName: string;

  @RdfProperty('sp:domainProperty')
  public domainProperty: string;

  @RdfProperty('sp:hasIndex')
  public index: number;

  @RdfProperty('sp:hasPropertyScope')
  public propertyScope = 'MEASUREMENT_PROPERTY';

  constructor(propertyID: string, parent: EventProperty, child?: EventProperty) {
    this.propertyID = propertyID;
    this.id = "http://eventProperty.de/" + propertyID;
    this.parent = parent;
    this.child = child;
  }

  public getRuntimeName(): string {
    return this.runtimeName;
  }

  public setRuntimeName(propertyName: string): void {
    this.runtimeName = propertyName;
  }

  public setDomainProperty(domainProperty: string): void {
    this.domainProperty = domainProperty;

  }

  public getPropertyNumber(): string {
    return this.propertyNumber.toString();
  }

  public setLabel(humanReadableTitle: string): void {
    this.label = humanReadableTitle;
  }

  public getLabel(): string {
    return this.label;
  }

  public getDescription(): string {
    return this.description;
  }

  public setDescription(humanReadableDescription: string): void {
    this.description = humanReadableDescription;
  }

  public abstract copy(): EventProperty;

}
