/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fzi.cep.sepa.actions.samples.number;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;
import java.io.IOException;
import java.util.Arrays;
import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;
import org.rendersnake.HtmlCanvas;

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
                                .div(id("container").style("min-width: 200px; min-height: 200px; margin: 0 auto; position: relative;"))
                                ._div();
                        return getStandardizedFooter(canvas);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return canvas;
        }

}
