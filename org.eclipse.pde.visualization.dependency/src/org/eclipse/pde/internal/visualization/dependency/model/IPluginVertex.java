package org.eclipse.pde.internal.visualization.dependency.model;

/**
 * Interface representing a graph vertex that adapts an underlying base or bundle description, 
 * or bundle specification model object (i.e. used to represent plug-in/fragment dependencies)
 */
public interface IPluginVertex extends IPDEDependencyVertex {
	
	/**
	 * Returns the name of the IPluginVertex
	 * @return the name of the IPluginVertex
	 */
	public String getName();
	
}
