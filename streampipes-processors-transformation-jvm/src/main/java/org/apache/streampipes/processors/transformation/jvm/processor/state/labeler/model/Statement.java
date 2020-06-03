/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.streampipes.processors.transformation.jvm.processor.state.labeler.model;

public class Statement {
    private String operator;
    private double value;
    private String label;

    public Statement() {
    }

    /**
     * This method checks if the user input is correct. When not null is returned
     * @param s
     * @return
     */
    public static Statement getStatement(String s) {
        Statement result = new Statement();

        String[] parts  = s.split(";");
        // default case
        if (parts.length == 2) {
            if (parts[0].equals("*")) {
                result.setOperator(parts[0]);
                result.setLabel(parts[1]);
                return result;
            } else {
                return null;
            }
        }

        // all other valid cases
        if (parts.length ==  3) {

            if (parts[0].equals(">") || parts[0].equals("<") || parts[0].equals("=")) {
                result.setOperator(parts[0]);
            } else {
                return null;
            }

            if (isNumeric(parts[1].replaceAll("-", ""))) {
                result.setValue(Double.parseDouble(parts[1]));
            } else {
                return null;
            }

            result.setLabel(parts[2]);

            return result;
        } else {
            return null;
        }
    }

    private static boolean isNumeric(final String str) {

        // null or empty
        if (str == null || str.length() == 0) {
            return false;
        }

        return str.chars().allMatch(Character::isDigit);

    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
