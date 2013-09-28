package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * The layout algorithm used to show a circuit
 */
public class CircuitLayoutAlgorithm extends AbstractLayoutAlgorithm {

	private int totalSteps;
	private final IElementaryCircuit fCircuit;

	public CircuitLayoutAlgorithm(IElementaryCircuit circuit, int styles) {
		super(styles);
		if (circuit == null) {
			throw new IllegalArgumentException("The 'circuit' parameter cannot be null."); //$NON-NLS-1$
		}
		fCircuit = circuit;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#setLayoutArea(double, double, double, double)
	 */
	public void setLayoutArea(double x, double y, double width, double height) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#isValidConfiguration(boolean, boolean)
	 */
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		return true;
	}

	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		// Obtain an ordered arrangement of the entities to layout to be consistent with 
		// the corresponding circuit ordering.
		LayoutEntity[] circuitEntities = new LayoutEntity[entitiesToLayout.length];
		for (int i = 0; i < entitiesToLayout.length; i++) {
			LayoutEntity entity = entitiesToLayout[i].getLayoutEntity();
			IVertex v = (IVertex) ((GraphNode) entity.getGraphData()).getData();
			circuitEntities[fCircuit.indexOf(v)] = entity;
		}

		totalSteps = circuitEntities.length;
		double cx = boundsWidth / 2;
		double cy = boundsHeight / 2;
		double d = Math.min(cx, cy);
		double angle = 360 / (double) circuitEntities.length;
		fireProgressStarted(totalSteps);
		for (int currentStep = 0; currentStep < circuitEntities.length; currentStep++) {
			LayoutEntity entity = circuitEntities[currentStep];
			double theta = currentStep * angle;
			int quadrant = 4;
			if (theta >= 0 && theta <= 90) {
				quadrant = 1;
			} else if (theta > 90 && theta <= 180) {
				quadrant = 2;
			} else if (theta > 180 && theta <= 270) {
				quadrant = 3;
			}

			double x = Math.abs(d * Math.sin(Math.toRadians(theta)));
			double y = Math.abs(d * Math.cos(Math.toRadians(theta)));
			if (quadrant == 1) {
				entity.setLocationInLayout(cx + x, cy - y);
			} else if (quadrant == 2) {
				entity.setLocationInLayout(cx + x, cy + y);
			} else if (quadrant == 3) {
				entity.setLocationInLayout(cx - x, cy + y);
			} else {
				entity.setLocationInLayout(cx - x, cy - y);
			}

			fireProgressEvent(currentStep, totalSteps);
		}
		fireProgressEnded(totalSteps);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#preLayoutAlgorithm(org.eclipse.zest.layouts.dataStructures.InternalNode[], org.eclipse.zest.layouts.dataStructures.InternalRelationship[], double, double, double, double)
	 */
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double x, double y, double width, double height) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#postLayoutAlgorithm(org.eclipse.zest.layouts.dataStructures.InternalNode[], org.eclipse.zest.layouts.dataStructures.InternalRelationship[])
	 */
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#getTotalNumberOfLayoutSteps()
	 */
	protected int getTotalNumberOfLayoutSteps() {
		return totalSteps;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#getCurrentLayoutStep()
	 */
	protected int getCurrentLayoutStep() {
		return 0;
	}
	
}