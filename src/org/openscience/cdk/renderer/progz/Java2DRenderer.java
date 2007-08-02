package org.openscience.cdk.renderer.progz;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.IRenderer2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Ellipse2D;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.renderer.progz.GeometryToolsInternalCoordinates;
import org.openscience.cdk.ringsearch.SSSRFinder;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.validate.ProblemMarker;


public class Java2DRenderer implements IJava2DRenderer {

	private Renderer2DModel rendererModel;
	private AffineTransform affine;

	protected LoggingTool logger;


	public Java2DRenderer(Renderer2DModel model) {
		this.rendererModel = model;
		logger = new LoggingTool(this);
	}
	
	public void paintChemModel(IChemModel model, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintMoleculeSet(IMoleculeSet moleculeSet, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintReaction(IReaction reaction, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintReactionSet(IReactionSet reactionSet, Graphics2D graphics) {
		// TODO Auto-generated method stub

	}

	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics) {
		// TODO Auto-generated method stub
		
	}

	public void paintMolecule(IAtomContainer atomCon, Graphics2D graphics,
			Rectangle2D bounds) {
		List shapes = new ArrayList();
		// create the bond shapes
		Iterator bonds = atomCon.bonds();
		while (bonds.hasNext()) {
			IBond bond = (IBond)bonds.next();
			shapes.add(
				new Line2D.Double(
					bond.getAtom(0).getPoint2d().x,
					bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x,
					bond.getAtom(1).getPoint2d().y
				)
			);
		}
		//rendererModel.setShowAromaticity(true);
		
		
		
		// calculate the molecule boundaries via the shapes
		Rectangle2D molBounds = createRectangle2D(shapes);
		if (molBounds == null) {
			molBounds = new Rectangle2D.Double();
				
			IAtom atom = atomCon.getAtom(0);
			double x = atom.getPoint2d().x;
			double y = atom.getPoint2d().y;
			
			molBounds.setRect(x - 1, y - 1, 2, 2);
		}
		AffineTransform transformMatrix = createScaleTransform(molBounds,bounds);
		affine = transformMatrix;
		graphics.transform(transformMatrix);
		System.out.println("transform matrix:" + graphics.getTransform());

		// draw the shapes
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(
			(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()),
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
		);
/*		for (Iterator iter = shapes.iterator(); iter.hasNext();) {
			graphics.draw((Shape)iter.next());
		}*/
		IRingSet ringSet = getRingSet(atomCon);
		
		paintBonds(atomCon, ringSet, graphics);

		// add rendering of atom symbols?
		paintAtoms(atomCon, graphics);
		
	}
	protected IRingSet getRingSet(IAtomContainer atomContainer)
	{
	  IRingSet ringSet = atomContainer.getBuilder().newRingSet();
	  java.util.Iterator molecules = null;

	  try
	  {
	    molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).molecules();
	  }

	  catch (Exception exception)
	  {
	    logger.warn("Could not partition molecule: ", exception.getMessage());
	    logger.debug(exception);
	    return ringSet;
	  }

	  while (molecules.hasNext())
	  {
	    SSSRFinder sssrf = new SSSRFinder((IMolecule)molecules.next());

	    ringSet.add(sssrf.findSSSR());
	  }

	  return ringSet;
	}
	/**
	 *  Searches through all the atoms in the given array of atoms, triggers the
	 *  paintColouredAtoms method if the atom has got a certain color and triggers
	 *  the paintAtomSymbol method if the symbol of the atom is not C.
	 */
	public void paintAtoms(IAtomContainer atomCon, Graphics2D graphics)
	{
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			paintAtom(atomCon, atomCon.getAtom(i), graphics);
		}
	}
	public void paintAtom(IAtomContainer container, IAtom atom, Graphics2D graphics)
	{
		//System.out.println("IAtom Symbol:" + atom.getSymbol() + " atom:" + atom);
		Font font;
		font = new Font("Serif", Font.PLAIN, 20);
		
		float fscale = 25;
		float[] transmatrix = { 1f / fscale, 0f, 0f, -1f / fscale};
		AffineTransform trans = new AffineTransform(transmatrix);
		font = font.deriveFont(trans);
		
		graphics.setFont(font);
		String symbol = "";
		if (atom.getSymbol() != null) {
			symbol = atom.getSymbol();
		}
		//symbol = "L"; //to test if a certain symbol is spaced out right 
		
		boolean drawSymbol = false; //paint all Atoms for the time being
		boolean isRadical = (container.getConnectedSingleElectronsCount(atom) > 0);
		if (atom instanceof IPseudoAtom)
		{
			drawSymbol = false;
//			if (atom instanceof FragmentAtom) {
//				paintFragmentAtom((FragmentAtom)atom, atomBackColor, graphics,
//						alignment, isRadical);
//			} else {
			//	paintPseudoAtomLabel((IPseudoAtom) atom, atomBackColor, graphics, alignment, isRadical);
//			}
			return;
		} else if (!atom.getSymbol().equals("C"))
		{
			/*
			 *  only show element for non-carbon atoms,
			 *  unless (see below)...
			 */
			drawSymbol = true;
		} else if (getRenderer2DModel().getKekuleStructure())
		{
			// ... unless carbon must be drawn because in Kekule mode
			drawSymbol = true;
		} else if (atom.getFormalCharge() != 0)
		{
			// ... unless carbon is charged
			drawSymbol = true;
		} else if (container.getConnectedBondsList(atom).size() < 1)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (getRenderer2DModel().getShowEndCarbons() && (container.getConnectedBondsList(atom).size() == 1))
		{
			drawSymbol = true;
		} else if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null)
		{
			// ... unless carbon is unbonded
			drawSymbol = true;
		} else if (atom.getMassNumber() != 0)
		{
			try
			{
				if (atom.getMassNumber() != IsotopeFactory.getInstance(container.getBuilder()).
						getMajorIsotope(atom.getSymbol()).getMassNumber())
				{
					drawSymbol = true;
				}
			} catch (Exception exception) {
                logger.debug("Could not get an instance of IsotopeFactory");
            }

		}

