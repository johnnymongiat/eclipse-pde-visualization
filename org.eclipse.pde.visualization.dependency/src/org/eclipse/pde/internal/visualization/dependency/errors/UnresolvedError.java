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
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.model.UnresolvedModelVertex;
import org.eclipse.pde.internal.visualization.dependency.ui.IDependencyVisualizationView;
import org.eclipse.ui.forms.IMessageManager;

public class UnresolvedError extends ErrorReporting {

	public UnresolvedError(IDependencyVisualizationView view, UnresolvedModelVertex vertex) {
		super(view, vertex);
	}

	/**
	 * Handle the error. In this case we should filter the nodes that are not part of the problem
	 */
	public void handleError() {
		view.handleUnresolvedDependencyError(this);
	}
	
	/**
	 * If element is instance of UnresolvedModelVertex than we show an error
	 * @param element <code>vertex</code> to be analyzed
	 * @param view the <code>view</code> 
	 * @param manager the message manager
	 */
	public static void containsError(IVertex element, IDependencyVisualizationView view, IMessageManager manager) {
		if (element instanceof UnresolvedModelVertex) {
			UnresolvedModelVertex errorVertex = (UnresolvedModelVertex) element;
			UnresolvedError error = new UnresolvedError(view, errorVertex);
			manager.addMessage(error, error.getErrorMessage(), error, IMessageProvider.ERROR);
		}
	}

	/**
	 * Returns the error message
	 * @return the error message
	 */
	public String getErrorMessage() {
		return PDEMessages.bind(PDEMessages.UNRESOLVED, (vertex.getType() == UnresolvedModelVertex.TYPE_FEATURE ? PDEMessages.FEATURE : PDEMessages.BUNDLE), vertex.getId());
	}

}
