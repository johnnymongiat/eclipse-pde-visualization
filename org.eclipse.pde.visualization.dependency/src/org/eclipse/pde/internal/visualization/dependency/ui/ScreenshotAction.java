package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.pde.internal.visualization.dependency.Activator;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.views.ImagePreviewPane;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;

/**
 * An Action that creates a button used to show a {@link ImagePreviewPane} 
 * to create a PNG image from the current graph
 */
public class ScreenshotAction extends Action {

	private IDependencyVisualizationView fView;
	
	public ScreenshotAction(IDependencyVisualizationView view) {
		setText(PDEMessages.SCREENSHOT);
		setImageDescriptor(PDEVizImages.DESC_SNAPSHOT);
		setToolTipText(PDEMessages.SCREENSHOT);
		fView = view;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		Graph g = (Graph) fView.getGraphViewer().getControl();
		Rectangle bounds = g.getContents().getBounds();
		Point size = new Point(g.getContents().getSize().width, g.getContents().getSize().height);
		org.eclipse.draw2d.geometry.Point viewLocation = g.getViewport().getViewLocation();
		final Image image = new Image(null, size.x, size.y);
		GC gc = new GC(image);
		SWTGraphics swtGraphics = new SWTGraphics(gc);

		swtGraphics.translate(-1 * bounds.x + viewLocation.x, -1 * bounds.y + viewLocation.y);
		g.getViewport().paint(swtGraphics);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		ImagePreviewPane previewPane = new ImagePreviewPane(shell);
		previewPane.setText(PDEMessages.IMAGE_PREVIEW);
		previewPane.open(image, size);
	}
}
