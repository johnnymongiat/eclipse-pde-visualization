package org.eclipse.pde.internal.visualization.dependency.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;

/**
 * An Action that implements an IMenuCreator to show a drop-down menu 
 * with options to show dependencies between the root vertex and the selected one  
 */
public class PathsDropDownAction extends Action implements IMenuCreator {
	
	private final AbstractDependencyVisualizationLabelProvider fLabelProvider;
	private final ShortestPathAction shortestPathAction;
	private final AllPathsAction allPathsAction;
	
	private Menu fMenu = null;
	
	public PathsDropDownAction(AbstractDependencyVisualizationLabelProvider labelProvider) {
		super(PDEMessages.SHOW_DEPENDENCY_PATH, PDEVizImages.DESC_LOOP_NODE_OBJ);
		fLabelProvider = labelProvider;
		setMenuCreator(this);
		
		shortestPathAction = new ShortestPathAction(fLabelProvider);
		allPathsAction = new AllPathsAction(fLabelProvider);
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
		
		boolean marked = false;
		marked = marked || shortestPathAction.isChecked();
		shortestPathAction.setChecked(shortestPathAction.isChecked());
		ActionContributionItem item = new ActionContributionItem(shortestPathAction);
		item.fill(fMenu, -1);
		
		marked = marked || allPathsAction.isChecked();
		allPathsAction.setChecked(allPathsAction.isChecked());
		item = new ActionContributionItem(allPathsAction);
		item.fill(fMenu, -1);
		
		new Separator().fill(fMenu, -1);
		Action noneAction = new Action(PDEMessages.NONE, AS_RADIO_BUTTON) {
			public void run() {
				fLabelProvider.updateInterestingDependenciesCalculator(new DefaultInterestingDependenciesCalculator());
				shortestPathAction.setChecked(false);
				allPathsAction.setChecked(false);
			}
		};
		noneAction.setChecked(!marked);
		item = new ActionContributionItem(noneAction);
		item.fill(fMenu, -1);			
		
		return fMenu;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}
	
	public void runWithEvent(Event event) {
		if (event.widget instanceof ToolItem) {
			ToolItem item = (ToolItem) event.widget;
			Control control = item.getParent();
			Menu menu = getMenu(control);

			Rectangle bounds = item.getBounds();
			Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
			menu.setLocation(control.toDisplay(topLeft));
			menu.setVisible(true);
		}
	}
}