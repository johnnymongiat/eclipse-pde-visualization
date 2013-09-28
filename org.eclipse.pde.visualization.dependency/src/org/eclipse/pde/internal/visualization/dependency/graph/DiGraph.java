package org.eclipse.pde.internal.visualization.dependency.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of a directed graph data structure using an adjacency list representation
 */
public class DiGraph {

	/* Adjacency list representation of the directed graph: Map<IVertex, List<IVertex>> */
	private Map adjacencyMap = new HashMap(0);

	/**
	 * Default constructor 
	 */
	public DiGraph() {}

	/**
	 * Ads a vertex to the <code>graph</code>.
	 * <p>
	 * Returns <code>true</code> if the <code>vertex</code> was added to the <code>graph</code> or <code>false</code> if already exists in the <code>graph</code>. 
	 * </p>
	 * @param vertex to be added to the <code>graph</code>
	 * @return true if the vertex was added to the <code>graph</code>
	 * @throws IllegalArgumentException if supplied argument is null
	 */
	public boolean addVertex(IVertex vertex) {
		if (vertex == null) {
			throw new IllegalArgumentException("The 'vertex' cannot be null."); //$NON-NLS-1$
		}
		if (adjacencyMap.containsKey(vertex)) {
			return false;
		}
		adjacencyMap.put(vertex, new ArrayList(0));
		return true;
	}

	/**
	 * Ads an edge to the <code>graph</code>.
	 * <p>
	 * Return <code>true</code> if the edge was added to the <code>graph</code> or <code>false</code> if already exists in the <code>graph</code>.
	 * </p>
	 * @param source the source <code>vertex</code>
	 * @param target the target <code>vertex</code>
	 * @return true if the vertex was added to the <code>graph</code>
	 */
	public boolean addEdge(IVertex source, IVertex target) {
		addVertex(source);
		addVertex(target);
		List list = (List) adjacencyMap.get(source);
		if (!list.contains(target)) {
			return list.add(target);
		}
		return false;
	}

	public boolean isEmpty() {
		return adjacencyMap.isEmpty();
	}
	
	/**
	 * Return the number of vertices in the <code>graph</code>.
	 * @return the number of vertices in this <code>graph</code>.
	 */
	public int numberOfVertices() {
		return adjacencyMap.size();
	}

	/**
	 * Return the number of edges in the <code>graph</code>.
	 * @return the number of edges in this <code>graph</code>.
	 */
	public int numberOfEdges() {
		int count = 0;
		Set entries = adjacencyMap.entrySet();
		Iterator itr = entries.iterator();
		while (itr.hasNext()) {
			count += ((List)((Map.Entry) itr.next()).getValue()).size();
		}
		return count;
	}
	
	/**
	 * Returns an array containing all the vertices connected to the specified <code>IVertex</code>.
	 * @return an array containing all the vertices connected to the specified <code>IVertex</code>.
	 */
	public IVertex[] getConnectedTo(IVertex source) {
		if (adjacencyMap.containsKey(source)) {
			List edges = (List) adjacencyMap.get(source);
			return (IVertex[]) edges.toArray(new IVertex[edges.size()]);
		}
		return new IVertex[0];
	}
	
	/**
	 * Returns an array containing all the vertices in this <code>graph</code>.
	 * @return an array containing all the vertices in this <code>graph</code>.
	 */
	public IVertex[] getVertices() {
		Set vertices = adjacencyMap.keySet();
		return (IVertex[]) vertices.toArray(new IVertex[vertices.size()]);
	}
	
	/**
	 * Returns <code>true</code> if this <code>graph</code> contains the specified <code>IVertex</code>.
	 * @return <code>true</code> if this <code>graph</code> contains the specified <code>IVertex</code>.
	 */
	public boolean containsVertex(IVertex vertex) {
		if (vertex != null && adjacencyMap.containsKey(vertex)) {
			return true;
		}
		return false;
	}
}
