/* $Revision: 8418 $ $Author: egonw $ $Date: 2007-06-25 22:05:44 +0200 (Mon, 25 Jun 2007) $
 * 
 * Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction.mechanism;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.reaction.ReactionMechanismTest;

/**
 * Tests for RadicalSiteRearrangementMechanism implementations.
 *
 * @cdk.module test-reaction
 */
public class RadicalSiteRearrangementMechanismTest extends ReactionMechanismTest {

	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setMechanism(RadicalSiteRearrangementMechanism.class);
	 }
	 
	/**
	 *  Constructor for the RadicalSiteRearrangementMechanismTest object.
	 */
	public RadicalSiteRearrangementMechanismTest(){
        super();
	}
	
	/**
	 * Junit test.
	 * 
	 * @throws Exception 
	 */
	@Test public void testRadicalSiteRearrangementMechanism(){
		IReactionMechanism mechanism = new RadicalSiteRearrangementMechanism();
		Assert.assertNotNull(mechanism);
	}

	/**
	 * Junit test.
	 * TODDO: REACT: add an example
	 * 
	 * @throws Exception 
	 */
	@Test public void testInitiate_IMoleculeSet_ArrayList_ArrayList(){
		IReactionMechanism mechanism = new RadicalSiteRearrangementMechanism();
		
		Assert.assertNotNull(mechanism);
	}
}
