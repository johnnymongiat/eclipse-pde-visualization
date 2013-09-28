package org.eclipse.pde.internal.visualization.dependency.model;

import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Interface representing a graph vertex that adapts a PDE model object. 
 */
public interface IPDEDependencyVertex extends IVertex {
	
	/**
	 * Returns the modelObject of the IPDEDependencyVertex
	 * @return the modelObject of the IPDEDependencyVertex
	 */
	public Object getModelObject();
	
	/**
	 * Returns the name of the IPDEDependencyVertex
	 * @return the name of the IPDEDependencyVertex
	 */
	public String getName();
		
	/**
	 * Returns the version of the IPDEDependencyVertex
	 * @return the version of the IPDEDependencyVertex
	 */
	public String getVersion();
}
