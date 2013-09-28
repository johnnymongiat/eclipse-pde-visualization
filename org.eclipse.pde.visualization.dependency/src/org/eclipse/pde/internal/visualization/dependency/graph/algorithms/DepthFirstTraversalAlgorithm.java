package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Implementation of the Depth-first search (DFS) graph traversal algorithm used to find 
 * all paths between two vertices of the graph.
 */
public class DepthFirstTraversalAlgorithm implements IAllPathsFindingAlgorithm {
	
	/**
	 * Returns a <code>List</code> containing all the paths between two vertices of the <code>graph</code>.
	 * 
	 * @param graph the <code>graph</code> used to calculate all paths
	 * @param source the source <code>IVertex</code>
	 * @param target the target <code>IVertex</code>
	 * 
	 * @return a <code>List</code> containing all the paths between two vertices of the <code>graph</code>.
	 * 
	 * @throws IllegalArgumentException if the <code>source</code> or <code>target</code> vertices are not members
	 * of the provided <code>graph</code>.
	 */
	public List findAllPaths(DiGraph graph, IVertex source, IVertex target) {
		if (!graph.containsVertex(source) || !graph.containsVertex(target)){
			throw new IllegalArgumentException("The 'source' and the 'target' vertices should be in the graph"); //$NON-NLS-1$
		}
		LinkedList currentPath = new LinkedList();
		List allPaths = new ArrayList(0);
		depthFirstSearch(graph, source, target, currentPath, allPaths);
		return allPaths;
	}
		
	//Implementation of all paths using depth first traversal
	private void depthFirstSearch(DiGraph graph, IVertex root, IVertex destination, LinkedList path, List allPaths) {
		IVertex currentVertex = root;
		path.add(currentVertex);
		if (currentVertex.equals(destination)){
			allPaths.add(path.clone());
			return;
		}
		
		IVertex[] children = graph.getConnectedTo(currentVertex);
		for (int i = 0; i < children.length; i++) {
			IVertex v = children[i];
			if (!path.contains(v)) {
				depthFirstSearch(graph, v, destination, path, allPaths);
				path.remove(v);
			}
		}
		path.remove(currentVertex);
	}

}
