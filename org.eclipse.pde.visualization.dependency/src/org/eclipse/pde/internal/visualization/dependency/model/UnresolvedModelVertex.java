package org.eclipse.pde.internal.visualization.dependency.model;

/**
 * Vertex representing a model object that could not be found in pde model (for example, configured version could not be matched);
 * Every UnresolvedModelVertex must be of type: UnresolvedModelVertex.TYPE_FEATURE, UnresolvedModelVertex.TYPE_PLUGIN, 
 * or UnresolvedModelVertex.TYPE_FRAGMENT
 */
public class UnresolvedModelVertex extends PDEDependencyVertex {

	public static final int TYPE_FEATURE = 0x1;
	public static final int TYPE_PLUGIN = 0x2;
	public static final int TYPE_FRAGMENT = 0x4;
	
	private String id;
	private String version;
	private int type;
	
	/**
	 * Creates a <code>UnresolvedModelVertex</code> with the specified modelObject, id, version and type.
	 *
	 * <b>Note:</b> the specified elements cannot be null.
	 * 
	 * @param modelObject - the underlying PDE feature object
	 * @param id - the <code>id</code> of the underlying PDE feature object
	 * @param version - the <code>version</code> of the underlying PDE feature object
	 * @param type - the type of <code>UnresolvedModelVertex</code> ({@link #TYPE_FEATURE}, {@link #TYPE_PLUGIN} or {@link #TYPE_FRAGMENT})
	 * 
	 * @throws IllegalArgumentException if one of the specified elements is null 
	 * or if the type is different than {@link #TYPE_FEATURE}, {@link #TYPE_PLUGIN} or {@link #TYPE_FRAGMENT}
	 */
	public UnresolvedModelVertex(Object modelObject, String id, String version, int type){
		super();
		if (modelObject == null) {
			throw new IllegalArgumentException("The 'modelObject' cannot be null."); //$NON-NLS-1$
		}
		if (id == null) {
			throw new IllegalArgumentException("The 'id' cannot be null."); //$NON-NLS-1$
		}
		if (version == null) {
			throw new IllegalArgumentException("The 'version' cannot be null."); //$NON-NLS-1$
		}
		if (type != TYPE_FEATURE && type != TYPE_PLUGIN && type != TYPE_FRAGMENT) {
			throw new IllegalArgumentException("The 'type' must be either TYPE_FEATURE, TYPE_PLUGIN, or TYPE_FRAGMENT."); //$NON-NLS-1$
		}
		
		this.modelObject = modelObject;
		this.id = id;
		this.version = version;
		this.type = type;
	}

	/**
	 * Returns the id of the <code>UnresolvedModelVertex</code>.
	 * @return the id of the <code>UnresolvedModelVertex</code>.
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

	/**
	 * Returns the type of the <code>UnresolvedModelVertex</code>.
	 * @return the type of the <code>UnresolvedModelVertex</code>.
	 */
	public int getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.features.PDEDependencyVertex#getName()
	 */
	public String getName() {
		return getId();
	}
}
