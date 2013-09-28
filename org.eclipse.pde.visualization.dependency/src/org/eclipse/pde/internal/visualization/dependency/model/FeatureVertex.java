package org.eclipse.pde.internal.visualization.dependency.model;

import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureObject;

/**
 * Standard implementation of an IFeatureVertex which accepts 
 * a non-null IFeatureObject, id, version, and corresponding IFeature
 */
public final class FeatureVertex extends PDEDependencyVertex implements IFeatureVertex {

	private IFeature correspondingFeature = null;
	private String id;
	private String version;
	
	/**
	 * Default constructor
	 * <p>
	 * Constructs a FeatureVertex with the specified featureObject, id, version and correspondingFeature.
	 * </p>
	 *
	 * <b>Note:</b> the specified elements cannot be null.
	 * 
	 * @param featureObject - the underlying PDE feature object (e.g. IFeature, IFeatureChild, IFeatureImport)
	 * @param id - the id of the underlying PDE feature object
	 * @param version - the version of the underlying PDE feature object
	 * @param correspondingFeature - the corresponding IFeature model object
	 * 
	 * @throws IllegalArgumentException if one of the specified elements is null 
	 * or if the featureObject and correspondingFeature are not the same object
	 */
	public FeatureVertex(IFeatureObject featureObject, String id, String version, IFeature correspondingFeature) {
		super();
		if (featureObject == null) {
			throw new IllegalArgumentException("The 'featureObject' cannot be null."); //$NON-NLS-1$
		}
		if (id == null) {
			throw new IllegalArgumentException("The 'id' cannot be null."); //$NON-NLS-1$
		}
		if (version == null) {
			throw new IllegalArgumentException("The 'version' cannot be null."); //$NON-NLS-1$
		}
		if (correspondingFeature == null) {
			throw new IllegalArgumentException("The 'correspondingFeature' cannot be null."); //$NON-NLS-1$
		}
		if ((featureObject instanceof IFeature) && (featureObject != correspondingFeature)) {
			throw new IllegalArgumentException("The 'featureObject' and 'correspondingFeature' must be the same."); //$NON-NLS-1$
		}
		this.modelObject = featureObject;
		this.id = id;
		this.version = version;
		this.correspondingFeature = correspondingFeature;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.IFeatureVertex#getCorrespondingFeature()
	 */
	public IFeature getCorrespondingFeature() {
		return correspondingFeature;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.IFeatureVertex#getId()
	 */
	public String getId() {
		return id;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.PDEDependencyVertex#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.PDEDependencyVertex#getName()
	 */
	public String getName() {
		return getId();
	}
	
}
