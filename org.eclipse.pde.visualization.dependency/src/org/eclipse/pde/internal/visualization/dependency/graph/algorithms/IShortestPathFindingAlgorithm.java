package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Interface used to find the shortest path between two vertices in a <code>graph</code>.
 */
public interface IShortestPathFindingAlgorithm {

	/**
	 * Returns an array containing all vertices included in the shortest paths between two vertices in a <code>graph</code>.
	 * 
	 * @param graph - the <code>graph</code> used to calculate the shortest path
	 * @param source - the source <code>vertex</code>
	 * @param target - the target <code>vertex</code>
	 * 
	 * @return an array containing all vertices included in the shortest paths between two vertices of the graph.
	 */
	public IVertex[] findShortestPath(DiGraph graph, IVertex source, IVertex target);
	
}
