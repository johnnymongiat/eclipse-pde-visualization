package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;

/**
 * An Action that creates a filter to show the corresponding circuit
 */
public class CircuitAction extends Action {

	private final IDependencyVisualizationView fView;
	private final IElementaryCircuit fCircuit;

	public CircuitAction(IDependencyVisualizationView view, IElementaryCircuit circuit, String text) {
		super(text, AS_RADIO_BUTTON);
		fView = view;
		fCircuit = circuit;
		setDescription(PDEMessages.FOCUS_ON_CIRCUIT);
		setToolTipText(PDEMessages.FOCUS_ON_CIRCUIT);
		setImageDescriptor(PDEVizImages.DESC_LOOP_OBJ);
	}

	public void run() {
		fView.focusOnCircuit(fCircuit);
	}

}
