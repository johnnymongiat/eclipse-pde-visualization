package org.eclipse.pde.internal.visualization.dependency.model;

import org.eclipse.pde.internal.visualization.dependency.graph.Vertex;

/**
 * Standard implementation of an IPDEDependencyVertex
 */
public abstract class PDEDependencyVertex extends Vertex implements IPDEDependencyVertex {

	protected Object modelObject;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.IPDEDependencyVertex#getModelObject()
	 */
	public Object getModelObject() {
		return modelObject;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.IPDEDependencyVertex#getName()
	 */
	public abstract String getName();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.IPDEDependencyVertex#getVersion()
	 */
	public abstract String getVersion();
}
