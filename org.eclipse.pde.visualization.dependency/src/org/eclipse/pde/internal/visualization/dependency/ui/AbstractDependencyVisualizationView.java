package org.eclipse.pde.internal.visualization.dependency.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.pde.internal.visualization.dependency.PDEMessages;
import org.eclipse.pde.internal.visualization.dependency.PDEVizImages;
import org.eclipse.pde.internal.visualization.dependency.errors.ErrorReporting;
import org.eclipse.pde.internal.visualization.dependency.errors.UnresolvedError;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.DepthFirstTraversalAlgorithm;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuit;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.IElementaryCircuitFindingAlgorithm;
import org.eclipse.pde.internal.visualization.dependency.graph.algorithms.JohnsonCircuitFindingAlgorithm;
import org.eclipse.pde.internal.visualization.dependency.model.IPDEDependencyVertex;
import org.eclipse.pde.internal.visualization.dependency.model.UnresolvedModelVertex;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;

public abstract class AbstractDependencyVisualizationView extends ViewPart implements IDependencyVisualizationView, IZoomableWorkbenchPart {

	 /** Name of the group for embedding additional actions in the view's tool bar. */
    public static final String TB_GROUP_ADDITIONS = "group.additions"; // Group. //$NON-NLS-1$
    
	private static final IElementaryCircuit[] EMPTY_CIRCUITS = new IElementaryCircuit[0];
	
	private INavigationHistoryManager navigationHistoryManager = null;
	private IElementaryCircuit[] circuits = EMPTY_CIRCUITS;
	private IElementaryCircuit currentCircuit = null;
	
	private DiGraph dependencyGraph = null;
	/*private*/protected IVertex currentRootVertex = null;//TODO change back to private
	private GraphViewer viewer;
	private AbstractDependencyVisualizationLabelProvider labelProvider;
	private IGraphEntityContentProvider contentProvider;
	private boolean showVersionNum = false;
	
	private Action focusDialogAction;
	private Action focusDialogActionToolbar;
	private Action refreshAction;
	private Action showSearchBarAction;
	private NavigationHistoryAction backwardAction;
	private NavigationHistoryAction forwardAction;
	private ShowVersionNumbersAction showVersionNumbersAction;
	private PathsDropDownAction pathsDropDownAction;
	private CircuitsDropDownAction circuitsDropDownAction;
	private ScreenshotAction screenshotAction;
	private ZoomContributionViewItem toolbarZoomContributionViewItem;
	private ZoomContributionViewItem contextZoomContributionViewItem;
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	private Composite headClient;
	
	public AbstractDependencyVisualizationView() {
		super();
	}
	
	public INavigationHistoryManager getNavigationHistoryManager() {
		if (navigationHistoryManager == null) {
			navigationHistoryManager = new NavigationHistoryManager(this);
		}
		return navigationHistoryManager;
	}
	
	public IElementaryCircuit[] getCircuits() {
		return (circuits == null ? EMPTY_CIRCUITS : circuits);
	}
	
	public IElementaryCircuit getCurrentCircuit() {
		return currentCircuit;
	}
	
	public DiGraph getDependencyGraph() {
		return dependencyGraph;
	}
	
	public void setDependencyGraph(DiGraph graph) {
		dependencyGraph = graph;
		findCircuits();
	}
	
	public GraphViewer getGraphViewer() {
		return viewer;
	}
	
	public boolean isVersionNumbersShown() {
		return showVersionNum;
	}
	
	public void showVersionNumbers(boolean enable) {
		showVersionNum = enable;
		viewer.update(contentProvider.getElements(currentRootVertex), null);
		viewer.refresh();
		viewer.applyLayout();
		selectionChanged(labelProvider.getSelected());
	}
	
	public void focusOn(final IVertex root, final boolean recordHistory, final ErrorReporting errorReporting) {
		viewer.setSelection(new StructuredSelection());
		selectionChanged(null);
		viewer.setFilters(new ViewerFilter[] {});
		
		DiGraph computedGraph = computeDependencyGraph(root);
		
		if (errorReporting != null) {
			if (errorReporting.getClass() == UnresolvedError.class) {
				final List paths = new DepthFirstTraversalAlgorithm().findAllPaths(getDependencyGraph(), currentRootVertex, errorReporting.getVertex());
				viewer.addFilter(new ViewerFilter() {
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						if (element instanceof EntityConnectionData) {
							return true;
						}
						for (int i = 0; i < paths.size(); i++) {
							List list = (List) paths.get(i);
							if (list.contains(element)) {
								return true;
							}
						}
						return false;
					}
				});
			}
			ErrorReporting.showCurrentError(this, errorReporting, form.getMessageManager());
			viewer.setInput(getDependencyGraph());
		} else {//don't update the graph when filtering errors
			viewer.setInput(computedGraph);
		}

