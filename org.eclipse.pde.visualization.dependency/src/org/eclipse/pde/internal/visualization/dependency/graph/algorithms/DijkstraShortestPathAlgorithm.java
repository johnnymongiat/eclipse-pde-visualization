package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Implementation of the Dijkstra's algorithm used to find 
 * the shortest path between two vertices of the graph.
 */
public class DijkstraShortestPathAlgorithm implements IShortestPathFindingAlgorithm {

	private static final String SPID = DijkstraShortestPathAlgorithm.class.getName() + ".SPID"; //$NON-NLS-1$
	private static final String DISTANCE = DijkstraShortestPathAlgorithm.class.getName() + ".DISTANCE"; //$NON-NLS-1$
	private static final String PREVIOUS = DijkstraShortestPathAlgorithm.class.getName() + ".PREVIOUS"; //$NON-NLS-1$
	private static final Object UNDEFINED = ""; //$NON-NLS-1$

	private int distanceOf(IVertex v) {
		return ((Integer)v.getPropertyValue(DISTANCE)).intValue();
	}

	private Object previousOf(IVertex v) {
		return v.getPropertyValue(PREVIOUS);
	}

	private IVertex getUnmarkedVertexWithSmallestDistance(Map unmarkedMap) {
		Iterator itr = unmarkedMap.entrySet().iterator();
		IVertex vertex = null;
		long max = Long.MAX_VALUE;
		while (itr.hasNext()) {
			Entry entry = (Entry) itr.next();
			IVertex temp = (IVertex) entry.getValue();
			int d = distanceOf(temp);
			if (d < max) {
				max = d;
				vertex = temp;
			}
		}
		return vertex;
	}

	/**
	 * Returns an array containing all vertices included in the shortest paths between two vertices in a <code>graph</code>.
	 * 
	 * @param graph - the <code>graph</code> used to calculate the shortest path
	 * @param source - the source <code>vertex</code>
	 * @param target - the target <code>vertex</code>
	 * @return an array containing all vertices included in the shortest paths between two vertices of the graph.
	 * @throws IllegalArgumentException if the source and the target vertices are not in the graph
	 */
	public IVertex[] findShortestPath(DiGraph graph, IVertex source, IVertex target) {
		if (!graph.containsVertex(source) && !graph.containsVertex(target)){
			throw new IllegalArgumentException("The 'source' and 'target' vertices should be in the graph"); //$NON-NLS-1$
		}

		IVertex[] vertices = graph.getVertices();
			
		try {
			Map unmarkedMap = new HashMap(vertices.length);
			for (int i = 0; i < vertices.length; i++) {
				IVertex v = vertices[i];
				v.setPropertyValue(DISTANCE, Integer.valueOf(Integer.MAX_VALUE));
				v.setPropertyValue(PREVIOUS, UNDEFINED);
				String id = String.valueOf(i);
				v.setPropertyValue(SPID, id);
				unmarkedMap.put(id, v);
			}

			source.setPropertyValue(DISTANCE, Integer.valueOf(0));

			while (!unmarkedMap.isEmpty()) {
				// Get the unmarked vertex with the minimal distance.
				IVertex w = getUnmarkedVertexWithSmallestDistance(unmarkedMap);

				// stop if all remaining vertices are inaccessible from source
				if (distanceOf(w) == Integer.MAX_VALUE){
					break;
				}
								
				// Mark the vertex (i.e. remove it from the unmarked map).
				unmarkedMap.remove(w.getPropertyValue(SPID));

				// Stop processing if target has been reached.
				if (w.equals(target)) {
					break;
				}

				// Relaxation heuristic.
				IVertex[] neighbors = graph.getConnectedTo(w);
				if (neighbors.length > 0) {
					int alt = distanceOf(w) + 1; // use "1" since DiGraph has no concept of weighted edges (i.e. all edges have equal weight)
					for (int i = 0; i < neighbors.length; i++) {
						IVertex z = neighbors[i];
						if (unmarkedMap.containsKey(z.getPropertyValue(SPID))) {
							if (alt < distanceOf(z)) {
								z.setPropertyValue(DISTANCE, Integer.valueOf(alt));
								z.setPropertyValue(PREVIOUS, w);
							}
						}
					}
				}
			}

			// Construct the shortest path.
			List shortestPath = new ArrayList(0);
			IVertex u = target;
			Object prev = previousOf(u);
			while (!prev.equals(UNDEFINED)) {
				u = (IVertex) prev;
				shortestPath.add(0, u);
				prev = previousOf(u);
			}
			if (!shortestPath.isEmpty()) {
				shortestPath.add(target);
			}

			return (IVertex[]) shortestPath.toArray(new IVertex[shortestPath.size()]);
		}
		finally {
			// clean-up
			for (int i = 0; i < vertices.length; i++) {
				IVertex v = vertices[i];
				v.removeProperty(DISTANCE);
				v.removeProperty(PREVIOUS);
				v.removeProperty(SPID);
			}
		}
	}

}
