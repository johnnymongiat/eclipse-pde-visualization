package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.List;
import java.util.Set;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.DepthFirstTraversalAlgorithm;
import org.eclipse.zest.core.viewers.EntityConnectionData;

public class AllPathsInterestingDependenciesCalculator extends AbstractInterestingDependenciesCalculator {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractInterestingDependenciesCalculator#calculateInterestingDependencies(org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider, java.util.Set, java.util.Set)
	 * 
	 * uses the DepthFirstTraversalAlgorithm to find all paths
	 */
	protected void calculateInterestingDependencies(AbstractDependencyVisualizationLabelProvider provider, 
			Set interestingEntities, Set interestingRelationships) {
		
		DiGraph graph = provider.getDependencyVisualizationView().getDependencyGraph();
		if (graph != null && provider.getRootVertex() != null && provider.getSelected() != null) {
			List allPaths = 
					new DepthFirstTraversalAlgorithm().findAllPaths(
							graph, provider.getRootVertex(), provider.getSelected());
			if (allPaths.size() > 0) {
				for (int i = 0; i < allPaths.size(); i++) {
					List list = (List) allPaths.get(i);
					
					int index = 0;
					while (index < list.size()-1){
						EntityConnectionData entityConnectionData = new EntityConnectionData(list.get(index), list.get(++index));
						interestingRelationships.add(entityConnectionData);
					}
					interestingEntities.addAll(list);
				}
			}
		}
	}
	
}
