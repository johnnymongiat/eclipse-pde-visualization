package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.List;

import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * An interface for the navigation history manager
 */
public interface INavigationHistoryManager {
	
	/**
	 * Adds the <code>vertex</code> to the history manager
	 * @param vertex - the vertex to be added to the history manager
	 */
	public void add(IVertex vertex);
	
	/**
	 * Goes to the previous element in the history manager
	 */
	public void backward();
	
	/**
	 * Returns if there is a previous element in the history manager
	 * @return if there is a previous element in the history manager
	 */
	public boolean canBackward();

	/**
	 * Returns if there is a next element in the history manager
	 * @return if there is a next element in the history manager
	 */
	public boolean canForward();
	
	/**
	 * Clears the history manager
	 */
	public void clear();
	
	/**
	 * Disposes the history manager
	 */
	public void dispose();
	
	/**
	 * Goes to the next element in the history manager 
	 */
	public void forward();
	
	/**
	 * Returns the current element from the history manager
	 * @return the current element from the history manager
	 */
	public IVertex getCurrent();
	
	/**
	 * Returns the view
	 * @return the view
	 */
	public IDependencyVisualizationView getDependencyVisualizationView();
	
	/**
	 * Sets the backward action
	 * @param action - the action to be set as the backward action
	 */
	public void setBackwardAction(NavigationHistoryAction action);
	
	/**
	 * Sets the forward action
	 * @param action - the action to be set as the forward action
	 */
	public void setForwardAction(NavigationHistoryAction action);
	
	/**
	 * Returns the sublist of elements from the current element to the end of the list
	 * @return the sublist of elements from the current element to the end of the list
	 */
	public List getForwardSubList();

	/**
	 * Returns the sublist of elements from the current element to the beginning of the list
	 * @return the sublist of elements from the current element to the beginning of the list
	 */
	public List getBackwardSubList();

	/**
	 * Updates the actions
	 */
	public void updateActions();
}
