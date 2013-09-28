package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.List;

import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/** 
 * Interface representing an elementary circuit 
 */
public interface IElementaryCircuit {

	/**
	 * Returns the list of vertices contained in the elementary circuit.
	 * @return the list of vertices contained in the elementary circuit.
	 */
	public List getVertices();
	
	/**
	 * Returns the index of the specified <code>vertex</code>.
	 * @param vertex - the vertex whose index is to be returned
	 * @return the index of the specified <code>vertex</code>.
	 */
	public int indexOf(IVertex vertex);
	
	/**
	 * Returns the size of the elementary circuit.
	 * @return the size of the elementary circuit.
	 */
	public int size();
	
}
