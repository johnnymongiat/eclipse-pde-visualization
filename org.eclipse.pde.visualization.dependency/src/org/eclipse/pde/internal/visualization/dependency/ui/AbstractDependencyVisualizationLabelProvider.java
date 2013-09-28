package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

public abstract class AbstractDependencyVisualizationLabelProvider implements ILabelProvider, IConnectionStyleProvider, IEntityStyleProvider {

	public static final RGB GRAY = new RGB(128, 128, 128);
	public static final RGB LIGHT_GRAY = new RGB(220, 220, 220);
	public static final RGB BLACK = new RGB(0, 0, 0);
	public static final RGB RED = new RGB(255, 0, 0);
	public static final RGB DARK_RED = new RGB(127, 0, 0);
	public static final RGB PASTEL_RED = new RGB(255, 105, 97);
	public static final RGB LIGHT_GREEN = new RGB(96, 255, 96);
	public static final RGB DISABLED = new RGB(225, 238, 255);
	public static final RGB WHITE = new RGB(255, 255, 255);
	
	private IDependencyVisualizationView fView;
	private GraphViewer fViewer;
	private AbstractInterestingDependenciesCalculator fDependencyCalculator;
	private IVertex rootVertex = null;
	private IVertex selectedVertex = null;
	private IVertex pinnedVertex = null;
	private Color disabledColor = null;

	public AbstractDependencyVisualizationLabelProvider(GraphViewer viewer, IDependencyVisualizationView view, 
			AbstractInterestingDependenciesCalculator dependencyCalculator) {
		Assert.isNotNull(viewer);
		Assert.isNotNull(view);
		Assert.isNotNull(dependencyCalculator);
		fViewer = viewer;
		fView = view;
		fDependencyCalculator = dependencyCalculator;
	}
	
