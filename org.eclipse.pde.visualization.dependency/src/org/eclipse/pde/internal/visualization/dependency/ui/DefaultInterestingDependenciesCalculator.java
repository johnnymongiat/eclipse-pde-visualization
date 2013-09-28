package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.Set;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.zest.core.viewers.EntityConnectionData;

/**
 * The default interesting dependencies calculator that highlights only the direct children of a node
 */
public class DefaultInterestingDependenciesCalculator extends AbstractInterestingDependenciesCalculator {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractInterestingDependenciesCalculator#calculateInterestingDependencies(org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider, java.util.Set, java.util.Set)
	 */
	protected void calculateInterestingDependencies(AbstractDependencyVisualizationLabelProvider provider, 
			Set interestingEntities, Set interestingRelationships) {
		DiGraph graph = provider.getDependencyVisualizationView().getDependencyGraph();
		if (graph != null && provider.getSelected() != null) {
			IVertex source = provider.getSelected();
			IVertex[] children = graph.getConnectedTo(source);
			for (int i = 0; i < children.length; i++) {
				IVertex child = children[i];
				interestingEntities.add(child);
				interestingRelationships.add(new EntityConnectionData(source, child));
			}
		}
	}
	
}
