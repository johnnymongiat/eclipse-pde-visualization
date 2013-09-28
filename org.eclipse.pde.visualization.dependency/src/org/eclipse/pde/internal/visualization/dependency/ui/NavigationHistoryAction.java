package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * An action that implements an IMenuCreator used to show the back and forward buttons
 */
public class NavigationHistoryAction extends Action implements IMenuCreator {

	private static final int RESULTS_IN_DROP_DOWN = 10;
	
	private final INavigationHistoryManager fManager;
	private final boolean forward;
	private Menu fMenu;

	/**
	 * Constructor
	 * @param manager - the history manager 
	 * @param forward - boolean to check if it's a forward action
	 */
	public NavigationHistoryAction(INavigationHistoryManager manager, boolean forward) {
		super("", AS_DROP_DOWN_MENU); //$NON-NLS-1$
		Assert.isNotNull(manager);
		this.fManager = manager;
		this.forward = forward;
		if (forward) {
			setText(PDEMessages.FORWARD);
			setToolTipText(PDEMessages.GO_FORWARD);
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
			fManager.setForwardAction(this);
		} else {
			setText(PDEMessages.BACK);
			setToolTipText(PDEMessages.GO_BACK);
			setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
			fManager.setBackwardAction(this);
		}
		setMenuCreator(this);
	}

	public INavigationHistoryManager getNavigationHistoryManager() {
		return fManager;
	}

	public void update() {
		if (forward) {
			setEnabled(fManager.canForward());
		} else {
			setEnabled(fManager.canBackward());
		}
	}

	public void run() {
		if (forward) {
			fManager.forward();
		} else {
			fManager.backward();
		}
	}

	public void dispose() {
		if (fMenu != null) {
			fMenu.dispose();
			fMenu = null;
		}		
	}

	public Menu getMenu(Control parent) {
		if (fMenu != null) {
			fMenu.dispose();
		}
		fMenu = new Menu(parent);
		if (forward) {
			List list = fManager.getForwardSubList();
			IVertex[] elements = (IVertex[]) list.toArray(new IVertex[list.size()]);
			addEntriesToMenu(fMenu, elements);
		} else {
			List list = fManager.getBackwardSubList();
			IVertex[] elements = new IVertex[list.size()];
			int index = 0;
			for (int i = list.size()-1; i >= 0 ; i--){
				elements[index++] = (IVertex) list.get(i);
			}
			addEntriesToMenu(fMenu, elements);
		}
		return fMenu;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
	
	private void addEntriesToMenu(Menu menu, IVertex[] elements) {
		int min = Math.min(elements.length, RESULTS_IN_DROP_DOWN);
		for (int i = 0; i < min; i++) {
			NavigationHistoryEntryAction action = new NavigationHistoryEntryAction(elements[i]);
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(menu, -1);
		}
	}
	
	private class NavigationHistoryEntryAction extends Action {
		private final IVertex vertex;

		public NavigationHistoryEntryAction(IVertex vertex){
			super("", AS_PUSH_BUTTON); //$NON-NLS-1$
			this.vertex = vertex;
			setText(fManager.getDependencyVisualizationView().getHistoryEntryText(vertex));
		}

		public void run() {
			fManager.getDependencyVisualizationView().focusOn(vertex, true, null);
			fManager.updateActions();
		}
	}
}