package org.streampipes.connect.firstconnector.guess;

public class PropertyGuessResults {
    private PropertyGuesses[] result;

    public PropertyGuessResults() {
    }


    public PropertyGuessResults(PropertyGuesses[] result) {
        this.result = result;
    }

    public PropertyGuesses[] getResult() {
        return result;
    }

    public void setResult(PropertyGuesses[] result) {
        this.result = result;
    }
}
