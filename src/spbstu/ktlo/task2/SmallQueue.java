package spbstu.ktlo.task2;

public class SmallQueue {

    private char[] data;
    private int index;

    public SmallQueue(char[] initial) {
        data = initial;
        index = 0;
    }

    public char next(char newValue) {
        char oldValue = data[index];
        data[index] = newValue;
        index++;
        if (index == data.length)
            index = 0;
        return oldValue;
    }

    public void reorganize() {
        char tmp;
        if (index > 0) {
            for (int i = 0, n = data.length / 2; i < n; i++) {
                tmp = data[i];
                data[i] = data[index];
                data[index] = tmp;
                index++;
                if (index == data.length)
                    index = 0;
            }
            index = 0;
        }
    }

    boolean areAllEquals() {
        char first = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] != first)
                return false;
        }
        return true;
    }

}
