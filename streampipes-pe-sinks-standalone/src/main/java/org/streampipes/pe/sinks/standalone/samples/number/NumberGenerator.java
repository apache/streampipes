/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.streampipes.pe.sinks.standalone.samples.number;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import org.streampipes.pe.sinks.standalone.samples.HtmlGenerator;

/**
 *
 * @author eberle
 */
public class NumberGenerator extends HtmlGenerator<NumberParameters> {

        public NumberGenerator(NumberParameters actionParameters) {
                super(actionParameters);
        }

        @Override
        protected HtmlCanvas buildHtmlCanvas() {
                HtmlCanvas canvas = new HtmlCanvas();
                try {
                        canvas = getStandardizedHeader(canvas, Arrays.asList("stomp.js", "number.js"), Arrays.asList());
                        canvas.body(onLoad(
                                "buildTable('"
                                + actionParameters.getUrl()
                                + "', '"
                                + actionParameters.getTopic()
                                + "', '"
                                + actionParameters.getColorValue()
                                + "', '"
                                + actionParameters.getPropertyName()
                                + "')")
                                .style("btn btn-danger"))
                                .div(id("container").style("min-width: 200px; height: 200px; margin: 0 auto; position: relative;"))
                                ._div();
                        return getStandardizedFooter(canvas);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return canvas;
        }

}
