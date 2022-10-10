package org.spdx.toolsJavaSolver;

import org.apache.commons.cli.*;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.toolsJavaSolver.generationTestCases.GenerationMinimalTestCase;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InvalidSPDXAnalysisException, IOException {
        var options = new Options();
        options.addOption(Option.builder("t").longOpt("test_case").desc("For possible values see some website.").hasArg().argName("TESTCASE").required().build());
        options.addOption(Option.builder("f").longOpt("output").desc("The output file path").hasArg().argName("PATH").required().build());
        options.addOption(Option.builder("h").longOpt("help").desc("Display usage").required(false).build());

        var parser = new DefaultParser();

        try {
            var cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }

            var testCase = cmd.getOptionValue("t");
            String outputPath = cmd.getOptionValue("f");

            SpdxDocument outputDoc;

            switch (testCase) {
                case "generationMinimalTest":
                    outputDoc = GenerationMinimalTestCase.buildDocument();
                    break;
                default:
                    //TODO: add info about supported test cases
                    System.err.print("Error: " + testCase + " is an unrecognized test case. Here is a list of possible test cases: (work in progress)\n");
                    System.exit(1);
                    return;
            }

            var modelStore = (MultiFormatStore) outputDoc.getModelStore();
            modelStore.serialize(outputDoc.getDocumentUri(), new FileOutputStream(outputPath));

        } catch (ParseException e) {
            System.err.println(e.getMessage());

            printUsage(options);
            System.exit(1);
        }
    }

    private static void printUsage(Options options) {
        var helper = new HelpFormatter();
        var helpHeader = "Output a file that solves the specified SPDX-testbed test case.\n\n";
        var helpFooter = "\n";
        helper.printHelp("testbed-solver.jar", helpHeader, options, helpFooter, true);
    }
}