package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Implementation of an <code>IStronglyConnectedComponentsAlgorithm</code> based on Tarjan's algorithm.
 */
public class TarjanAlgorithm implements IStronglyConnectedComponentsAlgorithm {

	private static final String INDEX = TarjanAlgorithm.class.getName() + ".tarjanINDEX"; //$NON-NLS-1$
	private static final String LOWLINK = TarjanAlgorithm.class.getName() + ".tarjanLOWLINK"; //$NON-NLS-1$
	
	private int index = 0;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IStronglyConnectedComponentsAlgorithm#findStronglyConnectedComponents(org.eclipse.pde.internal.visualization.dependency.graph.DiGraph)
	 */
	public List[] findStronglyConnectedComponents(DiGraph graph) {
		if (graph == null){
			throw new IllegalArgumentException("The 'graph' cannot be null"); //$NON-NLS-1$
		}
		
		index = 0;
		Stack stack = new Stack();
		List sccs = new ArrayList(0);
		IVertex[] vertices = graph.getVertices();
		try {
			for (int i = 0; i < vertices.length; i++) {
				IVertex v = vertices[i];
				if (v.getPropertyValue(INDEX) == null) {
					computeSCC(graph, v, stack, sccs);
				}
			}
			return (List[]) sccs.toArray(new List[sccs.size()]);
		} 
		finally {
			// clean-up
			for (int i = 0; i < vertices.length; i++) {
				IVertex v = vertices[i];
				v.removeProperty(INDEX);
				v.removeProperty(LOWLINK);
			}
		}
	}
	
	private void computeSCC(final DiGraph graph, final IVertex v, final Stack stack, final List sccs) {
		// Set the depth index for v to the smallest unused index
	    v.setPropertyValue(INDEX, Integer.valueOf(index));
	    v.setPropertyValue(LOWLINK, Integer.valueOf(index));
	    index++;
	    stack.push(v);
		
	    // Consider successors of v
	    IVertex[] edges = graph.getConnectedTo(v);
	    for (int i = 0; i < edges.length; i++) {
	    	IVertex w = edges[i];
	    	if (w.getPropertyValue(INDEX) == null) {
	    		// Successor w has not yet been visited; recurse on it
	    		computeSCC(graph, w, stack, sccs);
	    		v.setPropertyValue(LOWLINK, Integer.valueOf(Math.min(lowLinkOf(v), lowLinkOf(w))));
	    	}
	    	else if (stack.contains(w)) {
	    		// Successor w is in stack S and hence in the current SCC
		        v.setPropertyValue(LOWLINK, Integer.valueOf(Math.min(lowLinkOf(v), indexOf(w))));
	    	}
	    }

	    // If v is a root node, pop the stack and generate an SCC
	    if (lowLinkOf(v) == indexOf(v)) {
	    	// start a new strongly connected component
	    	List scc = new ArrayList(0);
	    	sccs.add(scc);
	    	
	    	IVertex x = null;
			do {
				x = (IVertex) stack.pop();
				scc.add(x);
			} while (!v.equals(x));	
	    }		
	}
	
	private int indexOf(IVertex v) {
		return ((Integer) v.getPropertyValue(INDEX)).intValue();
	}
	
	private int lowLinkOf(IVertex v) {
		return ((Integer) v.getPropertyValue(LOWLINK)).intValue();
	}

}
