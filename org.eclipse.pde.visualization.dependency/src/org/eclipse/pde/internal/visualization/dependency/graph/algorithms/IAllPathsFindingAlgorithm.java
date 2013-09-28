package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.List;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Interface used to find all the paths between two vertices of the graph.
 */
public interface IAllPathsFindingAlgorithm {

	/**
	 * Returns a <code>List</code> containing all the paths between two vertices of the <code>graph</code>.
	 * 
	 * @param graph - the graph used to calculate all paths
	 * @param source - the source vertex
	 * @param target - the target vertex
	 * 
	 * @return an <code>List</code> containing all the paths between two vertices of the graph.
	 */
	public List findAllPaths(DiGraph graph, IVertex source, IVertex target);
	
}