		if (drawSymbol != true)
			return;
		
		FontRenderContext frc = graphics.getFontRenderContext();
		TextLayout layout = new TextLayout(symbol, font, frc);
		Rectangle2D bounds = layout.getBounds();
		
		float margin = 0.03f; 
		float screenX = (float)(atom.getPoint2d().x - bounds.getWidth()/2);
		float screenY = (float)(atom.getPoint2d().y - bounds.getHeight()/2);

		bounds.setRect(bounds.getX() + screenX - margin,
		                  bounds.getY() + screenY - margin,
		                  bounds.getWidth() + 2 * margin,
		                  bounds.getHeight() + 2 * margin);
		
		Color atomColor = getRenderer2DModel().getAtomColor(atom, Color.BLACK);
		Color saveColor = graphics.getColor();
		Color bgColor = graphics.getBackground();
		graphics.setColor(bgColor);
		graphics.fill(bounds);
		
		graphics.setColor(atomColor);
		layout.draw(graphics, screenX, screenY);

		graphics.setColor(saveColor);
	}
	/**
	 *  Paints a rectangle of the given color at the position of the given atom.
	 *  For example when the atom is highlighted.
	 *
	 *@param  atom      The atom to be drawn
	 *@param  color     The color of the atom to be drawn
	 */
	public void paintColouredAtomBackground(org.openscience.cdk.interfaces.IAtom atom, Color color, Graphics2D graphics)
	{
		double x = atom.getPoint2d().x;
		double y = atom.getPoint2d().y;
		System.out.println("painting paintColouredAtomBackground now at " + x + " / " + y);
		//FIXME: right size for this AtomRadius (currently estimate)
		double atomRadius = 0.8;
		
		graphics.setColor(color);
	
		Rectangle2D shape = new Rectangle2D.Double();
		shape.setFrame(x - (atomRadius / 2), y - (atomRadius / 2), atomRadius, atomRadius);
		if(rendererModel.getIsCompact())
			graphics.draw(shape);
		else
			graphics.fill(shape);
	}
	/**
	 *  A ring is defined aromatic if all atoms are aromatic, -or- all bonds are
	 *  aromatic.
	 *  copied from AbstractRenderer2D
	 */
	public boolean ringIsAromatic(IRing ring)
	{
		boolean isAromatic = true;
		for (int i = 0; i < ring.getAtomCount(); i++)
		{
			if (!ring.getAtom(i).getFlag(CDKConstants.ISAROMATIC))
			{
				isAromatic = false;
			}
		}
		if (!isAromatic)
		{
			isAromatic = true;
			Iterator bonds = ring.bonds();
			while (bonds.hasNext())
				if (!((IBond)bonds.next()).getFlag(CDKConstants.ISAROMATIC))
					return false;
		}
		return isAromatic;
	}
	public static double distance2points(Point2d a, Point2d b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	/**
	 *  Paints the inner bond of a double bond that is part of a ring.
	 *
	 *@param  bond       The bond to be drawn
	 *@param  ring       The ring the bond is part of
	 *@param  bondColor  Color of the bond
	 */
	public void paintInnerBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryToolsInternalCoordinates.get2DCenter(ring);
		System.out.println("  paintInnerBond (=working) now at " + center);
		//next few lines draw a green and pink line just for debugging, to be removed later
		graphics.setColor(Color.green);
		Line2D line = new Line2D.Double(
				bond.getAtom(0).getPoint2d().x,				bond.getAtom(0).getPoint2d().y,
				center.x,				center.y						);
		graphics.draw(line);
		Line2D line2 = new Line2D.Double(
				bond.getAtom(1).getPoint2d().x,				bond.getAtom(1).getPoint2d().y,
				center.x,				center.y			);
		graphics.draw(line2);
		Point2d a = bond.getAtom(0).getPoint2d();
		Point2d b = bond.getAtom(1).getPoint2d();
	
		//TODO: put distanceconstant in the renderermodel
		double distanceconstant = 0.15; //distance between inner and outerbond (in world coordinates)

		double distance = distance2points(a,b);
		double u = ((center.x - a.x)*(b.x - a.x) + (center.y - a.y)*(b.y - a.y)) / (Math.pow(distance, 2));
		double px = a.x + u*(b.x - a.x);
		double py = a.y + u*(b.y - a.y);
		System.out.println("distancea and b: " + distance + " u: " + u + " px: " + px + " py " + py);
		graphics.setColor(Color.pink);
		Point2d z = new Point2d(px, py);
		Line2D linepink = new Line2D.Double(
				z.x,
				z.y,
				center.x,
				center.y
			);
		graphics.draw(linepink);
		graphics.setColor(bondColor);

		double ae = distance2points(a ,z) / distance2points(center, z) * distanceconstant;
		double af = Math.sqrt(Math.pow(ae,2) + Math.pow(distanceconstant,2));
		
		double pfx = a.x + af*(center.x - a.x);
		double pfy = a.y + af*(center.y - a.y);
		
		
		double bh = distance2points(b, z) / distance2points(center, z) * distanceconstant;
		double bi = Math.sqrt(Math.pow(bh, 2) + Math.pow(distanceconstant,2));
		
		double pix = b.x + bi*(center.x - b.x);
		double piy = b.y + bi*(center.y - b.y);

		graphics.setColor(Color.BLACK);
		Line2D linegood = new Line2D.Double(
				pfx,
				pfy,
				pix,
				piy
			);
		graphics.draw(linegood);
		graphics.setColor(bondColor);
		
	}

	/**
	 *  Triggers the suitable method to paint each of the given bonds and selects
	 *  the right color.
	 *
	 *@param  ringSet   The set of rings the molecule contains
	 */
	public void paintBonds(IAtomContainer atomCon, IRingSet ringSet, Graphics2D graphics)
	{
		Color bondColor;
		IRing ring;
		Iterator bonds = atomCon.bonds();
		ArrayList painted_rings = new ArrayList();

		logger.debug("Painting bonds...");
		System.out.println("--doing paintBonds now");
		while (bonds.hasNext())
		{
			IBond currentBond = (IBond)bonds.next();
			
			bondColor = (Color) rendererModel.getColorHash().get(currentBond);
			if (bondColor == null)
			{
				bondColor = rendererModel.getForeColor();
			}
			if (currentBond == rendererModel.getHighlightedBond() && 
					(rendererModel.getSelectedPart()==null || !rendererModel.getSelectedPart().contains(currentBond)))
			{
				bondColor = rendererModel.getHoverOverColor();
				for (int j = 0; j < currentBond.getAtomCount(); j++)
				{
					paintColouredAtomBackground(currentBond.getAtom(j), bondColor, graphics);
					
				}
			}
			ring = RingSetManipulator.getHeaviestRing(ringSet, currentBond);
			if (ring != null)
			{
				System.out.println("found a ring, ringIsAromatic(ring) " + ringIsAromatic(ring) + " getShowAromaticity: "
						+ rendererModel.getShowAromaticity());

				logger.debug("Found ring to draw");
				if (ringIsAromatic(ring) && rendererModel.getShowAromaticity())
				{
					logger.debug("Ring is aromatic");
					if (!painted_rings.contains(ring))
					{
						paintRingRing(ring, bondColor, graphics);
						painted_rings.add(ring);
					}
					paintSingleBond(currentBond, bondColor, graphics);
				} else
				{
					logger.debug("Ring is *not* aromatic");
					paintRingBond(currentBond, ring, bondColor, graphics);
				}
			} else
			{
				System.out.println("no ring found!");

				logger.debug("Drawing a non-ring bond");
				paintBond(currentBond, bondColor, graphics);
			}
		}
	}
	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond that
	 *  is part of a ring with CDK's grey inner bonds.
	 *
	 *@param  bond       The Bond to be drawn.
	 */
	public void paintRingBond(org.openscience.cdk.interfaces.IBond bond, IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryToolsInternalCoordinates.get2DCenter(ring);
		System.out.println(" painting paintRingBond now at " + center + " bond: " + bond);


		if (bond.getOrder() == 1.0)
		{
			// Added by rstefani (in fact, code copied from paintBond)
			if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
			{
				// Draw stero information if available
				if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
				{
					paintWedgeBond(bond, bondColor, graphics);
				} else
				{
					paintDashedWedgeBond(bond, bondColor, graphics);
				}
			} else
			{
				// end code by rstefani
				System.out.println("  singlebond in ring");
				paintSingleBond(bond, bondColor, graphics);
			}
		} else if (bond.getOrder() == 2.0)
		{
			
			paintSingleBond(bond, bondColor, graphics);
			paintInnerBond(bond, ring, bondColor, graphics);
		} else if (bond.getOrder() == 3.0)
		{
			paintTripleBond(bond, bondColor, graphics);
		} else
		{
			logger.warn("Drawing bond as single even though it has order: ", bond.getOrder());
			System.out.println("Drawing bond as single even though it has order: " + bond.getOrder());

			paintSingleBond(bond, bondColor, graphics);
		}
	}
	/**
	 *  Paints the given bond as a wedge bond.
	 *
	 *@param  bond       The singlebond to be drawn
	 *@param  bondColor  Color of the bond
	 */
	public void paintWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("painting paintWedgeBond now for: " + bond);
		//TODO: rewrite this old code:
		/*double wedgeWidth = r2dm.getBondWidth() * 2.0;
		// this value should be made customazible

		int[] coords = GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates());
		int[] screenCoords = getScreenCoordinates(coords);
		graphics.setColor(bondColor);
		int[] newCoords = GeometryTools.distanceCalculator(coords, wedgeWidth);
		int[] newScreenCoords = getScreenCoordinates(newCoords);
		if (bond.getStereo() == CDKConstants.STEREO_BOND_UP)
		{
			int[] xCoords = {screenCoords[0], newScreenCoords[6], newScreenCoords[4]};
			int[] yCoords = {screenCoords[1], newScreenCoords[7], newScreenCoords[5]};
			graphics.fillPolygon(xCoords, yCoords, 3);
		} else
		{
			int[] xCoords = {screenCoords[2], newScreenCoords[0], newScreenCoords[2]};
			int[] yCoords = {screenCoords[3], newScreenCoords[1], newScreenCoords[3]};
			graphics.fillPolygon(xCoords, yCoords, 3);
		}*/
	}


	/**
	 *  Paints the given bond as a dashed wedge bond.
	 *
	 *@param  bond       The single bond to be drawn
	 *@param  bondColor  Color of the bond
	 */
	public void paintDashedWedgeBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("painting paintDashedWedgeBond now for: " + bond);
		//TODO: rewrite this old code:
