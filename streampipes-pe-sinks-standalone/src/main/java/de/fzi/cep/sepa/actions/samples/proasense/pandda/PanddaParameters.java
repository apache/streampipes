package de.fzi.cep.sepa.actions.samples.proasense.pandda;

/**
 * Created by riemer on 12.02.2017.
 */
public class PanddaParameters {

  private String pdfTypePropertyKey;
  private String paramsPropertyKey;
  private String timestampsPropertyKey;

  public PanddaParameters() {

  }

  public PanddaParameters(String pdfTypePropertyKey, String paramsPropertyKey, String timestampsPropertyKey) {
    this.pdfTypePropertyKey = pdfTypePropertyKey;
    this.paramsPropertyKey = paramsPropertyKey;
    this.timestampsPropertyKey = timestampsPropertyKey;
  }

  public String getPdfTypePropertyKey() {
    return pdfTypePropertyKey;
  }

  public void setPdfTypePropertyKey(String pdfTypePropertyKey) {
    this.pdfTypePropertyKey = pdfTypePropertyKey;
  }

  public String getParamsPropertyKey() {
    return paramsPropertyKey;
  }

  public void setParamsPropertyKey(String paramsPropertyKey) {
    this.paramsPropertyKey = paramsPropertyKey;
  }

  public String getTimestampsPropertyKey() {
    return timestampsPropertyKey;
  }

  public void setTimestampsPropertyKey(String timestampsPropertyKey) {
    this.timestampsPropertyKey = timestampsPropertyKey;
  }
}
