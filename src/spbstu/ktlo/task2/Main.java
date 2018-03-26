package spbstu.ktlo.task2;

import com.sun.xml.internal.stream.writers.UTF8OutputStreamWriter;
import jdk.internal.util.xml.impl.ReaderUTF8;

import java.io.*;

public class Main {

    public static final int DO_PACK = 0b01;
    public static final int DO_UNPACK = 0b10;
    private static final String fileExtension = ".rle";

    private InputStream in;
    private OutputStream out;
    private int action;

    public static void main(String[] args) {
        Main task = new Main(args);
        task.doAction();
    }

    private static void exitFailure(String message){
        System.err.println(message);
        System.exit(message.hashCode());
    }

    public Main(InputStream input, OutputStream output, int action) {
        in = input;
        out = output;
        this.action = action;
    }

    public Main(String[] args) {
        if (args.length < 1)
            exitFailure("Insufficient argument list");

        ProgramParameters parameters = new ProgramParameters(args[0]);
        InputStream input = null;
        OutputStream output = null;

        // Pipe params
        if (parameters.has('o'))
            output = System.out;
        if (parameters.has('i'))
            input = System.in;
        // Action params
        if (parameters.has('z'))
            action |= DO_PACK;
        if (parameters.has('u'))
            action |= DO_UNPACK;

        // Other output file param
        int offset;
        if (args.length > 1 && "-out".equals(args[1])) {
            if (2 >= args.length)
                exitFailure("No output file was specified to -out parameter");
            if (output != null)
                exitFailure("Several outputs were specified (-o and -out)");
            try {
                output = new FileOutputStream(args[2]);
            }
            catch (Exception e) {
                exitFailure(e.getLocalizedMessage());
            }
            offset = 2;
        }
        else
            offset = 0;

        // Input filename
        if (2 + offset <= args.length) {
            if (input != null)
                exitFailure("Several inputs were specified (-i and filename)");
            try {
                input = new FileInputStream(args[1 + offset]);
            }
            catch (Exception e) {
                exitFailure(e.getLocalizedMessage());
            }
        }

        // If no input has specified
        if (input == null)
            exitFailure("No input file or stream");

        // If no output has specified
        if (output == null) {
            String filename;
            if (args.length == 1) {
                if (action == DO_PACK)
                    filename = "out.txt" + fileExtension;
                else
                    filename = "out.txt";
            }
            else if (action == DO_PACK)
                filename = args[1] + fileExtension;
            else {
                filename = args[1];
                int length = filename.length();
                if (length >=4 &&
                        filename.substring(length - fileExtension.length()).toLowerCase().equals(fileExtension)) {
                    filename = filename.substring(0, length - fileExtension.length());
                }
                else
                    exitFailure("Wrong input file extension (RLE expected)");
            }

            try {
                output = new FileOutputStream(filename);
            }
            catch (Exception e) {
                exitFailure(e.getLocalizedMessage());
            }
        }

        // Initialize streams
        in = input;
        out = output;
    }

    private void doAction() {
        try {
            switch (action) {
                case DO_PACK:
                    pack();
                    return;
                case DO_UNPACK:
                    unpack();
                    return;
                default:
                    exitFailure("Both actions pack and unpack were specified");
            }
        }
        catch (Exception e) {
            exitFailure(e.getLocalizedMessage());
        }
    }

    public void pack() throws IOException {
        BufferedReader input = new BufferedReader(new ReaderUTF8(in));
        BufferedWriter output = new BufferedWriter(new UTF8OutputStreamWriter(out));
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
        BufferedReader input = new BufferedReader(new ReaderUTF8(in));
        BufferedWriter output = new BufferedWriter(new UTF8OutputStreamWriter(out));
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
            exitFailure(e.getLocalizedMessage());
        }
        finally {
            try {
                input.close();
                output.close();
            }
            catch (Exception e) {
                exitFailure(e.getLocalizedMessage());
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