		form.setText(computeFormText(root));
		form.reflow(true);
		
		updateActionEnableStates(true);
		
		if (recordHistory && !root.equals(getNavigationHistoryManager().getCurrent())) {
			getNavigationHistoryManager().add(root);
		}
		
		currentRootVertex = root;
		viewer.setSelection(new StructuredSelection(root));
		selectionChanged(root);

		// When we load a new model, remove any pinnedNode;
		labelProvider.setPinnedVertex(null);
		
		// Check for errors in the graph
		if (errorReporting == null) {// Don't report errors while error reporting
			ErrorReporting.createErrorReports(computedGraph.getVertices(), this, form.getMessageManager());
		}
		
		viewer.setLayoutAlgorithm(getDefaultLayoutAlgorithm(currentRootVertex));
	}
	
	protected CompositeLayoutAlgorithm getDefaultLayoutAlgorithm(IVertex root) {
		return new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, new LayoutAlgorithm[] { 
				new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), 
				new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING) });
	}
	
	public void focusOnCircuit(IElementaryCircuit circuit) {
		currentCircuit = circuit;
		if (currentCircuit == null) {
			viewer.setLayoutAlgorithm(getDefaultLayoutAlgorithm(currentRootVertex));
			ViewerFilter[] filters = viewer.getFilters();
			if (filters != null && filters.length > 0) {
				for (int i = 0; i < filters.length; i++) {
					if (filters[i] instanceof CircuitViewerFilter) {
						viewer.setFilters(new ViewerFilter[] {});
						viewer.update(contentProvider.getElements(currentRootVertex), null);
						viewer.refresh();
						viewer.applyLayout();
						selectionChanged(labelProvider.getSelected());
						break;
					}
				}
			}
			return;
		}
		
		CircuitViewerFilter filter = new CircuitViewerFilter(currentCircuit);
		viewer.setFilters(new ViewerFilter[] {filter});
		if (currentCircuit.size() > 2) {
			viewer.setLayoutAlgorithm(new CircuitLayoutAlgorithm(currentCircuit, LayoutStyles.NO_LAYOUT_NODE_RESIZING));
		} else {//TODO layout algorithm
			// This is not necessary - we just do it because a circuit of size 2 needs no "special" layout
			// (even though the CircuitLayoutAlgorithm can correctly handle this case).
			viewer.setLayoutAlgorithm(getDefaultLayoutAlgorithm(currentRootVertex));
		}
		viewer.refresh();
		viewer.applyLayout();
	}
	
	private void findCircuits() {
		circuits = EMPTY_CIRCUITS;
		if (dependencyGraph != null) {
			BusyIndicator.showWhile(getSite().getShell().getDisplay(), new Runnable() {
				public void run() {
					IElementaryCircuitFindingAlgorithm circuitFindingAlg = new JohnsonCircuitFindingAlgorithm();
					circuits = circuitFindingAlg.findAllElementaryCircuits(dependencyGraph);
				}
			});
		}
		circuitsDropDownAction.setEnabled(circuits.length > 0);
	}
		
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		
		createHeaderRegion(form);
		
		GridLayout gLayout = new GridLayout(1, true);
		gLayout.marginHeight = 10;
		gLayout.marginWidth = 4; 
		gLayout.verticalSpacing = gLayout.horizontalSpacing = 0;
		form.getBody().setLayout(gLayout);
		
		toolkit.decorateFormHeading(form.getForm());
		createGraphSection(form.getBody());
		
		contentProvider = new DependencyGraphContentProvider(this);
		labelProvider = createDependencyVisualizationLabelProvider(new DefaultInterestingDependenciesCalculator());
		
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(null);
		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		viewer.setLayoutAlgorithm(getDefaultLayoutAlgorithm(currentRootVertex));
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selectedElement instanceof EntityConnectionData) {
					return;
				}
				AbstractDependencyVisualizationView.this.selectionChanged((IVertex) selectedElement);
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				Object element = selection.getFirstElement();
				if (element instanceof IVertex) {
					handleDoubleClickOf((IVertex) element);
				}
			}
		});

		toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
		getViewSite().getActionBars().getMenuManager().add(toolbarZoomContributionViewItem);
		contextZoomContributionViewItem = new ZoomContributionViewItem(this);
		
		makeActions();
		hookContextMenu();
		fillToolBar();
	}
	
	private void createHeaderRegion(final ScrolledForm form) {
		headClient = new Composite(form.getForm().getHead(), SWT.NULL);
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = glayout.marginHeight = 0;
		glayout.numColumns = 3;
		headClient.setLayout(glayout);
		headClient.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// Search label
		Label lblSearch = new Label(headClient, SWT.NONE);
		lblSearch.setText(PDEMessages.SEARCH_LABEL);
		
		// Search box
		final Text searchBox = toolkit.createText(headClient, ""); //$NON-NLS-1$
		GridData data = new GridData();
		data.widthHint = 300;
		searchBox.setLayoutData(data);
		
		// Cancel icon
		ToolBar cancelBar = new ToolBar(headClient, SWT.FLAT);
		final ToolItem cancelIcon = new ToolItem(cancelBar, SWT.NONE);
		cancelIcon.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				searchBox.setText(""); //$NON-NLS-1$
			}
		});
		cancelIcon.setImage(PDEVizImages.get(PDEVizImages.IMG_SEARCH_CANCEL));
		cancelIcon.setToolTipText(PDEMessages.CLEAR);
		cancelIcon.setEnabled(false);
		
		toolkit.paintBordersFor(headClient);
		
		searchBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (searchBox.getText().length() > 0) {
					cancelIcon.setEnabled(true);
				} else {
					cancelIcon.setEnabled(false);
				}
				
				List matched = new ArrayList();
				String textString = searchBox.getText();
				if (textString.length() > 0) {
					SearchPattern pattern = new SearchPattern();
					pattern.setPattern(textString.startsWith("*") ? textString : "*".concat(textString));
					Iterator iterator = viewer.getGraphControl().getNodes().iterator();
					while (iterator.hasNext()) {
						GraphItem item = (GraphItem) iterator.next();
						if (pattern.matches(item.getText())) {
							matched.add(item);
						}
					}
				}
				viewer.getGraphControl().setSelection((GraphItem[]) matched.toArray(new GraphItem[matched.size()]));
			}
		});
		
		form.setText(getInitialFormText());
		Image img = getInitialFormImage();
		if (img != null) {
			form.setImage(img);
		}
		
		// Add a hyperlink listener for the messages
		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
				String title = e.getLabel();
				Object href = e.getHref();
				if (href instanceof IMessage[] && ((IMessage[]) href).length > 1) {
					final Shell shell = new Shell(form.getShell(), SWT.ON_TOP | SWT.TOOL);
					shell.setImage(getImage(form.getMessageType()));
					shell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
					GridLayout layout = new GridLayout();
					layout.numColumns = 1;
					layout.verticalSpacing = 0;
					shell.setText(title);
					shell.setLayout(layout);
					
					Link link = new Link(shell, SWT.NONE);
					link.setText("<A>close</A>"); //$NON-NLS-1$
					GridData data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
					link.setLayoutData(data);
					link.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							shell.close();
						}
					});
					
					Group group = new Group(shell, SWT.NONE);
					data = new GridData(SWT.LEFT, SWT.TOP, true, true);
					group.setLayoutData(data);
					group.setLayout(layout);
					group.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					FormText text = toolkit.createFormText(group, true);
					configureFormText(form.getForm(), text);
					if (href instanceof IMessage[]) {
						text.setText(createFormTextContent((IMessage[]) href), true, false);
					}

					shell.pack();
					
					// Ensure the shell will stay within the primary monitor's bounds.
					final int cushion = 10;
					Point sourcePoint = ((Control) e.widget).toDisplay(0, 0);
					Rectangle pBounds = shell.getDisplay().getPrimaryMonitor().getBounds();
					Rectangle sBounds = shell.getBounds();
				    int delta = sourcePoint.x + sBounds.width + cushion - pBounds.width;
				    sourcePoint.x += (delta >= 0 ? -delta : cushion);
				    delta = sourcePoint.y + sBounds.height + cushion - pBounds.height;
					sourcePoint.y += (delta >= 0 ? -delta : cushion);
				    shell.setLocation(sourcePoint);
					
				    shell.open();
					
				} else if (href instanceof IMessage[]) {
					IMessage oneMessage = ((IMessage[]) href)[0];
					ErrorReporting error = (ErrorReporting) oneMessage.getData();
					if (error != null) {
						error.handleError();
					}
				}
			}
		});
		
		form.setHeadClient(headClient);
		form.reflow(true);
	}
	
	private void configureFormText(final Form form, FormText text) {
		text.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				String is = (String) e.getHref();
				try {
					((FormText) e.widget).getShell().dispose();
					int index = Integer.parseInt(is);
					IMessage[] messages = form.getChildrenMessages();
					IMessage message = messages[index];
					ErrorReporting error = (ErrorReporting) message.getData();
					if (error != null) {
						error.handleError();
					}
				} catch (NumberFormatException ex) {
				}
			}
		});
		text.setImage("error", getImage(IMessageProvider.ERROR)); //$NON-NLS-1$
		text.setImage("warning", getImage(IMessageProvider.WARNING)); //$NON-NLS-1$
		text.setImage("info", getImage(IMessageProvider.INFORMATION)); //$NON-NLS-1$
	}
	
	String createFormTextContent(IMessage[] messages) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("<form>"); //$NON-NLS-1$
		for (int i = 0; i < messages.length; i++) {
			IMessage message = messages[i];
			pw.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\""); //$NON-NLS-1$
			switch (message.getMessageType()) {
			case IMessageProvider.ERROR:
				pw.print("error"); //$NON-NLS-1$
				break;
			case IMessageProvider.WARNING:
				pw.print("warning"); //$NON-NLS-1$
				break;
			case IMessageProvider.INFORMATION:
				pw.print("info"); //$NON-NLS-1$
				break;
			}
			pw.print("\"> <a href=\""); //$NON-NLS-1$
			pw.print(i + ""); //$NON-NLS-1$
			pw.print("\">"); //$NON-NLS-1$
			if (message.getPrefix() != null) {
				pw.print(message.getPrefix());
			}
			pw.print(message.getMessage());
			pw.println("</a></li>"); //$NON-NLS-1$
		}
		pw.println("</form>"); //$NON-NLS-1$
		pw.flush();
		return sw.toString();
	}
	
	private Image getImage(int type) {
		switch (type) {
		case IMessageProvider.ERROR:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		case IMessageProvider.WARNING:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		case IMessageProvider.INFORMATION:
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		}
		return null;
	}
	
	private void createGraphSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		viewer = new VizGraphViewer(section, SWT.NONE);
		section.setClient(viewer.getControl());
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		toolkit.paintBordersFor(parent);
	}
	
	/**
	 * Create the standard actions.
	 */
	private void makeActions() {
		// The standard action for launching a corresponding dialog for selecting a PDE model object to be the
		// focus of dependency analysis. 
		focusDialogAction = createFocusDialogAction();

		// The corresponding toolbar's focus dialog action.
		focusDialogActionToolbar = new Action() {
			public void run() {
				focusDialogAction.run();
			}
		};
		focusDialogActionToolbar.setToolTipText(focusDialogAction.getToolTipText());
		focusDialogActionToolbar.setImageDescriptor(PDEVizImages.DESC_FOCUS);
		
		// Refresh action.
		refreshAction = new Action(){
			public void run() {
				if (currentRootVertex != null){
					focusOn(currentRootVertex, false, null);
				}
			}
		};
		refreshAction.setText(PDEMessages.REFRESH);
		refreshAction.setToolTipText(PDEMessages.REFRESH);
		refreshAction.setImageDescriptor(PDEVizImages.DESC_REFRESH);
		refreshAction.setEnabled(false);
		
		// The show/hide search bar toggle action.
		showSearchBarAction = new Action("", IAction.AS_CHECK_BOX){ //$NON-NLS-1$
			public void run() {
				form.setHeadClient(isChecked() ? headClient : null);
				form.reflow(true);
			}
		};
		showSearchBarAction.setText(PDEMessages.SHOW_SEARCH_BAR);
		showSearchBarAction.setToolTipText(PDEMessages.SHOW_SEARCH_BAR);
		showSearchBarAction.setImageDescriptor(PDEVizImages.DESC_SEARCH_BAR);
		showSearchBarAction.setChecked(true);
		
		// The standard navigation history actions.
		backwardAction = new NavigationHistoryAction(getNavigationHistoryManager(), false);
		forwardAction = new NavigationHistoryAction(getNavigationHistoryManager(), true);
		
		// The show/hide version number action.
		showVersionNumbersAction = new ShowVersionNumbersAction(this);
		
		// The paths drop-down action.
		pathsDropDownAction = new PathsDropDownAction(labelProvider);
		
		// The elementary circuits drop-down action.
		circuitsDropDownAction = new CircuitsDropDownAction(this);
		circuitsDropDownAction.setEnabled(false);
		
		// The standard screenshot action.
		screenshotAction = new ScreenshotAction(this);
		screenshotAction.setEnabled(false);
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		fillContextMenu(menuMgr);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				AbstractDependencyVisualizationView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator());
		if (!viewer.getSelection().isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.getFirstElement() instanceof IPDEDependencyVertex){
				IPDEDependencyVertex vertex = (IPDEDependencyVertex) selection.getFirstElement();
				if (!(vertex instanceof UnresolvedModelVertex)) {
					Action openEditorAction = createOpenEditorContextMenuAction(vertex);
					if (openEditorAction != null) {
						manager.add(openEditorAction);
						manager.add(new Separator());
					}
					Action focusOnAction = createFocusOnContextMenuAction(vertex);
					manager.add(focusOnAction);
				}
			}
		}
		manager.add(focusDialogAction);
		manager.add(new Separator());
		manager.add(refreshAction);
		configureAdditionalContextMenuActions(manager); // Allow additional actions to be contributed.
		manager.add(new Separator());
		
		// Add history actions.
		Action backHistoryAction = new Action() {
			public void run() {
				backwardAction.run();
			}
		};
		backHistoryAction.setText(backwardAction.getText());
		backHistoryAction.setImageDescriptor(backwardAction.getImageDescriptor());
		backHistoryAction.setEnabled(backwardAction.isEnabled());
		manager.add(backHistoryAction);
		Action forwardHistoryAction = new Action() {
			public void run() {
				forwardAction.run();
			}
		};
		forwardHistoryAction.setText(forwardAction.getText());
		forwardHistoryAction.setImageDescriptor(forwardAction.getImageDescriptor());
		forwardHistoryAction.setEnabled(forwardAction.isEnabled());
		manager.add(forwardHistoryAction);
		manager.add(new Separator());
		
		manager.add(screenshotAction);
		manager.add(new Separator());
		manager.add(contextZoomContributionViewItem);
	}
	
	private void fillToolBar() {
		final IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(focusDialogActionToolbar);
		manager.add(new Separator());
		manager.add(new GroupMarker(TB_GROUP_ADDITIONS));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(backwardAction);
		manager.add(forwardAction);
		manager.add(new Separator());
		manager.add(showSearchBarAction);
		manager.add(showVersionNumbersAction);
		manager.add(new Separator());
		manager.add(pathsDropDownAction);
		manager.add(circuitsDropDownAction);
		manager.add(new Separator());
		manager.add(screenshotAction);
		configureAdditionalToolBarActions(manager); // Allow additional actions to be contributed.
	}
	
	private void selectionChanged(IVertex selected) {
		labelProvider.setCurrentSelection(currentRootVertex, selected);
		viewer.update(contentProvider.getElements(currentRootVertex), null);
	}
	
	public void dispose() {
		form.dispose();
		super.dispose();
	}

	public void setFocus() {
		form.setFocus();
	}

	public AbstractZoomableViewer getZoomableViewer() {
		return viewer;
	}

	public void handleUnresolvedDependencyError(UnresolvedError unresolvedError) {
		focusOn(currentRootVertex, false, unresolvedError);
	}
	
	public void handleSuppressError() {
		focusOn(currentRootVertex, false, null);
	}
	
	protected void updateActionEnableStates(boolean enabled){
		screenshotAction.setEnabled(enabled);
		refreshAction.setEnabled(enabled);
	}
	
	protected abstract String getInitialFormText();
	
	protected Image getInitialFormImage() {
		return PDEVizImages.get(PDEVizImages.IMG_REQ_PLUGIN_OBJ);
	}
	
	protected abstract AbstractDependencyVisualizationLabelProvider createDependencyVisualizationLabelProvider(AbstractInterestingDependenciesCalculator dependencyCalculator);
	
	protected abstract DiGraph computeDependencyGraph(IVertex root);
	
	protected abstract String computeFormText(IVertex root);
	
	protected abstract void handleDoubleClickOf(IVertex selection);
	
	protected abstract Action createFocusDialogAction();
	
	// vertexToFocusOn
	protected abstract Action createFocusOnContextMenuAction(IPDEDependencyVertex vertex);
	
	protected Action createOpenEditorContextMenuAction(IPDEDependencyVertex vertex) {
		return null;
	}
	
	protected void configureAdditionalContextMenuActions(IMenuManager manager) {
		// default makes no additional contributions
	}
	
	protected void configureAdditionalToolBarActions(IToolBarManager manager) {
		// default makes no additional contributions
	}
	
	private static final class VizGraphViewer extends GraphViewer {
		public VizGraphViewer(Composite parent, int style) {
			super(parent, style);
			Graph graph = new Graph(parent, style) {
				public Point computeSize(int hint, int hint2, boolean changed) {
					return new Point(0, 0);
				}
			};
			setControl(graph);
		}
	}
	
}
