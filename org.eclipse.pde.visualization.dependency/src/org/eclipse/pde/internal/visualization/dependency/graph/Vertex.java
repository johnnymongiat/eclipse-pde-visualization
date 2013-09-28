package org.eclipse.pde.internal.visualization.dependency.graph;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Base implementation of an IVertex
 */
public class Vertex extends PlatformObject implements IVertex {

	private Map properties = new HashMap(0);
	
	public Vertex() {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.graph.IVertex#getPropertyValue(java.lang.String)
	 */
	public Object getPropertyValue(String propertyId) {
		return propertyId == null ? null : properties.get(propertyId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.graph.IVertex#setPropertyValue(java.lang.String, java.lang.Object)
	 */
	public Object setPropertyValue(String propertyId, Object propertyValue) {
		return propertyId == null ? null : properties.put(propertyId, propertyValue);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.graph.IVertex#removeProperty(java.lang.String)
	 */
	public Object removeProperty(String propertyId) {
		return propertyId == null ? null : properties.remove(propertyId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.graph.IVertex#clearProperties()
	 */
	public void clearProperties() {
		properties.clear();
	}

}
