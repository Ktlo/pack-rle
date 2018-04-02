package spbstu.ktlo.task2;

public class ProgramException extends Exception {

    private int errorCode;

    ProgramException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    int getErrorCode() {
        return errorCode;
    }

}
