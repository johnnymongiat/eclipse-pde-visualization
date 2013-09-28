package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.pde.internal.visualization.dependency.errors.ErrorReporting;
import org.eclipse.pde.internal.visualization.dependency.errors.UnresolvedError;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;
import org.eclipse.ui.IViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;

public interface IDependencyVisualizationView extends IViewPart {

	public DiGraph getDependencyGraph();
	
	public void setDependencyGraph(DiGraph graph);//TODO we should remove this...
	
	public GraphViewer getGraphViewer();
	
	public boolean isVersionNumbersShown();
	
	/**
	 * If true, the version number of the bundles will be displayed at the end
	 * the name. If false, this information will be hidden
	 * @param enable
	 */
	public void showVersionNumbers(boolean enable);
	
	public IElementaryCircuit[] getCircuits();
	public void focusOnCircuit(IElementaryCircuit circuit);
	public IElementaryCircuit getCurrentCircuit();
	
	/**
	 * Focuses on the specified {@link IVertex}.
	 * @param root - the not-null {@link IVertex} to focus on
	 * @param recordHistory - boolean used to set if history should be recorded
	 * @param errorReporting - the error that should be displayed or null 
	 */
	public void focusOn(IVertex root, boolean recordHistory, final ErrorReporting errorReporting);
	
	/**
	 * Returns the {@link NavigationHistoryManager}
	 * @return the {@link NavigationHistoryManager}
	 */
	public INavigationHistoryManager getNavigationHistoryManager();
	
	public String getHistoryEntryText(IVertex vertex);

	/**
	 * Handles the suppressing of an error
	 */
	public void handleSuppressError();

	/**
	 * Handles the showing of the specified <code>unresolvedError</code>
	 * @param unresolvedError - the error to be shown
	 */
	public void handleUnresolvedDependencyError(UnresolvedError unresolvedError);
		
}
