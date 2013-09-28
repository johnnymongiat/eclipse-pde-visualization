/*******************************************************************************
 * Copyright (c) 2009 EclipseSource Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.visualization.dependency.errors;

import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.model.UnresolvedModelVertex;
import org.eclipse.pde.internal.visualization.dependency.ui.IDependencyVisualizationView;

public class CurrentError extends ErrorReporting {

	ErrorReporting currentError;
	
	public CurrentError(IDependencyVisualizationView view, UnresolvedModelVertex vertex, ErrorReporting currentError) {
		super(view, vertex);
		this.currentError = currentError;
	}
	
	/**
	 * Returns the current error's message with a suppress message
	 * @return the current error's message with a suppress message
	 */
	public String getErrorMessage() {
		return PDEMessages.bind(PDEMessages.SUPPRESS, currentError.getErrorMessage());
	}

	/**
	 * Method that handles the filtering of the graph
	 */
	public void handleError() {
		this.view.handleSuppressError();
	}

}
