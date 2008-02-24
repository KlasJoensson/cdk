/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.tools.manipulator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @cdk.module test-standard
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-07-23
 */
public class ReactionManipulatorTest extends NewCDKTestCase {

	private IReaction reaction;
	
	public ReactionManipulatorTest() {
		super();
	}

    @Before
    public void setUp() throws Exception {
		String filename1 = "data/mdl/reaction-1.rxn";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        ReactionSet set = (ReactionSet)reader1.read(new ReactionSet());
        reaction = set.getReaction(0);
        reader1.close();
    }



    @Test public void testReverse_IReaction() {
        Reaction reaction = new Reaction();
        reaction.setDirection(Reaction.BACKWARD);
        Molecule water = new Molecule();
        reaction.addReactant(water, 3.0);
        reaction.addReactant(new Molecule());
        reaction.addProduct(new Molecule());
        
        Reaction reversedReaction = (Reaction)ReactionManipulator.reverse(reaction);
        Assert.assertEquals(Reaction.FORWARD, reversedReaction.getDirection());
        Assert.assertEquals(2, reversedReaction.getProductCount());
        Assert.assertEquals(1, reversedReaction.getReactantCount());
        Assert.assertEquals(3.0, reversedReaction.getProductCoefficient(water), 0.00001);
    }
    
    @Test public void testGetAllIDs_IReaction() {
        Reaction reaction = new Reaction();
        reaction.setID("r1");
        Molecule water = new Molecule();
        water.setID("m1");
        Atom oxygen = new Atom("O");
        oxygen.setID("a1");
        water.addAtom(oxygen);
        reaction.addReactant(water);
        reaction.addProduct(water);
        
        Vector ids = ReactionManipulator.getAllIDs(reaction);
        Assert.assertNotNull(ids);
        Assert.assertEquals(5, ids.size());
    }
    /**
	 * A unit test suite for JUnit. Test of mapped IAtoms
	 *
	 * @return    The test suite
	 */
    @Test public void testGetMappedChemObject_IReaction_IAtom() throws Exception {
    	IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
    	IMolecule reactant = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[C+]-C=C");
    	IMolecule product = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=C=C");
    	
    	IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(0),product.getAtom(0));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(1),product.getAtom(1));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(2),product.getAtom(2));
        reaction.addMapping(mapping);
    	
        reaction.addReactant(reactant);
        reaction.addProduct(product);
        
        IAtom mappedAtom = (IAtom)ReactionManipulator.getMappedChemObject(reaction, reactant.getAtom(0));
        Assert.assertEquals(mappedAtom, product.getAtom(0));
        
        mappedAtom = (IAtom)ReactionManipulator.getMappedChemObject(reaction, product.getAtom(1));
        Assert.assertEquals(mappedAtom, reactant.getAtom(1));
        
        
    }
    /**
	 * A unit test suite for JUnit. Test of mapped IBond
	 *
	 * @return    The test suite
	 */
    @Test public void testGetMappedChemObject_IReaction_IBond()throws ClassNotFoundException, CDKException, java.lang.Exception {
    	IReaction reaction = DefaultChemObjectBuilder.getInstance().newReaction();
    	IMolecule reactant = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[C+]-C=C");
    	IMolecule product = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=C=C");
    	
    	IMapping mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getAtom(0),product.getAtom(0));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getBond(0),product.getBond(0));
        reaction.addMapping(mapping);
        mapping = DefaultChemObjectBuilder.getInstance().newMapping(reactant.getBond(1),product.getBond(1));
        reaction.addMapping(mapping);
    	
        reaction.addReactant(reactant);
        reaction.addProduct(product);
        
        IBond mappedBond = (IBond)ReactionManipulator.getMappedChemObject(reaction, reactant.getBond(0));
        Assert.assertEquals(mappedBond, product.getBond(0));
        
        mappedBond = (IBond)ReactionManipulator.getMappedChemObject(reaction, product.getBond(1));
        Assert.assertEquals(mappedBond, reactant.getBond(1));
    }
    
	@Test public void testGetAtomCount_IReaction() throws Exception {
        Assert.assertEquals(19, ReactionManipulator.getAtomCount(reaction));
	}
	
	@Test public void testGetBondCount_IReaction() throws Exception {
        Assert.assertEquals(18, ReactionManipulator.getBondCount(reaction));
	}
	
	@Test public void testGetAllAtomContainers_IReaction() throws Exception {
		Assert.assertEquals(3, ReactionManipulator.getAllAtomContainers(reaction).size());
	}	
    
	@Test public void testSetAtomProperties_IReactionSet_Object_Object() throws Exception {
		ReactionManipulator.setAtomProperties(reaction, "test", "ok");
		Iterator atomContainers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			Iterator atoms = container.atoms();
			while (atoms.hasNext()) {
				IAtom atom = (IAtom)atoms.next();
				Assert.assertNotNull(atom.getProperty("test"));
				Assert.assertEquals("ok", atom.getProperty("test"));
			}
		}
	}

	@Test public void testGetAllChemObjects_IReactionSet() {
		List allObjects = ReactionManipulator.getAllChemObjects(reaction);
		// does not recurse beyond the IAtomContainer, so:
		// reaction, 2xreactant, 1xproduct
		Assert.assertEquals(4, allObjects.size());
	}

	@Test public void testGetRelevantAtomContainer_IReaction_IAtom() {
		Iterator atomContainers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			IAtom anAtom = container.getAtom(0);
			Assert.assertEquals(
				container, 
				ReactionManipulator.getRelevantAtomContainer(reaction, anAtom)
			);
		}
	}
	
	@Test
    public void testGetRelevantAtomContainer_IReaction_IBond() {
		Iterator atomContainers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
		while (atomContainers.hasNext()) {
			IAtomContainer container = (IAtomContainer)atomContainers.next();
			IBond aBond = container.getBond(0);
			Assert.assertEquals(
				container, 
				ReactionManipulator.getRelevantAtomContainer(reaction, aBond)
			);
		}
	}

}
