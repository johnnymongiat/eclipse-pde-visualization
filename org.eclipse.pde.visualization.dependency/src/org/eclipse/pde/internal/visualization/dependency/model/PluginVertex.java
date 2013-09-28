package org.eclipse.pde.internal.visualization.dependency.model;

import org.eclipse.osgi.service.resolver.BaseDescription;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;

/**
 * Standard implementation of an IPluginVertex which accepts 
 * a non-null {@link BaseDescription}, {@link BundleDescription}, or {@link BundleSpecification} object
 */
public class PluginVertex extends PDEDependencyVertex implements IPluginVertex {

	/**
	 * A <code>graph</code> vertex that adapts an underlying base or bundle description, or bundle specification model object
	 * 
	 * @param modelObject - a non-null BaseDescription, BundleDescription or BundleSpecification object 
	 * @throws IllegalArgumentException if the specified element is null 
	 * or if the modelObject is not a instance of BaseDescription or BundleSpecification
	 */
	public PluginVertex(Object modelObject) {
		if (modelObject == null) {
			throw new IllegalArgumentException("The 'modelObject' cannot be null."); //$NON-NLS-1$
		}
		if (!(modelObject instanceof BaseDescription || modelObject instanceof BundleSpecification)) {
			throw new IllegalArgumentException("The 'modelObject' must be of type 'BaseDescription' or 'BundleSpecification'."); //$NON-NLS-1$
		}
		this.modelObject = modelObject;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.PDEDependencyVertex#getName()
	 */
	public String getName() {
		if (modelObject instanceof BaseDescription) {
			return ((BaseDescription) modelObject).getName();
		}
		return ((BundleSpecification) modelObject).getName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.PDEDependencyVertex#getVersion()
	 */
	public String getVersion() {
		if (modelObject instanceof BundleDescription) {
			BundleDescription bundleDescription = ((BundleDescription) modelObject);
			return bundleDescription.getVersion().toString();
		}
		else if (modelObject instanceof BundleSpecification) {
			BundleSpecification bundleSpecification = (BundleSpecification) modelObject;
			return bundleSpecification.getBundle().getVersion().toString();
		}
		return ""; //$NON-NLS-1$
	}
}
