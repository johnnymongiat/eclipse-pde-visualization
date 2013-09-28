package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * A dialog used to show the list of all circuits found in the graph
 */
public class CircuitListDialog extends ListDialog{

	private final List fCircuitList;
		
	public CircuitListDialog(Shell parent, List elements, IElementaryCircuit initialSelection) {
		super(parent);
		setTitle(PDEMessages.CIRCUIT_LIST);
		this.fCircuitList = elements;
		
		setDefaultImage(PDEVizImages.get(PDEVizImages.IMG_LOOP_OBJ));
		setTitle(PDEMessages.SHOW_CYCLES);
		setMessage(PDEMessages.SELECT_A_CYCLE);
		setContentProvider(new CircuitContentProvider());
		setLabelProvider(new CircuitLabelProvider());
		setInput(fCircuitList);
		setInitialSelections( new Object[] {initialSelection});
		setHelpAvailable(false);
	}

	class CircuitContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object element) {
			return fCircuitList.toArray(new Object[fCircuitList.size()]);
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	}
	
	class CircuitLabelProvider extends LabelProvider {

		public Image getImage(Object element) {
			return PDEVizImages.get(PDEVizImages.IMG_LOOP_OBJ);
		}

		public String getText(Object element) {
			String circuit = PDEMessages.CIRCUIT;
			if (element instanceof IElementaryCircuit)
				return circuit + " " + (fCircuitList.indexOf(element)+1); //$NON-NLS-1$
			return circuit;
		}
	} 
}
