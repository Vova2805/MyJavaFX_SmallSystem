package structure;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Quize implements Serializable {
    private LinkedHashMap<String,Level> programmerLevels;

    public Quize() {
        programmerLevels = new LinkedHashMap<>();
    }
    public Quize(Quize quize) {
        programmerLevels = quize.programmerLevels;
    }

    public LinkedHashMap<String, Level> getProgrammerLevels() {
        return programmerLevels;
    }

    public void setProgrammerLevels(LinkedHashMap<String, Level> programmerLevels) {
        this.programmerLevels = programmerLevels;
    }
}
