package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;

/**
 * An Action that creates a toggle button for showing the version
 */
public class ShowVersionNumbersAction extends Action {

	private IDependencyVisualizationView fView;
	
	public ShowVersionNumbersAction(IDependencyVisualizationView view) {
		super("", AS_CHECK_BOX); //$NON-NLS-1$
		setImageDescriptor(PDEVizImages.DESC_SHOW_VERSION);
		setToolTipText(PDEMessages.SHOW_VERSIONS);
		setDescription(PDEMessages.SHOW_VERSIONS);
		fView = view;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		fView.showVersionNumbers(!fView.isVersionNumbersShown());
	}
	
}
