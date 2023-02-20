package org.apache.streampipes.processors.imageprocessing.jvm.processor.commons;

public enum ImagePropertyConstants {
    BOX_WIDTH(Constants.boxWidth),
    BOX_HEIGHT(Constants.boxHeight),
    BOX_X(Constants.boxX),
    BOX_Y(Constants.boxY),
    SCORE(Constants.score),
    CLASS_INDEX(Constants.classesindex),
    IMAGE(Constants.image),
    CLASS_NAME(Constants.classname),
    TIMESTAMP(Constants.timestamp),
    IMAGE_MAPPING(Constants.imageMapping);


    private String property;

    ImagePropertyConstants(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    private static final class Constants {
        private static final String boxWidth = "box_width";
        private static final String boxHeight = "box_height";
        private static final String boxX = "box_x";
        private static final String boxY = "box_y";
        private static final String score = "score";
        private static final String classesindex = "classesindex";
        private static final String image = "image";
        private static final String classname = "classname";
        private static final String timestamp = "timestamp";
        private static final String imageMapping = "image-mapping";

    }
}
