package org.eclipse.pde.internal.visualization.dependency;

import org.eclipse.osgi.util.NLS;

public class PDEMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.pde.internal.visualization.dependency.pdemessages"; //$NON-NLS-1$

	public static String ALL_PATHS;
	public static String BACK;
	public static String BUNDLE;
	public static String CIRCUIT;
	public static String CIRCUIT_LIST;
	public static String CLEAR;
	public static String ERROR_OPENING_VIEW;
	public static String FEATURE;
	public static String FEATURE_VIEW_NAME;
	public static String FEATURE_VIEW_TITLE_NODE;
	public static String FEATURE_VIEW_TITLE;
	public static String FOCUS_ON;
	public static String FOCUS_ON_NODE;
	public static String FOCUS_ON_CIRCUIT;
	public static String FOCUS_ON_FEATURE;
	public static String FORWARD;
	public static String GO_BACK;
	public static String GO_FORWARD;
	public static String IMAGE_PREVIEW;
	public static String MORE;
	public static String NONE;
	public static String OPEN_IN_FEATURE_EDITOR;
	public static String OPEN_IN_MANIFEST_EDITOR;
	public static String REFRESH;
	public static String SCREENSHOT;
	public static String SEARCH_LABEL;
	public static String SELECT_A_CYCLE;
	public static String SHORTEST_PATH;
	public static String SHOW_CYCLES;
	public static String SHOW_DEPENDENCY_PATH;
	public static String SHOW_SEARCH_BAR;
	public static String SHOW_VERSIONS;
	public static String SUPPRESS;
	public static String UNRESOLVED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, PDEMessages.class);
	}

	private PDEMessages() {
	}
}
