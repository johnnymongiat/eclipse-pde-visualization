package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Default implementation of an {@link IElementaryCircuit}
 */
public class ElementaryCircuit implements IElementaryCircuit {

	private final List circuit;
	private final Map map;
	
	/**
	 * Constructs an empty ElementaryCircuit.
	 */
	public ElementaryCircuit() {
		this(0);
	}
	
	/**
	 * Constructs an empty <code>ElementaryCircuit</code> with the specified initial capacity.
	 * @param initialCapacity - the initial capacity of the <code>ElementaryCircuit</code>
	 */
	public ElementaryCircuit(int initialCapacity) {
		circuit = new ArrayList(initialCapacity);
		map = new HashMap(initialCapacity);
	}
	
	/**
	 * Appends the specified vertex to this <code>ElementaryCircuit</code>.
	 * @param vertex
	 */
	public void appendVertex(IVertex vertex) {
		if (vertex == null) {
			throw new IllegalArgumentException("Cannot append a 'null' vertex to the elementary circuit."); //$NON-NLS-1$
		}
		if (map.containsKey(vertex)) {
			throw new IllegalArgumentException("Cannot append a duplicate vertex to the elementary circuit."); //$NON-NLS-1$
		}
		circuit.add(vertex);
		map.put(vertex, Integer.valueOf(size() - 1));
	}
	
	public List getVertices() {
		return Collections.unmodifiableList(circuit);
	}

	public int indexOf(IVertex vertex) {
		Integer index = (Integer) map.get(vertex);
		return (index == null ? -1 : index.intValue());
	}

	public int size() {
		return circuit.size();
	}

}
