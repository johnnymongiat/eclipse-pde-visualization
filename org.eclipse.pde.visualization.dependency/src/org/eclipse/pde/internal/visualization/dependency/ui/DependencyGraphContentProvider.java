package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

/**
 * The content provider used to provide the content for the UI 
 */
public class DependencyGraphContentProvider implements IGraphEntityContentProvider {

	private final IDependencyVisualizationView fView;
	private static final IVertex[] EMPTY_VERTICES = new IVertex[0];

	public DependencyGraphContentProvider(IDependencyVisualizationView view) {
		if (view == null) {
			throw new IllegalArgumentException("The 'view' parameter cannot be null."); //$NON-NLS-1$
		}
		fView = view;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphEntityContentProvider#getConnectedTo(java.lang.Object)
	 */
	public Object[] getConnectedTo(Object entity) {
		if (fView.getDependencyGraph() != null && entity instanceof IVertex) {
			return fView.getDependencyGraph().getConnectedTo((IVertex) entity);
		}
		return EMPTY_VERTICES;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphEntityContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof DiGraph) {
			return ((DiGraph) inputElement).getVertices();
		}
		return EMPTY_VERTICES;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof DiGraph) {
			fView.setDependencyGraph((DiGraph) newInput);
		}
	}
	
}