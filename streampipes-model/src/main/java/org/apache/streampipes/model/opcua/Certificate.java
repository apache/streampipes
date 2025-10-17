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

package org.apache.streampipes.model.opcua;

import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

@TsModel
public final class Certificate implements Storable {

  @JsonAlias("_id")
  @SerializedName("_id")
  private String elementId;

  @JsonAlias("_rev")
  @SerializedName("_rev")
  private String rev;

  private String subjectDn;
  private String issuerDn;
  private String serialNumber;
  private String notBefore;
  private String notAfter;
  private String sigAlgName;
  private String algorithm;
  private String basicConstraints;
  private List<String> keyUsages;
  private List<String> extendedKeyUsages;
  private List<String> subjectAlternativeNames;
  private String certificateDerBase64;

  private CertificateState state;


  public Certificate() {
  }

  public Certificate(String subjectDn,
                     String issuerDn,
                     String serialNumber,
                     String notBefore,
                     String notAfter,
                     String sigAlgName,
                     String algorithm,
                     String certificateDerBase64,
                     CertificateState state) {
    this.subjectDn = subjectDn;
    this.issuerDn = issuerDn;
    this.serialNumber = serialNumber;
    this.notBefore = notBefore;
    this.notAfter = notAfter;
    this.sigAlgName = sigAlgName;
    this.algorithm = algorithm;
    this.certificateDerBase64 = certificateDerBase64;
    this.state = state;
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return elementId;
  }

  @Override
  public void setElementId(String elementId) {
    this.elementId = elementId;
  }

  public String getSubjectDn() {
    return subjectDn;
  }

  public String getIssuerDn() {
    return issuerDn;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public String getNotBefore() {
    return notBefore;
  }

  public String getNotAfter() {
    return notAfter;
  }

  public String getSigAlgName() {
    return sigAlgName;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public String getCertificateDerBase64() {
    return certificateDerBase64;
  }

  public CertificateState getState() {
    return state;
  }

  public void setSubjectDn(String subjectDn) {
    this.subjectDn = subjectDn;
  }

  public void setIssuerDn(String issuerDn) {
    this.issuerDn = issuerDn;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setNotBefore(String notBefore) {
    this.notBefore = notBefore;
  }

  public void setNotAfter(String notAfter) {
    this.notAfter = notAfter;
  }

  public void setSigAlgName(String sigAlgName) {
    this.sigAlgName = sigAlgName;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getBasicConstraints() {
    return basicConstraints;
  }

  public void setBasicConstraints(String basicConstraints) {
    this.basicConstraints = basicConstraints;
  }

  public List<String> getKeyUsages() {
    return keyUsages;
  }

  public void setKeyUsages(List<String> keyUsages) {
    this.keyUsages = keyUsages;
  }

  public List<String> getExtendedKeyUsages() {
    return extendedKeyUsages;
  }

  public void setExtendedKeyUsages(List<String> extendedKeyUsages) {
    this.extendedKeyUsages = extendedKeyUsages;
  }

  public List<String> getSubjectAlternativeNames() {
    return subjectAlternativeNames;
  }

  public void setSubjectAlternativeNames(List<String> subjectAlternativeNames) {
    this.subjectAlternativeNames = subjectAlternativeNames;
  }

  public void setCertificateDerBase64(String certificateDerBase64) {
    this.certificateDerBase64 = certificateDerBase64;
  }

  public void setState(CertificateState state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Certificate that = (Certificate) o;
    return Objects.equals(getSubjectDn(), that.getSubjectDn()) && Objects.equals(getIssuerDn(), that.getIssuerDn()) && Objects.equals(getSerialNumber(), that.getSerialNumber()) && Objects.equals(getNotBefore(), that.getNotBefore()) && Objects.equals(getNotAfter(), that.getNotAfter()) && Objects.equals(getSigAlgName(), that.getSigAlgName()) && Objects.equals(getAlgorithm(), that.getAlgorithm()) && Objects.equals(getCertificateDerBase64(), that.getCertificateDerBase64()) && getState() == that.getState();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSubjectDn(), getIssuerDn(), getSerialNumber(), getNotBefore(), getNotAfter(), getSigAlgName(), getAlgorithm(), getCertificateDerBase64(), getState());
  }
}
