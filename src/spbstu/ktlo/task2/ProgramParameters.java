package spbstu.ktlo.task2;

import java.util.Set;

public class ProgramParameters {

    private char[] parameters;

    public ProgramParameters(String parameters) {
        this.parameters = parameters.toCharArray();
    }

    public boolean has(char parameter) {
        for (char p : parameters) {
            if (p == parameter)
                return true;
        }
        return false;
    }

}
