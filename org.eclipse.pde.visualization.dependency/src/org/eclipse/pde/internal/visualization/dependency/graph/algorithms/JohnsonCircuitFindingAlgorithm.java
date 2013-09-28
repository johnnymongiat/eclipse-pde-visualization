package org.eclipse.pde.internal.visualization.dependency.graph.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Donald B. Johnson's algorithm used for finding all elementary circuits of a directed graph
 */
public class JohnsonCircuitFindingAlgorithm implements IElementaryCircuitFindingAlgorithm {

	private static final String INDEX = JohnsonCircuitFindingAlgorithm.class.getName() + ".johnsonCircuitINDEX"; //$NON-NLS-1$
	
	private Map Ak = null;//Map<IVertex, List<IVertex>>
	private Map B = null;//Map<IVertex, List<IVertex>>
	private boolean[] blocked;
	private int s;
	private final Stack stack = new Stack();//Stack<IVertex>
	private final IStronglyConnectedComponentsAlgorithm sccAlg;
	private DiGraph origGraph;
	private List circuits;//List<IElementaryCircuit>
	
	public JohnsonCircuitFindingAlgorithm() {
		this(new TarjanAlgorithm());
	}
	
	public JohnsonCircuitFindingAlgorithm(IStronglyConnectedComponentsAlgorithm sccAlg) {
		if (sccAlg == null)
			throw new IllegalArgumentException("The 'sccAlg' cannot be null."); //$NON-NLS-1$
		this.sccAlg = sccAlg;
	}
	
	private static final int indexOf(IVertex v) {
		return ((Integer)v.getPropertyValue(INDEX)).intValue();
	}
	
	private void unblock(IVertex vertex) {
		int u = indexOf(vertex);
		blocked[u-1] = false;
		List edges = (List) B.get(vertex);
		Iterator itr = edges.iterator();
		while (itr.hasNext()) {
			IVertex wVertex = (IVertex) itr.next();
			itr.remove();
			int w = indexOf(wVertex);
			if (blocked[w-1]) {
				unblock(wVertex);
			}
		}
	}
	
	private boolean circuit(IVertex vertex) {
		boolean f = false;
		stack.push(vertex);
		int v = indexOf(vertex);
		blocked[v-1] = true;
		
		List edges = (List) Ak.get(vertex);
		Iterator itr = edges.iterator();
		while (itr.hasNext()) {
			IVertex wVertex = (IVertex) itr.next();
			int w = indexOf(wVertex);
			if (w == s) {
				// output circuit composed of stack
				ElementaryCircuit circuit = new ElementaryCircuit(stack.size());
				for (int i = 0; i < stack.size(); i++) {
					circuit.appendVertex((IVertex) stack.get(i));
				}
				circuits.add(circuit);
				f = true;
			}
			else if (!blocked[w-1]) {
				if (circuit(wVertex)) {
					f = true;
				}
			}
		}
		
		if (f) {
			unblock(vertex);
		}
		else {
			edges = (List) Ak.get(vertex);
			itr = edges.iterator();
			while (itr.hasNext()) {
				IVertex wVertex = (IVertex) itr.next();
				List wEdges = (List) B.get(wVertex);
				if (!wEdges.contains(vertex)) {
					wEdges.add(vertex);
				}
			}
		}
		
		stack.pop();
		
		return f;
	}
	
	private IVertex leastVertex(Map adj) {
		IVertex least = null;
		int current = Integer.MAX_VALUE;
		Iterator keysItr = adj.keySet().iterator();
		while (keysItr.hasNext()) {
			IVertex v = (IVertex) keysItr.next();
			int t = indexOf(v);
			if (t < current) {
				current = t;
				least = v;
			}
		}
		return least;
	}
	
	private Map getAdjacencyStructureOfSCC(List scc, DiGraph g) {
		Map adjacencyMap = new HashMap(0);
		Iterator itr = scc.iterator();
		while (itr.hasNext()) {
			IVertex v = (IVertex) itr.next();
			List list = new ArrayList(0);
			adjacencyMap.put(v, list);
			IVertex[] edges = g.getConnectedTo(v);
			for (int i = 0; i < edges.length; i++) {
				IVertex w = edges[i];
				if (scc.contains(w)) {
					list.add(w);
				}
			}
		}
		return adjacencyMap;
	}
	
	private DiGraph createSubgraph(int s, int n) {
		DiGraph graph = new DiGraph();
		IVertex[] vertices = origGraph.getVertices();
		for (int i = 0; i < vertices.length; i++) {
			IVertex v = vertices[i];
			int index = indexOf(v);
			if (index >= s && index <= n) {
				graph.addVertex(v);
			}
		}
		
		vertices = graph.getVertices();
		for (int i = 0; i < vertices.length; i++) {
			IVertex v = vertices[i];
			IVertex[] edges = origGraph.getConnectedTo(v);
			for (int j = 0; j < edges.length; j++) {
				IVertex w = edges[j];
				if (graph.containsVertex(w)) {
					graph.addEdge(v, w);
				}
			}
		}
		
		return graph;
	}
	
	/**
	 * Returns an array containing all elementary circuits
	 * @param graph - the graph used to calculate elementary circuits
	 * @return an array containing all elementary circuits
	 * @throws IllegalArgumentException if the graph is null
	 */	
	public IElementaryCircuit[] findAllElementaryCircuits(DiGraph graph) {
		if (graph == null){
			throw new IllegalArgumentException("The 'graph' cannot be null"); //$NON-NLS-1$
		}
		circuits = new ArrayList(0);
		
		origGraph = graph;
		int n = graph.numberOfVertices();
		blocked = new boolean[n];
		
		B = new HashMap(n);
		IVertex[] vertices = graph.getVertices();
		for (int ci = 1; ci <= n; ci++) {
			vertices[ci-1].setPropertyValue(INDEX, Integer.valueOf(ci));
			B.put(vertices[ci-1], new ArrayList(0));
		}
		
		stack.clear();
		s = 1;
		while (s < n) {
			Ak = null;
			
			// adjacency structure of strong component K with least
			// vertex in subgraph of G induced by {s, s+1, ..., n};
			int leastVertex = Integer.MAX_VALUE;
			DiGraph subgraph = createSubgraph(s, n);
			List[] sccs = sccAlg.findStronglyConnectedComponents(subgraph);
			for (int i = 0; i < sccs.length; i++) {
				List scc = sccs[i];
				Map adj = getAdjacencyStructureOfSCC(scc, subgraph);
				int t = indexOf(leastVertex(adj));
				if (t < leastVertex) {
					leastVertex = t;
					Ak = adj;
				}
			}
			
			if (Ak != null) {
				IVertex sVertex = leastVertex(Ak);
				s = indexOf(sVertex);
				
				Iterator keysItr = Ak.keySet().iterator();
				while (keysItr.hasNext()) {
					IVertex iVertex = (IVertex) keysItr.next();
					int i = indexOf(iVertex);
					blocked[i-1] = false;
					B.put(iVertex, new ArrayList(0));
				}
				
				circuit(sVertex);
				s++;
			}
			else {
				s = n;
			}
		}
		
		//TODO needs to be in a finally block
		// Cleanup
		vertices = graph.getVertices();
		for (int i = 0; i < vertices.length; i++) {
			IVertex v = vertices[i];
			v.removeProperty(INDEX);
		}
		
		return (IElementaryCircuit[]) circuits.toArray(new IElementaryCircuit[circuits.size()]);
	}

}
