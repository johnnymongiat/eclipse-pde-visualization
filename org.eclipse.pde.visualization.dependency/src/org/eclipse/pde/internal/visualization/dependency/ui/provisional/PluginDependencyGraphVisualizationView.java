package org.eclipse.pde.internal.visualization.dependency.ui.provisional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.ui.dialogs.PluginSelectionDialog;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.model.IPDEDependencyVertex;
import org.eclipse.pde.internal.visualization.dependency.model.IPluginVertex;
import org.eclipse.pde.internal.visualization.dependency.model.PluginVertex;
import org.eclipse.pde.internal.visualization.dependency.ui.AbstractDependencyVisualizationLabelProvider;
import org.eclipse.pde.internal.visualization.dependency.ui.AbstractDependencyVisualizationView;
import org.eclipse.pde.internal.visualization.dependency.ui.AbstractInterestingDependenciesCalculator;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public final class PluginDependencyGraphVisualizationView extends AbstractDependencyVisualizationView {

	public static final String ID = "org.eclipse.pde.visualization.views.PluginDependencyGraphVisualizationView";
	
	private Action showCalleesAction = null;
	private Action showCallersAction = null;
	private boolean isShowCalleesMode = true;
	
	public PluginDependencyGraphVisualizationView() {
		super();
	}
	
	protected CompositeLayoutAlgorithm getDefaultLayoutAlgorithm(IVertex root) {
		if (isShowCalleesMode) {
			return new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] { 
					new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), 
					new PluginVizRootShiftOnTop(LayoutStyles.NO_LAYOUT_NODE_RESIZING, root),
					new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
		}
		return new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] { 
				new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), 
				new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
		
	}

	protected String getInitialFormText() {
		return "Plug-in Dependency Analysis";
	}

	protected AbstractDependencyVisualizationLabelProvider createDependencyVisualizationLabelProvider(
			AbstractInterestingDependenciesCalculator dependencyCalculator) {
		return new PluginDependencyGraphLabelProvider(getGraphViewer(), this, dependencyCalculator);
	}

	protected DiGraph computeDependencyGraph(IVertex root) {
		IPluginVertex vertex = (IPluginVertex) root;
		PluginDependencyHelper helper = new PluginDependencyHelper();
		if (isShowCalleesMode) {
			return helper.createPluginDependencyGraph(vertex);
		}
		return helper.createReversedPluginDependencyGraph(vertex);
	}

	protected String computeFormText(IVertex root) {
		return ("Plug-in Dependency Analysis: " + ((IPluginVertex)root).getName());
	}

	protected void handleDoubleClickOf(IVertex selection) {
		if (selection instanceof IPluginVertex) {
			focusOn(selection, true, null);
		}
	}

	protected Action createFocusDialogAction() {
		Action action = new Action() {
			public void run() {
				PluginSelectionDialog dialog = new PluginSelectionDialog(getSite().getShell(), true, false);
				if (dialog.open() == Window.OK) {
					IPluginModelBase pluginModelBase = (IPluginModelBase) dialog.getFirstResult();
					focusOn(new PluginVertex(pluginModelBase.getBundleDescription()), true, null);
				}
			}
		};
		action.setText(PDEMessages.FOCUS_ON);
		action.setToolTipText("Focus on a plug-in");
		return action;
	}

	protected Action createFocusOnContextMenuAction(final IPDEDependencyVertex vertex) {
		Action action = new Action() {
			public void run() {
				if (vertex instanceof IPluginVertex){
					focusOn(vertex, true, null);
				}
			}
		};
		action.setText(PDEMessages.bind(PDEMessages.FOCUS_ON_NODE, vertex.getName()));
		action.setImageDescriptor(PDEVizImages.DESC_FOCUS);
		return action;
	}
	
	protected Action createOpenEditorContextMenuAction(final IPDEDependencyVertex vertex) {
		Action action = null;
		if (vertex instanceof IPluginVertex) {
			action = new Action() {
				public void run() {
					if (vertex instanceof IPluginVertex) {
						ManifestEditor.openPluginEditor(((IPluginVertex) vertex).getName());
					}
				}
			};
			action.setText(PDEMessages.OPEN_IN_MANIFEST_EDITOR);
			action.setToolTipText(PDEMessages.OPEN_IN_MANIFEST_EDITOR);
		}
		return action;
	}
	
	public String getHistoryEntryText(IVertex vertex) {
		return (vertex instanceof IPluginVertex ? ((IPluginVertex)vertex).getName() : "");
	}
	
	protected void configureAdditionalToolBarActions(IToolBarManager manager) {
		showCalleesAction = new CallDirectionAction(true);
		showCalleesAction.setDescription("Show Callees");
		showCalleesAction.setToolTipText("Show Callees");
		showCalleesAction.setImageDescriptor(PDEVizImages.DESC_CALLEES);
		showCalleesAction.setChecked(isShowCalleesMode);
		manager.appendToGroup(TB_GROUP_ADDITIONS, showCalleesAction);
		
		showCallersAction = new CallDirectionAction(false);
		showCallersAction.setDescription("Show Callers");
		showCallersAction.setToolTipText("Show Callers");
		showCallersAction.setImageDescriptor(PDEVizImages.DESC_CALLERS);
		showCallersAction.setChecked(!isShowCalleesMode);
		manager.appendToGroup(TB_GROUP_ADDITIONS, showCallersAction);
	}
	
	private final class CallDirectionAction extends Action {
		
		private final boolean calleeDirection;
		
		public CallDirectionAction(boolean calleeDirection) {
			super(null, AS_RADIO_BUTTON);
			this.calleeDirection = calleeDirection;
		}

		public void run() {
			if (isChecked()) {
				isShowCalleesMode = calleeDirection;
				focusOn(currentRootVertex, false, null);
			}
		}
	}

	/**
	 * Shifts the root node on top center in case if the graph has cycles.
	 * <p>
	 * Can be used only in a <code>CompositeLayoutAlgorithm</code>.
	 */
	private static final class PluginVizRootShiftOnTop extends AbstractLayoutAlgorithm {
		
		private IVertex root;
		
		public PluginVizRootShiftOnTop(int styles, IVertex root) {
			super(styles);
			if (root != null)
				this.root = root;
		}

		protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, 
				double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
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
