/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.validate.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Command line utility for checking the chemical information from files.
 *
 * @author     egonw
 * @created    2003-07-14
 *
 * @keyword    command line util
 */
public class Validator {

    private org.openscience.cdk.tools.LoggingTool logger;
    
    public Validator() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName(), true);
    }

    public ValidationReport validate(File input) throws IOException {
        Reader fileReader = new FileReader(input);
        ChemObjectReader reader = new ReaderFactory().createReader(fileReader);
        if (reader == null) {
            System.out.println("Cannot parse file with unknown file type: " + input.toString());
            return new ValidationReport();
        }
        
        // read contents from file
        ChemFile content = null;
        try {
            content = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (CDKException exception) {
            System.out.println("Error while reading file: " + exception.toString());
            return new ValidationReport();
        }
        if (content == null) {
            System.out.println("Cannot read contents from file.");
            return new ValidationReport();
        }
        
        // validate contents
        ValidatorEngine engine = new ValidatorEngine();
        engine.addValidator(new CDKValidator());
        engine.addValidator(new BasicValidator());
        return engine.validateChemFile(content);
    }
    
    public void outputErrors(String filename, ValidationReport report) {
        Enumeration errors = report.getErrors().elements();
        while (errors.hasMoreElements()) {
            ValidationTest test = (ValidationTest)errors.nextElement();
            System.out.println(filename + ": <ERROR> " + test.getError());
            if (test.getDetails().length() > 0) {
                System.out.println("  " + test.getDetails());
            }
        }
        errors = report.getWarnings().elements();
        while (errors.hasMoreElements()) {
            ValidationTest test = (ValidationTest)errors.nextElement();
            System.out.println(filename + ": <WARNING> " + test.getError());
            if (test.getDetails().length() > 0) {
                System.out.println("  " + test.getDetails());
            }
        }
        errors = report.getCDKErrors().elements();
        while (errors.hasMoreElements()) {
            ValidationTest test = (ValidationTest)errors.nextElement();
            System.out.println(filename + ": <CDK ERROR> " + test.getError());
            if (test.getDetails().length() > 0) {
                System.out.println("  " + test.getDetails());
            }
        }
    }
    
    /**
     * Runs the program from the command line.
     *
     * @param  args  command line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("syntax: Validator <file> <file2> ...");
            System.exit(0);
        }
        
        Validator validator = new Validator();
        for (int i=0; i<args.length; i++) {
            String ifilename = args[i];
            try {
                File input = new File(ifilename);
                if (!input.isDirectory()) {
                    ValidationReport report = validator.validate(input);
                    validator.outputErrors(ifilename, report);
                } else {
                    System.out.println("Skipping directory: " + ifilename);
                }
            } catch (FileNotFoundException exception) {
                System.out.println("Skipping file. Cannot find it: " + ifilename);
            } catch (Exception exception) {
                System.err.println(ifilename + ": error=");
                exception.printStackTrace();
            }
        }
    }
    
}