	/**
	 * Returns the color associated with the given RGB from the registry, or null if no such definition exists.
	 * @param rgb - the RGB color code
	 * @return the color associated with the given RGB from the registry, or null if no such definition exists.
	 */
	public static final Color getColorFromRegistry(RGB rgb) {
		StringBuffer sb = new StringBuffer();
		sb.append(rgb.red);
		sb.append("-"); //$NON-NLS-1$
		sb.append(rgb.green);
		sb.append("-"); //$NON-NLS-1$
		sb.append(rgb.blue);
		String key = sb.toString();
		ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
		if (!colorRegistry.hasValueFor(key)) {
			colorRegistry.put(key, rgb);
		}
		return colorRegistry.get(key);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		fView = null;
		fViewer = null;
		rootVertex = null;
		selectedVertex = null;
		pinnedVertex = null;
		fDependencyCalculator.clear();
		fDependencyCalculator = null;
		disabledColor = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#fisheyeNode(java.lang.Object)
	 */
	public boolean fisheyeNode(Object entity) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBackgroundColour(java.lang.Object)
	 */
	public Color getBackgroundColour(Object entity) {
		if (entity.equals(rootVertex)) {
			return getColorFromRegistry(LIGHT_GREEN);
		}
		if (entity.equals(selectedVertex) || entity.equals(pinnedVertex)) {
			return fViewer.getGraphControl().DEFAULT_NODE_COLOR;
		} else if (fDependencyCalculator.getInterestingEntities().contains(entity)) {
			return fViewer.getGraphControl().HIGHLIGHT_ADJACENT_COLOR;
		}
		return getDisabledColor();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderColor(java.lang.Object)
	 */
	public Color getBorderColor(Object entity) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderHighlightColor(java.lang.Object)
	 */
	public Color getBorderHighlightColor(Object entity) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getBorderWidth(java.lang.Object)
	 */
	public int getBorderWidth(Object entity) {
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getColor(java.lang.Object)
	 */
	public Color getColor(Object rel) {
		return (fDependencyCalculator.getInterestingRelationships().contains(rel) ? 
				getColorFromRegistry(DARK_RED) : getColorFromRegistry(LIGHT_GRAY));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getConnectionStyle(java.lang.Object)
	 */
	public int getConnectionStyle(Object rel) {
		return (fDependencyCalculator.getInterestingRelationships().contains(rel) ? 
				ZestStyles.CONNECTIONS_DASH | ZestStyles.CONNECTIONS_DIRECTED : ZestStyles.CONNECTIONS_DIRECTED);
	}
	
	/**
	 * Returns the {@link IDependencyVisualizationView} associated with this label provider 
	 * @return the {@link IDependencyVisualizationView} associated with this label provider 
	 */
	public IDependencyVisualizationView getDependencyVisualizationView() {
		return fView;
	}
	
	/**
	 * Returns the disabled color.
	 * @return the disabled color
	 */
	private Color getDisabledColor() {
		if (disabledColor == null) {
			disabledColor = getColorFromRegistry(DISABLED);
		}
		return disabledColor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getForegroundColour(java.lang.Object)
	 */
	public Color getForegroundColour(Object entity) {
		if (selectedVertex != null || pinnedVertex != null) {
			if (entity.equals(selectedVertex) || entity.equals(pinnedVertex)) {
				return getColorFromRegistry(BLACK);
			} else if (fDependencyCalculator.getInterestingEntities().contains(entity)) {
				return getColorFromRegistry(BLACK);
			} 
			return getColorFromRegistry(GRAY);
		}
		return getColorFromRegistry(BLACK);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getHighlightColor(java.lang.Object)
	 */
	public Color getHighlightColor(Object rel) {
		return getColorFromRegistry(DARK_RED);
	}

	/**
	 * Returns the {@link AbstractInterestingDependenciesCalculator} associated with this label provider
	 * @return the {@link AbstractInterestingDependenciesCalculator} associated with this label provider
	 */
	public AbstractInterestingDependenciesCalculator getInterestingDependenciesCalculator() {
		return fDependencyCalculator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getLineWidth(java.lang.Object)
	 */
	public int getLineWidth(Object rel) {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IEntityStyleProvider#getNodeHighlightColor(java.lang.Object)
	 */
	public Color getNodeHighlightColor(Object entity) {
		return null;
	}
	
	/**
	 * Returns the pinned vertex
	 * @return the pinned vertex
	 */
	public IVertex getPinnedVertex() {
		return pinnedVertex;
	}
	
	/**
	 * Returns the root vertex
	 * @return the root vertex
	 */
	public IVertex getRootVertex() {
		return rootVertex;
	}

	/**
	 * Returns the selected vertex if there are no pinned vertices or the pinned vertex
	 * @return the selected vertex if there are no pinned vertices or the pinned vertex
	 */
	public IVertex getSelected() {
		if (pinnedVertex != null) {
			return pinnedVertex;
		}
		return selectedVertex;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IConnectionStyleProvider#getTooltip(java.lang.Object)
	 */
	public IFigure getTooltip(Object entity) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}
	
	/**
	 * Sets the current selection
	 * @param root - the root vertex
	 * @param selected - the selected vertex
	 */
	public void setCurrentSelection(IVertex root, IVertex selected) {
		internalUnrevealConnections();
		rootVertex = root;
		selectedVertex = selected;
		internalUpdateViewer();
	}

	/**
	 * Sets the pinned vertex
	 * @param pinnedVertex - the vertex to be pinned
	 */
	public void setPinnedVertex(IVertex pinnedVertex) {
		this.pinnedVertex = pinnedVertex;
	}
	
	/**
	 * Sets the <code>AbstractInterestingDependenciesCalculator</code> to be used 
	 * @param dependencyCalculator - the <code>AbstractInterestingDependenciesCalculator</code> to be set
	 */
	public void updateInterestingDependenciesCalculator(AbstractInterestingDependenciesCalculator dependencyCalculator) {
		Assert.isNotNull(dependencyCalculator);
		internalUnrevealConnections();
		fDependencyCalculator = dependencyCalculator;
		internalUpdateViewer();
	}

	/**
	 * UnReveals all the connections
	 */
	private void internalUnrevealConnections() {
		for (Iterator iter = fDependencyCalculator.getInterestingRelationships().iterator(); iter.hasNext();) {
			EntityConnectionData entityConnectionData = (EntityConnectionData) iter.next();
			fViewer.unReveal(entityConnectionData);
		}
	}
	
	/**
	 * Updates the viewer nodes and connections
	 */
	private void internalUpdateViewer() {
		fDependencyCalculator.calculate(this);
		
		Object[] nodes = fViewer.getNodeElements();
		for (int i = 0; i < nodes.length; i++) {
			fViewer.update(nodes[i], null);
		}

		for (Iterator iter = fDependencyCalculator.getInterestingRelationships().iterator(); iter.hasNext();) {
			Object entityConnectionData = iter.next();
			fViewer.reveal(entityConnectionData);
		}

		Object[] connections = fViewer.getConnectionElements();
		for (int i = 0; i < connections.length; i++) {
			fViewer.update(connections[i], null);
		}
	}
	
}
