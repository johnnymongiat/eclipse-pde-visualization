package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;

/**
 * Interface used to find all the elementary circuits in a graph.
 */
public interface IElementaryCircuitFindingAlgorithm {

	/**
	 * Returns an array containing all elementary circuits
	 * @param graph - the graph used to calculate elementary circuits
	 * @return an array containing all elementary circuits
	 */
	public IElementaryCircuit[] findAllElementaryCircuits(DiGraph graph);
	
}
