package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.Set;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.DijkstraShortestPathAlgorithm;
import org.eclipse.zest.core.viewers.EntityConnectionData;

public class ShortestPathInterestingDependenciesCalculator extends AbstractInterestingDependenciesCalculator {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractInterestingDependenciesCalculator#calculateInterestingDependencies(org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider, java.util.Set, java.util.Set)
	 */
	protected void calculateInterestingDependencies(AbstractDependencyVisualizationLabelProvider provider, 
			Set interestingEntities, Set interestingRelationships) {
		DiGraph graph = provider.getDependencyVisualizationView().getDependencyGraph();
		if (graph != null && provider.getRootVertex() != null && provider.getSelected() != null) {
			IVertex[] shortestPath = 
					new DijkstraShortestPathAlgorithm().findShortestPath(
							graph, provider.getRootVertex(), provider.getSelected());
			if (shortestPath.length > 0) {
				for (int i = 0; i < shortestPath.length-1; i++) {
					IVertex v = shortestPath[i];
					interestingEntities.add(v);
					interestingRelationships.add(new EntityConnectionData(v, shortestPath[i+1]));
				}
				interestingEntities.add(shortestPath[shortestPath.length-1]);
			}
		}
	}
	
}