/*	graphics.setColor(bondColor);

		double bondLength = GeometryTools.getLength2D(bond, r2dm.getRenderingCoordinates());
		int numberOfLines = (int) (bondLength / 4.0);
		// this value should be made customizable
		double wedgeWidth = r2dm.getBondWidth() * 2.0;
		// this value should be made customazible

		double widthStep = wedgeWidth / (double) numberOfLines;
		Point2d point1 = r2dm.getRenderingCoordinate(bond.getAtom(0));
		Point2d point2 = r2dm.getRenderingCoordinate(bond.getAtom(1));
		if (bond.getStereo() == CDKConstants.STEREO_BOND_DOWN_INV)
		{
			// draw the wedge bond the other way around
			point1 = r2dm.getRenderingCoordinate(bond.getAtom(1));
			point2 = r2dm.getRenderingCoordinate(bond.getAtom(0));
		}
		Vector2d lengthStep = new Vector2d(point2);
		lengthStep.sub(point1);
		lengthStep.scale(1.0 / numberOfLines);
		Vector2d vector2d = GeometryToolsInternalCoordinates.calculatePerpendicularUnitVector(point1, point2);

		Point2d currentPoint = new Point2d(point1);
		Point2d q1 = new Point2d();
		Point2d q2 = new Point2d();
		for (int i = 0; i <= numberOfLines; ++i)
		{
			Vector2d offset = new Vector2d(vector2d);
			offset.scale(i * widthStep);
			q1.add(currentPoint, offset);
			q2.sub(currentPoint, offset);
			int[] lineCoords = {(int) q1.x, (int) q1.y, (int) q2.x, (int) q2.y};
			lineCoords = getScreenCoordinates(lineCoords);
			graphics.drawLine(lineCoords[0], lineCoords[1], lineCoords[2], lineCoords[3]);
			currentPoint.add(lengthStep);
		}*/
	}

	/**
	 *  Draws the ring in an aromatic ring.
	 */
	public void paintRingRing(IRing ring, Color bondColor, Graphics2D graphics)
	{
		Point2d center = GeometryToolsInternalCoordinates.get2DCenter(ring);
		System.out.println(" painting a Ringring now at " + center);
		
		double[] minmax = GeometryToolsInternalCoordinates.getMinMax(ring);
		double width = (minmax[2] - minmax[0]) * 0.7;
		double height = (minmax[3] - minmax[1]) * 0.7;
		
		//make a circle
		if (width > height)
			width = height;
		else if (height > width)
			height = width;
		
		double[] coords = { (center.x - (width / 2.0)), (center.y - (height / 2.0)) };
		//offset is the width of the ring
		double offset = (0.05 * Math.max(width, height));
		double offsetX2 = 2 * offset;

		// Fill outer oval.
		graphics.setColor(bondColor);
		Shape shape = new Ellipse2D.Double(coords[0], coords[1], width, height);
		graphics.fill(shape);
		
		// Erase inner oval.
		graphics.setColor(rendererModel.getBackColor());
		shape = new Ellipse2D.Double(coords[0] + offset, coords[1] + offset, width - offsetX2, height - offsetX2);
		graphics.fill(shape);
		
		// Reset drawing colour.
		graphics.setColor(bondColor);
	}
	/**
	 *  Triggers the paint method suitable to the bondorder of the given bond.
	 *
	 *@param  bond       The Bond to be drawn.
	 */
	public void paintBond(IBond bond, Color bondColor, Graphics2D graphics)
	{

		System.out.println("      paintBond, getstereo: " + bond.getStereo() + " getorder: " + bond.getOrder() + " x,y: " + bond.getAtom(0).getPoint2d().x + "," +
				bond.getAtom(0).getPoint2d().y);
		
		if (!GeometryToolsInternalCoordinates.has2DCoordinates(bond)) {
			return;
		}
		
		if (!rendererModel.getShowExplicitHydrogens()) {
			if (bond.getAtom(0).getSymbol().equals("H")) return;
			if (bond.getAtom(1).getSymbol().equals("H")) return;
		}

		if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE && bond.getStereo() != CDKConstants.STEREO_BOND_UNDEFINED)
		{
			// Draw stereo information if available
			if (bond.getStereo() >= CDKConstants.STEREO_BOND_UP)
			{
				paintWedgeBond(bond, bondColor, graphics);
			} else
			{
				paintDashedWedgeBond(bond, bondColor, graphics);
			}
		} else
		{
			// Draw bond order when no stereo info is available
			if (bond.getOrder() == CDKConstants.BONDORDER_SINGLE)
			{
				paintSingleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_DOUBLE)
			{
				paintDoubleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == CDKConstants.BONDORDER_TRIPLE)
			{
				paintTripleBond(bond, bondColor, graphics);
			} else if (bond.getOrder() == 8.)
			{
				paintAnyBond(bond, bondColor, graphics);
			} else
			{
				System.out.println("       painting single bond because order > 3?");
				// paint all other bonds as single bonds
				paintSingleBond(bond, bondColor, graphics);
			}
		}
	}
	/**
	 *  Paints the given single bond.
	 *
	 *@param  bond       The single bond to be drawn
	 */
	public void paintAnyBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		//TODO: rewrite this old code:
		/*if (GeometryToolsInternalCoordinates.has2DCoordinates(bond))
		{
			int[] screencoords=getScreenCoordinates(GeometryTools.getBondCoordinates(bond, r2dm.getRenderingCoordinates()));
			int dashlength=4;
			int spacelength=4;
            if ((screencoords[0] == screencoords[2]) && (screencoords[1] == screencoords[3]))
            {
                    graphics.drawLine(screencoords[0], screencoords[1], screencoords[2], screencoords[3]);
                    return;
            }
            double linelength = Math.sqrt((screencoords[2] - screencoords[0]) * (screencoords[2] - screencoords[0]) + (screencoords[3] - screencoords[1]) * (screencoords[3] - screencoords[1]));
            double xincdashspace = (screencoords[2] - screencoords[0]) / (linelength / (dashlength + spacelength));
            double yincdashspace = (screencoords[3] - screencoords[1]) / (linelength / (dashlength + spacelength));
            double xincdash = (screencoords[2] - screencoords[0]) / (linelength / (dashlength));
            double yincdash = (screencoords[3] - screencoords[1]) / (linelength / (dashlength));
            int counter = 0;
            for (double i = 0; i < linelength - dashlength; i += dashlength + spacelength)
            {
                    graphics.drawLine((int) (screencoords[0] + xincdashspace * counter), (int) (screencoords[1] + yincdashspace * counter), (int) (screencoords[0] + xincdashspace * counter + xincdash), (int) (screencoords[1] + yincdashspace * counter + yincdash));
                    counter++;
            }
            if ((dashlength + spacelength) * counter <= linelength)
            {
                    graphics.drawLine((int) (screencoords[0] + xincdashspace * counter), (int) (screencoords[1] + yincdashspace * counter), screencoords[2], screencoords[3]);
            }
		}*/
	}
	/**
	 *  Paints the given single bond.
	 *
	 *@param  bond       The single bond to be drawn
	 */
	public void paintDoubleBond(IBond bond, Color bondColor, Graphics2D graphics)
	{
		if (GeometryToolsInternalCoordinates.has2DCoordinates(bond))
		{
			double[] tempc = new double[] { bond.getAtom(0).getPoint2d().x, bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x, bond.getAtom(1).getPoint2d().y};
			
			double[] coords = GeometryToolsInternalCoordinates.distanceCalculator(tempc, 0.1);

			Line2D line = new Line2D.Double(
					coords[0], coords[1], coords[6], coords[7]
				);
			paintOneBond(line, bondColor, graphics);

			Line2D line2 = new Line2D.Double(
					coords[2], coords[3], coords[4], coords[5]
				);
			paintOneBond(line2, bondColor, graphics);
			
		}
	}
	/**
	 *  Paints the given triple bond.
	 *
	 *@param  bond       The triple bond to be drawn
	 */
	public void paintTripleBond(org.openscience.cdk.interfaces.IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("painting paintTripleBond now at " + bond.getAtom(0).getPoint2d());

		paintSingleBond(bond, bondColor, graphics);
		//TODO: rewrite this old code:
	/*	int[] coords = GeometryTools.distanceCalculator(GeometryTools.getBondCoordinates(bond,r2dm.getRenderingCoordinates()), (r2dm.getBondWidth() / 2 + r2dm.getBondDistance()));

		int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
		paintOneBond(newCoords1, bondColor, graphics);

		int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
		paintOneBond(newCoords2, bondColor, graphics);*/
	}

	public void paintSingleBond(IBond bond, Color bondColor, Graphics2D graphics)
	{
		System.out.println("  painting paintSingleBond " + bond.getAtom(0).getPoint2d() + " // " + bond.getAtom(1).getPoint2d());
		if (GeometryToolsInternalCoordinates.has2DCoordinates(bond))
		{
			Line2D line = new Line2D.Double(
					bond.getAtom(0).getPoint2d().x,
					bond.getAtom(0).getPoint2d().y,
					bond.getAtom(1).getPoint2d().x,
					bond.getAtom(1).getPoint2d().y
				);
			paintOneBond(line, bondColor, graphics);
		}
	}
	
	/**
	 *  Really paints the bond. It is triggered by all the other paintbond methods
	 *  to draw a polygon as wide as bond width.
	 *
	 *@param  coords
	 *@param  bondColor  Color of the bond
	 */
	public void paintOneBond(Line2D line, Color bondColor, Graphics2D graphics)
	{
		// draw the shapes
		graphics.setColor(bondColor);
		graphics.setStroke(new BasicStroke(
				(float) (rendererModel.getBondWidth()/rendererModel.getBondLength()), 
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
		);
		graphics.setColor(bondColor);
		graphics.draw(line);
	}
	
/*	public void paintOneBond(int[] coords, Color bondColor, Graphics2D graphics)
	{
		graphics.setColor(bondColor);
        int[] newCoords = GeometryTools.distanceCalculator(coords, r2dm.getBondWidth() / 2);
		int[] screenCoords = getScreenCoordinates(newCoords);
    int[] xCoords = {screenCoords[0], screenCoords[2], screenCoords[4], screenCoords[6]};
		int[] yCoords = {screenCoords[1], screenCoords[3], screenCoords[5], screenCoords[7]};
        graphics.fillPolygon(xCoords, yCoords, 4);
	}*/
	
	private AffineTransform createScaleTransform(Rectangle2D contextBounds, Rectangle2D rendererBounds) {
		AffineTransform affinet = new AffineTransform();
		
		//scale
		double factor = rendererModel.getZoomFactor() * (1.0 - rendererModel.getMargin() * 2.0);
	    double scaleX = factor * rendererBounds.getWidth() / contextBounds.getWidth();
	    double scaleY = factor * rendererBounds.getHeight() / contextBounds.getHeight();

	    if (scaleX > scaleY) {
	    	//System.out.println("Scaled by Y: " + scaleY);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	affinet.scale(scaleY, -scaleY);
	    } else {
	    	//System.out.println("Scaled by X: " + scaleX);
	    	// FIXME: should be -X: to put the origin in the lower left corner 
	    	affinet.scale(scaleX, -scaleX);
	    }
	    //translate
	    double scale = affinet.getScaleX();
		//System.out.println("scale: " + scale);
	    double dx = -contextBounds.getX() * scale + 0.5 * (rendererBounds.getWidth() - contextBounds.getWidth() * scale);
	    double dy = -contextBounds.getY() * scale - 0.5 * (rendererBounds.getHeight() + contextBounds.getHeight() * scale);
	    //System.out.println("dx: " + dx + " dy:" +dy);						
	    affinet.translate(dx / scale, dy / scale);
	    
		return affinet;
	}
	
	
	/*public static Point2D getCoorFromScreen(Graphics2D graphics, Point2D ptSrc) {
		Point2D ptDst = new Point2D.Double();
		AffineTransform affine = graphics.getTransform();
		try {
			affine.inverseTransform(ptSrc, ptDst);
		}
		catch (Exception exception) {
			System.out.println("Unable to reverse affine transformation");
			System.exit(0);
		}
		return ptDst;
	}*/
	/**
	 *  Returns model coordinates from screencoordinates provided by the graphics translation
	 *   
	 * @param ptSrc the point to convert
	 * @return Point2D in real world coordinates
	 */
	public Point2D getCoorFromScreen(Point2D ptSrc) {
		Point2D ptDst = new Point2D.Double();
		try {
			affine.inverseTransform(ptSrc, ptDst);
		}
		catch (Exception exception) {
			System.out.println("Unable to reverse affine transformation");
			System.exit(0);
		}
		return ptDst;
	}
	/**
	 * 
	 * @param container
	 * @param ptSrc in real world coordinates (ie not screencoordinates)
	 */
	public static void showClosestAtomOrBond(IAtomContainer container, Point2D ptSrc) {
		IAtom atom = GeometryToolsInternalCoordinates.getClosestAtom( ptSrc.getX(), ptSrc.getY(), container);
		double Atomdist = Math.sqrt(Math.pow(atom.getPoint2d().x - ptSrc.getX(), 2) + Math.pow(atom.getPoint2d().y - ptSrc.getY(), 2));

		System.out.println("closest Atom distance: " + Atomdist + " Atom:" + atom);
		
		IBond bond = GeometryToolsInternalCoordinates.getClosestBond( ptSrc.getX(), ptSrc.getY(), container);
		Point2d bondCenter = GeometryToolsInternalCoordinates.get2DCenter(bond.atoms());
		
		double Bonddist = Math.sqrt(Math.pow(bondCenter.x - ptSrc.getX(), 2) + Math.pow(bondCenter.y - ptSrc.getY(), 2));
		System.out.println("closest Bond distance: " + Bonddist + " Bond: " + bond);
	}
	
	private Rectangle2D createRectangle2D(List shapes) {
	    Iterator it = shapes.iterator();
	    
	    if (it.hasNext()) {
	    	Rectangle2D result = ((Shape) it.next()).getBounds2D();
	    
	    	while (it.hasNext()) { 
	    		Rectangle2D.union(result, ((Shape) it.next()).getBounds2D(), result);
	    	}
	        
	    	// FIXME: make a decent estimate for the margin
	    	double margin = result.getHeight() / 50; //2% margin
	    	if (margin < 0.2) {
	    		margin = 0.2; //0.2 is enough to make symbols appear on screen	
	    	}
	    	result.setRect(result.getMinX() - margin, result.getMinY() - margin, result.getWidth() + 2 * margin, result.getHeight() + 2 * margin);
	    
	    	return result;    
	    }
	    else 
	    	return null;
	}
	
	public Renderer2DModel getRenderer2DModel() {
		return this.rendererModel;
	}

	public void setRenderer2DModel(Renderer2DModel model) {
		this.rendererModel = model;
	}
}