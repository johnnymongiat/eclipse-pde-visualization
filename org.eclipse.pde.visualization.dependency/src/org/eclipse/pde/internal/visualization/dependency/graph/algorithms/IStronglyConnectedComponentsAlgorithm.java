package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.List;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;

/**
 * Interface used for finding the strongly connected components of a given <code>DiGraph</code>.
 */
public interface IStronglyConnectedComponentsAlgorithm {

	/**
	 * Returns an array of <code>List</code>(s) where each list holds the vertex set for an individual 
	 * strongly connected component.
	 * 
	 * @param graph the source <code>DiGraph</code> (cannot be <code>null</code>)
	 * 
	 * @return an array of <code>List</code>(s) where each list holds the vertex set for an individual 
	 * strongly connected component.
	 * 
	 * @throws IllegalArgumentException if the specified <code>graph</code> is <code>null</code>.
	 */
	public List[] findStronglyConnectedComponents(DiGraph graph);

}
