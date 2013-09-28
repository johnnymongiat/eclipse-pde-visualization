package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.ui.dialogs.FeatureSelectionDialog;
import org.eclipse.pde.internal.ui.editor.feature.FeatureEditor;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.model.FeatureDependencyHelper;
import org.eclipse.pde.internal.visualization.dependency.model.FeatureVertex;
import org.eclipse.pde.internal.visualization.dependency.model.IFeatureVertex;
import org.eclipse.pde.internal.visualization.dependency.model.IPDEDependencyVertex;
import org.eclipse.pde.internal.visualization.dependency.model.IPluginVertex;
import org.eclipse.pde.internal.visualization.dependency.model.PluginVertex;
import org.eclipse.pde.internal.visualization.dependency.views.PluginVisualizationView;
import org.eclipse.ui.PartInitException;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public final class FeatureVisualizationView extends AbstractDependencyVisualizationView {

	public static final String ID = "org.eclipse.pde.visualization.views.FeatureDependenciesView";
	
	public FeatureVisualizationView() {
		super();
	}
	
	protected CompositeLayoutAlgorithm getDefaultLayoutAlgorithm(IVertex root) {
		return new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] { 
				new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), 
				new FeatureVizRootShiftOnTop(LayoutStyles.NO_LAYOUT_NODE_RESIZING, root),
				new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
	}

	protected String getInitialFormText() {
		return PDEMessages.FEATURE_VIEW_TITLE;
	}

	protected AbstractDependencyVisualizationLabelProvider createDependencyVisualizationLabelProvider(
			AbstractInterestingDependenciesCalculator dependencyCalculator) {
		return new FeatureDependencyGraphLabelProvider(getGraphViewer(), this, dependencyCalculator);
	}

	protected DiGraph computeDependencyGraph(IVertex root) {
		return new FeatureDependencyHelper().createFeatureGraph((IFeatureVertex) root);
	}

	protected String computeFormText(IVertex root) {
		return PDEMessages.bind(PDEMessages.FEATURE_VIEW_TITLE_NODE, ((IFeatureVertex)root).getId());
	}

	protected void handleDoubleClickOf(IVertex selection) {
		if (selection instanceof IFeatureVertex) {
			focusOn(selection, true, null);
		}
		else if (selection instanceof IPluginVertex) {
			focusOnPlugin((PluginVertex) selection);
		}
	}

	protected Action createFocusDialogAction() {
		Action action = new Action() {
			public void run() {
				FeatureSelectionDialog dialog = new FeatureSelectionDialog(getSite().getShell(), 
						PDECore.getDefault().getFeatureModelManager().getModels(), false);
				if (dialog.open() == Window.OK) {
					IFeatureModel selectedModel = (IFeatureModel) dialog.getFirstResult();
					IFeature feature = selectedModel.getFeature();
					String id = feature.getId();
					String version = FeatureDependencyHelper.safeVersion(feature.getVersion());
					focusOn(new FeatureVertex(feature, id, version, feature), true, null);
				}
			}
		};
		action.setText(PDEMessages.FOCUS_ON);
		action.setToolTipText(PDEMessages.FOCUS_ON_FEATURE);
		return action;
	}

	protected Action createFocusOnContextMenuAction(final IPDEDependencyVertex vertex) {
		Action action = new Action() {
			public void run() {
				if (vertex instanceof IFeatureVertex) {
					focusOn(vertex, true, null);
				} else if (vertex instanceof IPluginVertex){
					focusOnPlugin((IPluginVertex) vertex);
				}
			}
		};
		action.setText(PDEMessages.bind(PDEMessages.FOCUS_ON_NODE, vertex.getName()));
		action.setImageDescriptor(PDEVizImages.DESC_FOCUS);
		return action;
	}
	
	protected Action createOpenEditorContextMenuAction(final IPDEDependencyVertex vertex) {
		Action action = new Action() {
			public void run() {
				if (vertex instanceof IFeatureVertex) {
					FeatureEditor.openFeatureEditor(((IFeatureVertex) vertex).getCorrespondingFeature());
				} else if (vertex instanceof IPluginVertex) {
					ManifestEditor.openPluginEditor(((IPluginVertex) vertex).getName());
				}
			}
		};
		
		if (vertex instanceof IFeatureVertex) {
			action.setText(PDEMessages.OPEN_IN_FEATURE_EDITOR);
			action.setToolTipText(PDEMessages.OPEN_IN_FEATURE_EDITOR);
		} else if (vertex instanceof IPluginVertex) {
			action.setText(PDEMessages.OPEN_IN_MANIFEST_EDITOR);
			action.setToolTipText(PDEMessages.OPEN_IN_MANIFEST_EDITOR);
		}
		return action;
	}

	private void focusOnPlugin(IPluginVertex vertex) {
		Object plugin = vertex.getModelObject();
		try {
			PluginVisualizationView view = (PluginVisualizationView) getSite().getPage().showView("org.eclipse.pde.visualization.views.SampleView"); //$NON-NLS-1$
			view.setFocusOn(plugin);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), PDEMessages.FEATURE_VIEW_NAME, 
					PDEMessages.ERROR_OPENING_VIEW, e.getStatus());
		}
	}
	
	public String getHistoryEntryText(IVertex vertex) {
		return (vertex instanceof IFeatureVertex ? ((IFeatureVertex)vertex).getId() : "");
	}
	
	/**
	 * Shifts the root node on top center in case if the graph has cycles.
	 * <p>
	 * Can be used only in a <code>CompositeLayoutAlgorithm</code>.
	 */
	private static final class FeatureVizRootShiftOnTop extends AbstractLayoutAlgorithm {
		
		private IVertex root;
		
		public FeatureVizRootShiftOnTop(int styles, IVertex root) {
			super(styles);
			if (root != null)
				this.root = root;
		}

		protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
			for (int i = 0; i < entitiesToLayout.length; i++) {
				InternalNode internalNode = entitiesToLayout[i];
				InternalNode layoutEntity = (InternalNode) internalNode.getLayoutEntity();
				IVertex rootVertex = (IVertex) ((GraphNode) layoutEntity.getLayoutEntity().getGraphData()).getData();
				if (root != null)
					if (root.equals(rootVertex)){
						internalNode.setInternalLocation(boundsWidth/2, 0);
					}
			}
			updateLayoutLocations(entitiesToLayout);
		}

		protected int getCurrentLayoutStep() {
			return 0;
		}
		
		protected int getTotalNumberOfLayoutSteps() {
			return 0;
		}
		
		protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
			return true;
		}

		protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
			// ignore
		}

		protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double x, double y, double width, double height) {
			// ignore
		}

		public void setLayoutArea(double x, double y, double width, double height) {
			// ignore
		}
	}

}
