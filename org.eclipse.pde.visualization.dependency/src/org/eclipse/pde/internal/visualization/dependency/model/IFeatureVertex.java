package org.eclipse.pde.internal.visualization.dependency.model;

import org.eclipse.pde.internal.core.ifeature.IFeature;

/**
 * Interface representing a graph vertex that adapts an underlying 
 * PDE feature object (e.g. IFeature, IFeatureChild, IFeatureImport)
 * 
 * Every IFeatureVertex must also be associated with its corresponding 
 * IFeature model object 
 * 
 * <b>Note:</b> If the source feature object is an instance of IFeature, 
 * then the corresponding feature attribute must be this same object) 
 */
public interface IFeatureVertex extends IPDEDependencyVertex {
	
	/**
	 * Returns the id of the IFeatureVertex
	 * @return the id of the IFeatureVertex
	 */
	public String getId();
	
	/**
	 * Returns the version of the IFeatureVertex
	 * @return the version of the IFeatureVertex
	 */
	public String getVersion();

	/**
	 * Returns the corresponding IFeature model object
	 * @return the corresponding IFeature model object
	 */
	public IFeature getCorrespondingFeature();
	
}
