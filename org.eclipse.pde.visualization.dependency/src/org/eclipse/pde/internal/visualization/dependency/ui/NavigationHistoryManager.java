package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;

/**
 * Implementation of a history manager used for handling history
 */
public final class NavigationHistoryManager implements INavigationHistoryManager {

	private static final int DEFAULT_CAPACITY = 20;
	
	private final IDependencyVisualizationView fView;
	private final List history = new ArrayList(DEFAULT_CAPACITY);
	private int activeIndex = -1;
	private NavigationHistoryAction backwardAction;
	private NavigationHistoryAction forwardAction;
	
	public NavigationHistoryManager(IDependencyVisualizationView view) {
		Assert.isNotNull(view);
		fView = view;
	}
	
	public void add(IVertex vertex) {
		if (!history.contains(vertex)){
			Assert.isNotNull(vertex);
			removeForwardEntries();
			history.add(vertex);
			activeIndex++;
		} else {
			activeIndex = history.indexOf(vertex);
		}
		updateActions();
	}

	public void backward() {
		activeIndex--;
		IVertex vertex = (IVertex) history.get(activeIndex);
		fView.focusOn(vertex, false, null);
		updateActions();
	}

	public boolean canBackward() {
		return (activeIndex >= 1);
	}

	public boolean canForward() {
		return (activeIndex >= 0 && activeIndex < history.size() - 1);
	}

	public void clear() {
		history.clear();
		activeIndex = -1;
	}

	public void dispose() {
		clear();
	}
	
	public void forward() {
		activeIndex++;
		IVertex vertex = (IVertex) history.get(activeIndex);
		fView.focusOn(vertex, true, null);
		updateActions();
	}
	
	public IVertex getCurrent() {
		return (IVertex) (activeIndex == -1 ? null : history.get(activeIndex));
	}
	
	public IDependencyVisualizationView getDependencyVisualizationView() {
		return fView;
	}

	public void setBackwardAction(NavigationHistoryAction action) {
		backwardAction = action;
		updateActions();
	}

	public void setForwardAction(NavigationHistoryAction action) {
		forwardAction = action;
		updateActions();
	}
	
	private void removeForwardEntries() {
		while (canForward()) {
			history.remove(history.size()-1);
		}
	}

	public void updateActions() {
		activeIndex = history.indexOf(getCurrent());
		if (backwardAction != null) {
			backwardAction.update();
		}
		if (forwardAction != null) {
			forwardAction.update();
		}
	}

	public List getForwardSubList() {
		return history.subList(activeIndex+1, history.size());
	}

	public List getBackwardSubList() {
		return history.subList(0, activeIndex);
	}
	
}
