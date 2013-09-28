package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;
import org.eclipse.zest.core.viewers.EntityConnectionData;

/**
 * A ViewerFilter used to show only the nodes of a circuit  
 */
public class CircuitViewerFilter extends ViewerFilter {

	private final IElementaryCircuit fCircuit;
	
	public CircuitViewerFilter(IElementaryCircuit circuit) {
		fCircuit = circuit;
	}
	
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (fCircuit == null) {
			return false;
		}
		if (element instanceof IVertex) {
			return fCircuit.indexOf((IVertex) element) != -1;
		}
		if (element instanceof EntityConnectionData) {
			EntityConnectionData conn = (EntityConnectionData) element;
			int si = fCircuit.indexOf((IVertex) conn.source);
			int di = fCircuit.indexOf((IVertex) conn.dest);
			if (si != -1 && di != -1 && ((di - si == 1) || (di == 0 && si == fCircuit.size() - 1))) {
				return true;
			}
		}
		return false;
	}
	
}