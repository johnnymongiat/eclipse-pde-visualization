package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;

/**
 * An Action that uses an {@link AbstractInterestingDependenciesCalculator} 
 * to reveal the shortest path between the root and the selected node. 
 */
public class ShortestPathAction extends Action {
	
	private AbstractDependencyVisualizationLabelProvider fLabelProvider;
	
	public ShortestPathAction(AbstractDependencyVisualizationLabelProvider labelProvider) {
		super(PDEMessages.SHORTEST_PATH, AS_RADIO_BUTTON);
		setDescription(PDEMessages.SHORTEST_PATH);
		setToolTipText(PDEMessages.SHORTEST_PATH);
		
		fLabelProvider = labelProvider;
	}

	public void run() {
		fLabelProvider.updateInterestingDependenciesCalculator(new ShortestPathInterestingDependenciesCalculator());
	}
}
