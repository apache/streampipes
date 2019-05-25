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

import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.grounding.FormatDescription;

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

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Map<String, Object> parse(byte[] object) throws ParseException {
        Map<String, Object> result = new HashMap<>();

        String resultImage = Base64.getEncoder().encodeToString(object);

        result.put("image", resultImage);

        return result;
    }
}
