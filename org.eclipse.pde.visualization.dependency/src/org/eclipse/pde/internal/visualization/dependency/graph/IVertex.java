package org.eclipse.pde.internal.visualization.dependency.graph;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Interface representing a graph vertex.
 * <p>Allows key-object associations to be stored per instance </p>
 */
public interface IVertex extends IAdaptable {

	/**
	 * Returns the value to which the specified <code>propertyId</code> is mapped, or <code>null</code> if this map contains no mapping for the <code>propertyId</code>. 
	 * @param propertyId - the id whose associated value is to be returned 
	 * @return the value to which the specified key is mapped, or <code>null</code> if this map contains no mapping for the key 
	 */
	public Object getPropertyValue(String propertyId);

	/**
	 * Associates the specified value with the specified id in this map
	 * @param propertyId - id with which the specified value is to be associated
	 * @param propertyValue - value to be associated with the specified <code>propertyId</code>
	 * @return the previous value associated with <code>propertyId</code>, or <code>null</code> if there was no mapping for <code>propertyId</code>
	 */
	public Object setPropertyValue(String propertyId, Object propertyValue);

	/**
	 * Removes the mapping for a key from this map if it is present 
	 * <p>Returns the value to which this map previously associated the id, or null if the map contained no mapping for the id.</p>
	 * @param propertyId - id whose mapping is to be removed from the map
	 * @return the value to which this map previously associated the <code>propertyId</code>, or <code>null</code> if the map contained no mapping for the <code>propertyId</code>. 
	 */
	public Object removeProperty(String propertyId);

	/**
	 * Clears the properties of the <code>IVertex</code>
	 */
	public void clearProperties();

}