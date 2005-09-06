/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  Connectivity index (order 0):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned values are:
 *  chi0 is the Atomic connectivity index (order 0),
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:chi0
 */
public class ConnectivityOrderZeroDescriptor implements Descriptor {

	/**
	 *  Constructor for the ConnectivityOrderZeroDescriptor object
	 */
	public ConnectivityOrderZeroDescriptor() { }


	/**
	 *  Gets the specification attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:chi0",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		return (null);
		// no parameters to return
	}


	/**
	 *  Description of the Method
	 *
	 *@param  atomContainer                AtomContainer
	 *@return                   Atomic connectivity index (order 0)
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer atomContainer) throws CDKException {
		double chi0 = 0;
		org.openscience.cdk.interfaces.Atom[] atoms = atomContainer.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			int atomDegree = 0;
			org.openscience.cdk.interfaces.Atom[] neighboors = atomContainer.getConnectedAtoms(atoms[i]);
			for (int a = 0; a < neighboors.length; a++) {
				if (!neighboors[a].getSymbol().equals("H")) {
					atomDegree += 1;
				}
			}
			if(atomDegree > 0) {
				chi0 += 1/(Math.sqrt(atomDegree));
			}
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(chi0));
	}


	/**
	 *  Gets the parameterNames attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

