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

package org.apache.streampipes.connect.adapters.gdelt;

//public class GdeltAdapter extends PullAdapter {
//
//  public static final String ID = "org.apache.streampipes.connect.adapters.gdelt";
//
//
//  private String url = "http://data.gdeltproject.org/gdeltv2/lastupdate.txt";
//
//  public GdeltAdapter() {
//    super();
//  }
//
//  public GdeltAdapter(SpecificAdapterStreamDescription adapterDescription) {
//    super(adapterDescription);
//  }
//
//  @Override
//  protected PollingSettings getPollingInterval() {
//    return PollingSettings.from(TimeUnit.MINUTES, 15);
//  }
//
//  @Override
//  public SpecificAdapterStreamDescription declareModel() {
//    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
//        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
//        .withLocales(Locales.EN)
//        .build();
//    return description;
//  }
//
//  public List<Map<String, Object>> getEvents() {
//
//    List<Map<String, Object>> eventResults = new ArrayList<>();
//    return eventResults;
//  }
//
//  @Override
//  protected void pullData() {
//    try {
//      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
//      String firstLine = bufferedReader.readLine();
//      String[] parts = firstLine.split(" ");
//      URL zipFileUrl = new URL(parts[2]);
//      bufferedReader.close();
//      ZipInputStream zipInputStream = new ZipInputStream(zipFileUrl.openStream());
//      BufferedReader zipBufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
//      zipInputStream.getNextEntry();
//      CSVReader csvReader = new CSVReader(zipBufferedReader);
//      String[] nextRecord;
//      while ((nextRecord = csvReader.readNext()) != null) {
//
//        Map<String, Object> event = new HashMap<>();
//
//        event.put("global_event_id", nextRecord[0]);
//        event.put("day", nextRecord[1]);
//        event.put("month_year", nextRecord[2]);
//        event.put("year", nextRecord[3]);
//        event.put("fraction_date", nextRecord[4]);
//
//        event.put("actor_1_code", nextRecord[5]);
//        event.put("actor_1_name", nextRecord[6]);
//        event.put("actor_1_country_code", nextRecord[7]);
//        event.put("actor_1_known_group_code", nextRecord[8]);
//        event.put("actor_1_ethnic_code", nextRecord[9]);
//        event.put("actor_1_religion_1_code", nextRecord[10]);
//        event.put("actor_1_religion_2_code", nextRecord[11]);
//        event.put("actor_1_type_1_code", nextRecord[12]);
//        event.put("actor_1_type_2_code", nextRecord[13]);
//        event.put("actor_1_type_3_code", nextRecord[14]);
//
//        event.put("actor_2_code", nextRecord[15]);
//        event.put("actor_2_name", nextRecord[16]);
//        event.put("actor_2_country_code", nextRecord[17]);
//        event.put("actor_2_known_group_code", nextRecord[18]);
//        event.put("actor_2_ethnic_code", nextRecord[19]);
//        event.put("actor_2_religion_1_code", nextRecord[20]);
//        event.put("actor_2_religion_2_code", nextRecord[21]);
//        event.put("actor_2_type_1_code", nextRecord[22]);
//        event.put("actor_2_type_2_code", nextRecord[23]);
//        event.put("actor_2_type_3_code", nextRecord[24]);
//
//        event.put("source_url", nextRecord[60]);
//        adapterPipeline.process(event);
//
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//  }
//
//  @Override
//  public void stopAdapter() throws AdapterException {
//
//  }
//
//  @Override
//  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
//    return new GdeltAdapter(adapterDescription);
//  }
//
//  @Override
//  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) {
//    GuessSchema guessSchema = new GuessSchema();
//    EventSchema eventSchema = new EventSchema();
//
//    List<EventProperty> allProperties = new ArrayList<>();
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.Integer, "global_event_id").label("Global Event ID")
//        .description("Globally unique identifier").build());
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.Integer, "day").label("Day")
//        .description("Date the event took place in YYYYMMDD format").build());
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.Integer, "month_year").label("MonthYear")
//        .description("Date the event took place in YYYYMM format").build());
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.Integer, "year").label("Year")
//        .description("Date the event took place in YYYY format").build());
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.Float, "fraction_date").label("FractionDate")
//        .description(
//            "Date the event took place in YYYY.FFFF format (where FFFF is the percentage of the year completed)")
//        .build());
//
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_code").label("Actor1Code")
//        .description("The complete raw CAMEO code for Actor1").build());
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_name").label("Actor1Name")
//        .description("The actual name of the Actor1").build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_country_code").label("Actor1CountryCode")
//            .description("The 3-character CAMEO code for the country affiliation of Actor1").build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_known_group_code").label("Actor1KnownGroupCode")
//            .description("The CAMEO code if Actor 1 is a known IGO/NGO/rebel organization").build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_ethnic_code").label("Actor1EthnicCode")
//        .description("The CAMEO code if the source document specifies the ethnic affiliation of Actor1").build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_religion_1_code").label("Actor1Religion1Code")
//            .description("The CAMEO code if the source document specifies the religious affiliation of Actor1")
//            .build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_religion_2_code").label("Actor1Religion2Code")
//            .description("The CAMEO code if the source document specifies multiple religious affiliations of Actor1")
//            .build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_type_1_code").label("Actor1Type1Code")
//        .description("The 3-character CAMEO code of the CAMEO “type” or “role” of Actor1").build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_type_2_code").label("Actor1Type2Code")
//        .description("The 3-character CAMEO code of the CAMEO “type” or “role” of Actor1").build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_1_type_3_code").label("Actor1Type3Code")
//        .description("The 3-character CAMEO code of the CAMEO “type” or “role” of Actor1").build());
//
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_code").label("Actor2Code")
//        .description("The complete raw CAMEO code for Actor2").build());
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_name").label("Actor2Name")
//        .description("The actual name of the Actor2").build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_country_code").label("Actor2CountryCode")
//            .description("The 3-character CAMEO code for the country affiliation of Actor2").build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_known_group_code").label("Actor2KnownGroupCode")
//            .description("The CAMEO code if Actor 1 is a known IGO/NGO/rebel organization").build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_ethnic_code").label("Actor2EthnicCode")
//        .description("The CAMEO code if the source document specifies the ethnic affiliation of Actor2").build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_religion_1_code").label("Actor2Religion1Code")
//            .description("The CAMEO code if the source document specifies the religious affiliation of Actor2")
//            .build());
//    allProperties.add(
//        PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_religion_2_code").label("Actor2Religion2Code")
//            .description("The CAMEO code if the source document specifies multiple religious affiliations of Actor2")
//            .build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_type_1_code").label("Actor2Type1Code")
//        .description("The 3-character CAMEO code of the CAMEO “type” or “role” of Actor2").build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_type_2_code").label("Actor2Type2Code")
//        .description("The 3-character CAMEO code of the CAMEO “type” or “role” of Actor2").build());
//    allProperties
//    .add(PrimitivePropertyBuilder.create(Datatypes.String, "actor_2_type_3_code").label("Actor2Type3Code")
//        .description("The 3-character CAMEO code of the CAMEO “type” or “role” of Actor2").build());
//
//    allProperties.add(PrimitivePropertyBuilder.create(Datatypes.String, "source_url").label("URL")
//        .description("The url of the source of the article").build());
//
//    eventSchema.setEventProperties(allProperties);
//    guessSchema.setEventSchema(eventSchema);
//    return guessSchema;
//  }
//
//  @Override
//  public String getId() {
//    return ID;
//  }
//}
