/*******************************************************************************
 * Copyright 2005-2006, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria IBM CAS, IBM Toronto
 * Lab
 ******************************************************************************/
package org.eclipse.pde.internal.visualization.dependency.errors;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.model.UnresolvedModelVertex;
import org.eclipse.pde.internal.visualization.dependency.ui.IDependencyVisualizationView;
import org.eclipse.ui.forms.IMessageManager;

public abstract class ErrorReporting {

	protected UnresolvedModelVertex vertex;
	protected IDependencyVisualizationView view; 
	
	public ErrorReporting(IDependencyVisualizationView view, UnresolvedModelVertex vertex) {
		this.view = view;
		this.vertex = vertex;
	}

	/**
	 * Returns the current error <code>vertex</code>
	 * @return the current error <code>vertex</code>
	 */
	public UnresolvedModelVertex getVertex() {
		return this.vertex;
	}
	
	public abstract void handleError();
	public abstract String getErrorMessage();
	
	/**
	 * Creates the error reports
	 * @param elements the vertices to be analyzed
	 * @param view the view
	 * @param manager the message manager
	 */
	public static void createErrorReports(IVertex[] elements, IDependencyVisualizationView view, IMessageManager manager) {
		manager.removeAllMessages();
		
		for (int i = 0; i < elements.length; i++) {
			IVertex element = elements[i];
			UnresolvedError.containsError(element, view, manager);
		}
	}
	
	/**
	 * Shows the current selected error
	 * @param view the view
	 * @param currentError the current error
	 * @param manager the message manager
	 */
	public static void showCurrentError(IDependencyVisualizationView view, ErrorReporting currentError, IMessageManager manager) {
		manager.removeAllMessages();
		CurrentError currentErrorWrapper = new CurrentError(view, currentError.getVertex(), currentError);
		manager.addMessage(currentErrorWrapper, currentErrorWrapper.getErrorMessage(), currentErrorWrapper, IMessageProvider.INFORMATION);
		
	}
	
}
