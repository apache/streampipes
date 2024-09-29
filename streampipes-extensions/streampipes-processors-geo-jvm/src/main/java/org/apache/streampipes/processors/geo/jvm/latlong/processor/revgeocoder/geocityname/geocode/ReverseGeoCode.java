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
package org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname.geocode;

import org.apache.streampipes.processors.geo.jvm.latlong.processor.revgeocoder.geocityname.geocode.kdtree.KDTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Daniel Glasson on 18/05/2014. Uses KD-trees to quickly find the nearest point
 * <p>
 * ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream("c:\\AU.txt"), true);
 * System.out.println("Nearest to -23.456, 123.456 is " + geocode.nearestPlace(-23.456, 123.456));
 */
public class ReverseGeoCode {
  KDTree<GeoName> kdTree;

  // Get placenames from http://download.geonames.org/export/dump/

  /**
   * Parse the zipped geonames file.
   *
   * @param zippedPlacednames
   *          a {@link ZipInputStream} zip file downloaded from http://download.geonames.org/export/dump/; can not be
   *          null.
   * @param majorOnly
   *          only include major cities in KD-tree.
   * @throws IOException
   *           if there is a problem reading the {@link ZipInputStream}.
   * @throws NullPointerException
   *           if zippedPlacenames is {@code null}.
   */
  public ReverseGeoCode(ZipInputStream zippedPlacednames, boolean majorOnly) throws IOException {
    // depending on which zip file is given,
    // country specific zip files have read me files
    // that we should ignore
    ZipEntry entry;
    do {
      entry = zippedPlacednames.getNextEntry();
    } while (entry.getName().equals("readme.txt"));

    createKdTree(zippedPlacednames, majorOnly);

  }

  public ReverseGeoCode(InputStream inputStream, boolean majorOnly, boolean extract) throws IOException {
    createKdTree(inputStream, majorOnly);
  }

  /**
   * Parse the raw text geonames file.
   *
   * @param placenames
   *          the text file downloaded from http://download.geonames.org/export/dump/; can not be null.
   * @param majorOnly
   *          only include major cities in KD-tree.
   * @throws IOException
   *           if there is a problem reading the stream.
   * @throws NullPointerException
   *           if zippedPlacenames is {@code null}.
   */
  public ReverseGeoCode(InputStream placenames, boolean majorOnly) throws IOException {
    createKdTree(placenames, majorOnly);
  }

  private void createKdTree(InputStream placenames, boolean majorOnly) throws IOException {
    ArrayList<GeoName> arPlaceNames;
    arPlaceNames = new ArrayList<GeoName>();
    // Read the geonames file in the directory
    BufferedReader in = new BufferedReader(new InputStreamReader(placenames));
    String str;
    try {
      while ((str = in.readLine()) != null) {
        GeoName newPlace = new GeoName(str);
        if (!majorOnly || newPlace.majorPlace) {
          arPlaceNames.add(newPlace);
        }
      }
    } catch (IOException ex) {
      throw ex;
    } finally {
      in.close();
    }
    kdTree = new KDTree<GeoName>(arPlaceNames);
  }

  public GeoName nearestPlace(double latitude, double longitude) {
    return kdTree.findNearest(new GeoName(latitude, longitude));
  }
}
