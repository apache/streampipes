/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fzi.cep.sepa.actions.samples.verticalbar;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

/**
 *
 * @author eberle
 */
class VerticalBarParameters extends ActionParameters {

        private int min;
        private int max;
        private String propertyName;
        private String colorValue;

        public VerticalBarParameters(String topic, String url, int min, int max, String propertyName, String colorValue) {
                super(topic, url);
                this.min = min;
                this.max = max;
                this.propertyName = propertyName;
                this.colorValue = colorValue;
        }

        public int getMin() {
                return min;
        }

        public int getMax() {
                return max;
        }

        public String getPropertyName() {
                return propertyName;
        }

        public String getColorValue() {
                return colorValue;
        }

}
