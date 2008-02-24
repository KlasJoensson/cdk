package org.openscience.cdk.test.qsar.descriptors.molecular;

import java.io.InputStream;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.descriptors.molecular.MDEDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs a test for the MDEDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */

public class MDEDescriptorTest extends MolecularDescriptorTest {

    public MDEDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(MDEDescriptorTest.class);
    }

    public void setUp() throws Exception {
    	setDescriptor(MDEDescriptor.class);
    }

    public void testMDE1() throws ClassNotFoundException, CDKException, Exception {
        String filename = "data/mdl/mdeotest.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);

        DoubleArrayResult result = (DoubleArrayResult) descriptor.calculate(ac).getValue();

        for (int i = 0; i < 19; i++) System.out.println(result.get(i));
        
        assertEquals(0.0000, result.get(MDEDescriptor.mdeo11), 0.0001);
        assertEquals(1.1547, result.get(MDEDescriptor.mdeo12), 0.0001);
        assertEquals(2.9416, result.get(MDEDescriptor.mdeo22), 0.0001);
    }
}