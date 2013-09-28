package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;

/**
 * An Action that uses an {@link AbstractInterestingDependenciesCalculator} 
 * to reveal all paths between the root and the selected node. 
 */
public class AllPathsAction extends Action {
	
	private AbstractDependencyVisualizationLabelProvider fLabelProvider;
	
	public AllPathsAction(AbstractDependencyVisualizationLabelProvider labelProvider) {
		super(PDEMessages.ALL_PATHS, AS_RADIO_BUTTON);
		setDescription(PDEMessages.ALL_PATHS);
		setToolTipText(PDEMessages.ALL_PATHS);
		
		fLabelProvider = labelProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		fLabelProvider.updateInterestingDependenciesCalculator(new AllPathsInterestingDependenciesCalculator());
	}
}