/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

package org.streampipes.connect.adapter.generic.format.image;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.grounding.FormatDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageFormat extends Format {

    public static final String ID = "https://streampipes.org/vocabulary/v1/format/image";

    public ImageFormat() {

    }

    @Override
    public Format getInstance(FormatDescription formatDescription) {
        return new ImageFormat();
    }

    @Override
    public FormatDescription declareModel() {
        FormatDescription fd = new FormatDescription(ID, "Image", "Allows to process images");

        fd.setAppId(ID);
        return fd;
    }


    public static void main(String... args) throws IOException {
//        http://141.21.43.35/record/current.jpg
        InputStream result = Request.Get("https://upload.wikimedia.org/wikipedia/commons/9/95/KWF_Test.png")
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute()
                    .returnContent()
                    .asStream();

        byte[] b =  IOUtils.toByteArray(result);
        System.out.println(Base64.getEncoder().encodeToString(b));

        System.out.println("========k=======k=======k=======k=======k=======k=======k=======k=======k=======k=======k=======k=======k======k");
//        InputStream in  = IOUtils.toInputStream(result, "UTF-8");
//        byte[] a = IOUtils.toByteArray(in);

//        System.out.println(new String(a));
//        System.out.println(Base64.getEncoder().encodeToString(a));

//        System.out.println(in);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Map<String, Object> parse(byte[] object) throws ParseException {
        Map<String, Object> result = new HashMap<>();

        String resultImage = Base64.getEncoder().encodeToString(object);

        System.out.println("Format " + Base64.getEncoder().encodeToString(object));

        result.put("image", resultImage);

        return result;
    }
}
