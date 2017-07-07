/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fzi.cep.sepa.actions.samples.number;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

/**
 *
 * @author eberle
 */
public class NumberParameters extends ActionParameters{
    
    private String propertyName;
    private String colorValue;
    
    public NumberParameters(String topic, String url, String propertyName, String colorValue){
        super(topic, url);
        this.propertyName = propertyName;
        this.colorValue = colorValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getColorValue() {
        return colorValue;
    }
    
    
}
