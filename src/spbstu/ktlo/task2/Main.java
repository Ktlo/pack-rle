package spbstu.ktlo.task2;

import spbstu.ktlo.task2.cli.CLIException;

import java.io.*;

public class Main {

    private InputStream in;
    private OutputStream out;
    private Action action;

    public static void main(String[] args) {
        ArgumentResolver resolver;
        try {
            resolver = new ArgumentResolver(args);
        } catch (CLIException e) {
            exitFailure(e.getMessage(), 2);
            return;
        } catch (IllegalAccessException e) {
            // This is impossible to happen
            exitFailure(e.getLocalizedMessage(), -127);
            return;
        } catch (ProgramException e) {
            exitFailure(e.getMessage(), e.getErrorCode());
            return;
        } catch (FileNotFoundException e) {
            exitFailure(e.getMessage(), 1);
            return;
        }
        Main task = resolver.getResult();
        task.doAction();
    }

    private static void exitFailure(String message, int code){
        System.err.println(message);
        System.exit(code);
    }

    public Main(InputStream input, OutputStream output, Action action) {
        in = input;
        out = output;
        this.action = action;
    }

    private void doAction() {
        try {
            switch (action) {
                case pack:
                    pack();
                    break;
                case unpack:
                    unpack();
                    break;
            }
        }
        catch (Exception e) {
            exitFailure(e.getLocalizedMessage(), -11);
        }
    }

    public void pack() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(out));
        char[] buffer = new char[4];
        int result = input.read(buffer);
        if (result < 0) {
            input.close();
            output.close();
            return;
        }
        if (result < 4) {
            output.write(buffer, 0 , result);
            input.close();
            output.close();
            return;
        }
        SmallQueue queue = new SmallQueue(buffer);
        while (true) {
            if (queue.areAllEquals()) {
                int sum = 4;
                char repeatedValue = buffer[0];
                int tmp = -1;
                for (; sum < Character.MAX_VALUE; sum++) {
                    tmp = input.read();
                    if (tmp != repeatedValue)
                        break;
                }
                char[] anotherBuffer = new char[] { 0, (char) sum, repeatedValue };
                output.write(anotherBuffer);
                if (tmp < 0) {
                    input.close();
                    output.close();
                    return;
                }
                result = input.read(anotherBuffer);
                if (result < 0) {
                    input.close();
                    output.close();
                    return;
                }
                if (result < 3) {
                    output.write(tmp);
                    output.write(anotherBuffer, 0, result);
                    input.close();
                    output.close();
                    return;
                }
                queue.next((char) tmp);
                queue.next(anotherBuffer[0]);
                queue.next(anotherBuffer[1]);
                queue.next(anotherBuffer[2]);
            }
            else {
                int tmp = input.read();
                if (tmp < 0)
                    break;
                output.write(queue.next((char) tmp));
            }
        }
        queue.reorganize();
        output.write(buffer);
        output.close();
        input.close();
    }

    public void unpack() {
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(out));

        int symbol;
        try {
            while ((symbol = input.read()) >= 0) {
                if (symbol == 0) {
                    int length = input.read();
                    int character = input.read();
                    if (length < 0 || character < 0)
                        throw new Exception("Invalid file format");
                    for (int i = 0; i < length; i++)
                        output.write(character);
                }
                else
                    output.write(symbol);
            }
        }
        catch (Exception e) {
            exitFailure(e.getLocalizedMessage(), -12);
        }
        finally {
            try {
                input.close();
                output.close();
            }
            catch (Exception e) {
                exitFailure(e.getLocalizedMessage(), -13);
            }
        }
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public InputStream getInputStream() {
        return in;
    }
}
