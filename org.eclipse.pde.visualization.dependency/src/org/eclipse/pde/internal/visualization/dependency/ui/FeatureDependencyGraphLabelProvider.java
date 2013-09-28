package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.ui.PDELabelProvider;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.pde.internal.ui.util.SharedLabelProvider;
import org.eclipse.pde.internal.ui.views.dependencies.DependenciesLabelProvider;
import org.eclipse.pde.internal.visualization.dependency.model.IPDEDependencyVertex;
import org.eclipse.pde.internal.visualization.dependency.model.PDEDependencyVertex;
import org.eclipse.pde.internal.visualization.dependency.model.UnresolvedModelVertex;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;

/**
 * The implementation of an {@link AbstractDependencyVisualizationLabelProvider} used for showing dependencies between features 
 */
public class FeatureDependencyGraphLabelProvider extends AbstractDependencyVisualizationLabelProvider {

	private final DependenciesLabelProvider pdeLabelProvider = new DependenciesLabelProvider(true);
		
	public FeatureDependencyGraphLabelProvider(GraphViewer viewer, IDependencyVisualizationView view,
			AbstractInterestingDependenciesCalculator dependencyCalculator) {
		super(viewer, view, dependencyCalculator);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider#dispose()
	 */
	public void dispose() {
		pdeLabelProvider.dispose();
		super.dispose();
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider#getBackgroundColour(java.lang.Object)
	 */
	public Color getBackgroundColour(Object entity) {
		if (entity instanceof UnresolvedModelVertex) {
			return getColorFromRegistry(PASTEL_RED);
		}
		return super.getBackgroundColour(entity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider#getForegroundColour(java.lang.Object)
	 */
	public Color getForegroundColour(Object entity) {
		if (getInterestingDependenciesCalculator().getInterestingEntities().contains(entity)){
			return getColorFromRegistry(BLACK);
		}
		if (entity instanceof UnresolvedModelVertex) {
			return getColorFromRegistry(WHITE);
		}
		return super.getForegroundColour(entity);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof EntityConnectionData) {
			return null;
		}
		Image img = null;
		
		if (element instanceof IPDEDependencyVertex){
			Object modelObject = ((PDEDependencyVertex) element).getModelObject();
			
			if (modelObject instanceof IFeature) {//root node
				img = pdeLabelProvider.getImage(((IFeature) modelObject).getModel());
			} else if (modelObject instanceof BundleSpecification) {
				if ( ((BundleSpecification) modelObject).isOptional() ){//change the flag to warning
					img = PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_PLUGIN_OBJ, SharedLabelProvider.F_OPTIONAL | SharedLabelProvider.F_WARNING);
				} else {
					img = pdeLabelProvider.getImage(modelObject);
				}
			} else {
				img = pdeLabelProvider.getImage(modelObject);
			}
		}
		return img;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider#getNodeHighlightColor(java.lang.Object)
	 */
	public Color getNodeHighlightColor(Object entity) {
		if (entity instanceof UnresolvedModelVertex) {
			return getColorFromRegistry(RED);
		}
		return super.getNodeHighlightColor(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof IPDEDependencyVertex){
			PDEDependencyVertex vertex = (PDEDependencyVertex) element;
			String text = vertex.getName();
			if (getDependencyVisualizationView().isVersionNumbersShown()) {
				return text + " " + PDELabelProvider.formatVersion(vertex.getVersion()); //$NON-NLS-1$
			}
			return text;
		}
		return ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.pde.internal.visualization.dependency.provisional.ui.AbstractDependencyVisualizationLabelProvider#getTooltip(java.lang.Object)
	 */
	public IFigure getTooltip(Object entity) {
		if (entity instanceof PDEDependencyVertex) {
			PDEDependencyVertex vertex = (PDEDependencyVertex) entity;
			return new Label(vertex.getName() + " (" + vertex.getVersion() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
	
}
