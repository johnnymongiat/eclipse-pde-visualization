package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;

/**
 * An Action that implements an IMenuCreator, that is used to show a drop-down menu containing all the circuits
 */
public class CircuitsDropDownAction extends Action implements IMenuCreator {
	
	private static final int RESULTS_IN_DROP_DOWN = 10;
		
	private final IDependencyVisualizationView fView;
	private Menu fMenu = null;
	
	public CircuitsDropDownAction(IDependencyVisualizationView view) {
		super();
		fView = view;
		setToolTipText(PDEMessages.SHOW_CYCLES);
		setImageDescriptor(PDEVizImages.DESC_LOOP_OBJ);
		setMenuCreator(this);
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
		
		// Sort the circuits by increasing size.
		final List circuits = Arrays.asList(fView.getCircuits());
		Collections.sort(circuits, new Comparator() {
			public int compare(Object o1, Object o2) {
				IElementaryCircuit c1 = (IElementaryCircuit) o1;
				IElementaryCircuit c2 = (IElementaryCircuit) o2;
				return c1.size() - c2.size();
			}
		});
		
		// Add a corresponding action for each circuit.
		boolean marked = false;
		int min = Math.min(circuits.size(), RESULTS_IN_DROP_DOWN);
		for (int i = 0; i < circuits.size(); i++) {
			IElementaryCircuit circuit = (IElementaryCircuit) circuits.get(i);
			CircuitAction action = new CircuitAction(fView, circuit, PDEMessages.CIRCUIT + (i+1));
			boolean checked = circuit.equals(fView.getCurrentCircuit());
			marked = marked || checked;
			action.setChecked(checked);
			if (i < min){
				ActionContributionItem item = new ActionContributionItem(action);
				item.fill(fMenu, -1);
			}
		}
		if (circuits.size() > RESULTS_IN_DROP_DOWN){// Add the "More" action.
			final CircuitListDialog dialog = new CircuitListDialog(fView.getSite().getShell(), circuits, (IElementaryCircuit) circuits.get(RESULTS_IN_DROP_DOWN));
	
			Action moreAction = new Action(PDEMessages.MORE, IAction.AS_PUSH_BUTTON) {
				public void run() {
					if (dialog.open() == Window.OK) {
						Object[] selectedCircuits = dialog.getResult();
						if (selectedCircuits.length != 0){
							fView.focusOnCircuit((IElementaryCircuit) selectedCircuits[0]);
						}
					}
				}
			};
			ActionContributionItem item = new ActionContributionItem(moreAction);
			item.fill(fMenu, -1);
		}
		
		// Add the "None" action.
		new Separator().fill(fMenu, -1);
		Action noneAction = new Action(PDEMessages.NONE, IAction.AS_RADIO_BUTTON) {
			public void run() {
				fView.focusOnCircuit(null);
			}
		};
		noneAction.setChecked(!marked);
		ActionContributionItem item = new ActionContributionItem(noneAction);
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