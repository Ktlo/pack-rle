package spbstu.ktlo.task2;

import spbstu.ktlo.task2.cli.*;

import java.io.*;
import java.util.List;

public class ArgumentResolver {

    // Packed or compressed file extension
    private static final String fileExtension = ".rle";

    /* OPTIONS BEGIN */
    @Option(name = 'i', usage = "use standard input stream")
    private boolean useStdIn;

    @Option(name = 'o', usage = "use standard output stream")
    private boolean useStdOut;

    @Option(name = 'u', usage = "unpack compressed file")
    private boolean actionUnpack;

    @Option(name = 'z', usage = "compress file")
    private boolean actionPack;

    @Parameter(name = "out", usage = "specify an output file")
    private String outFile;

    @Parameter(name = "help", usage = "shows usage and exit")
    private boolean showHelp;

    @Arguments
    private List<String> arguments;
    /* OPTIONS END */

    private Main result;

    public ArgumentResolver(String[] args) throws CLIException, IllegalAccessException, ProgramException, FileNotFoundException {
        CLIParser parser = new CLIParser(this);
        parser.parse(args);

        // Show help and exit
        if (showHelp) {
            System.out.println("Usage: [-zuio] [--out file.out] [file.in]");
            System.out.println();
            System.out.println(parser.generateUsage());
            System.exit(0);
        }

        // Prevent unsupported situations
        int argN = arguments.size();
        if (argN > 1)
            throw new ProgramException("Too many input files were specified, 1 expected, got " + argN, 3);
        if (useStdIn && argN == 1)
            throw new ProgramException("Both standard input stream and input file were specified", 4);
        if (!useStdIn && argN == 0)
            throw new ProgramException("No input was specified", 5);
        if (useStdOut && outFile != null)
            throw new ProgramException("Both standard input stream and input file were specified", 6);
        if (actionPack && actionUnpack)
            throw new ProgramException("Both actions pack and unpack were specified", 7);
        if (!actionPack && !actionUnpack)
            throw new ProgramException("No action pack or unpack was specified", 8);

        // Setup the streams and action
        InputStream input;
        OutputStream output;
        Action action;

        if (actionPack)
            action = Action.pack;
        else
            action = Action.unpack;

        String inputFileName = argN == 1 ? arguments.get(0) : null;
        if (useStdIn)
            input = System.in;
        else
            input = new FileInputStream(inputFileName);

        if (useStdOut)
            output = System.out;
        else if (outFile != null)
            output = new FileOutputStream(outFile);
        else {
            // Get the name of output file
            if (useStdIn) {
                // When using stdin
                if (action == Action.pack)
                    output = new FileOutputStream("out" + fileExtension);
                else
                    output = new FileOutputStream("out.txt");
            }
            else {
                // When input filename was specified
                if (action == Action.pack)
                    // When do pack
                    output = new FileOutputStream(inputFileName + fileExtension);
                else {
                    // When do unpack
                    if (inputFileName.endsWith(fileExtension))
                        output = new FileOutputStream(inputFileName.substring(0,
                                inputFileName.length() - fileExtension.length()));
                    else
                        output = new FileOutputStream(inputFileName + ".txt");
                }
            }
        }

        result = new Main(input, output, action);
    }

    public Main getResult() {
        return result;
    }
}
