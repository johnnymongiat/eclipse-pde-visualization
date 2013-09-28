package org.eclipse.pde.internal.visualization.dependency.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.service.resolver.BaseDescription;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.core.plugin.IMatchRules;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.feature.FeatureImport;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureImport;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.views.AnalysisUtil;
import org.osgi.framework.Version;

public final class FeatureDependencyHelper {
	
	/** Set which stores visited source vertices of the feature dependency graph. */
	private final transient Set processedSources = new HashSet(0);
	
	/** Cache containing currently processed <code>IPluginVertex</code>(s) that form dependencies in the feature graph. */
	private final transient Map pluginVertexCache = new HashMap(0);
	
	/** Cache containing currently processed error vertices linked to feature model dependencies that could not be found. */
	private final transient Map featureErrorVertexCache = new HashMap(0);
	
	/** Cache containing currently processed error vertices linked to plug-in model dependencies of a feature that could not be found. */
	private final transient Map pluginErrorVertexCache = new HashMap(0);
	
	/** Cache containing currently processed error vertices linked to fragment model dependencies of a feature that could not be found. */
	private final transient Map fragmentErrorVertexCache = new HashMap(0);
	
	/**
	 * The key class used to identify entries in the vertex caches. The key's uniqueness is determined 
	 * by the corresponding model object's id and version attributes.
	 */
	private static final class VertexCacheKey {
		public String id;
		public String version;
		public VertexCacheKey(String id, String version) {
			this.id = id;
			this.version = version;
		}
		public int hashCode() {
			final int prime = 31;
			int result = prime + id.hashCode();
			result = prime * result + version.hashCode();
			return result;
		}
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof VertexCacheKey)) {
				return false;
			}
			VertexCacheKey other = (VertexCacheKey) obj;
			return (id.equals(other.id) && version.equals(other.version));
		}
	}
	
	/**
	 * Returns the <code>Version.emptyVersion.toString()</code> value if the specified <code>version</code> is <code>null</code> or
	 * an "empty" string, otherwise returns the original <code>version</code>.
	 * 
	 * @param version the version
	 * 
	 * @return the <code>Version.emptyVersion.toString()</code> value if the specified <code>version</code> is <code>null</code> or
	 * an "empty" string, otherwise returns the original <code>version</code>.
	 */
	public static final String safeVersion(String version) {
		return (version == null || version.trim().length() == 0 ? Version.emptyVersion.toString() : version);
	}
	
	/**
	 * Clears all entries from the corresponding caches.
	 */
	private void clearCaches() {
		processedSources.clear();
		pluginVertexCache.clear();
		featureErrorVertexCache.clear();
		pluginErrorVertexCache.clear();
		fragmentErrorVertexCache.clear();
	}
	
	/**
	 * Returns the specified <code>graph</code>'s <code>IFeatureVertex</code> matching the specified <code>id</code>, and
	 * <code>version</code>, or <code>null</code> if no match.
	 * 
	 * @param graph   the feature <code>DiGraph</code>
	 * @param id      the feature id to match
	 * @param version the feature version to match
	 * 
	 * @return the specified <code>graph</code>'s <code>IFeatureVertex</code> matching the specified <code>id</code>, and
	 * <code>version</code>, or <code>null</code> if no match.
	 */
	private IFeatureVertex findFeatureVertexMatchingIdAndVersion(DiGraph graph, String id, String version) {
		IFeatureVertex match = null;
		if (graph.numberOfVertices() > 0) {
			final Version versionToMatch = Version.parseVersion(version);
			IVertex[] vertices = graph.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				IVertex v = vertices[i];
				if (v instanceof IFeatureVertex) {
					IFeatureVertex feature = (IFeatureVertex) v;
					if (id.equals(feature.getId()) && versionToMatch.equals(Version.parseVersion(feature.getVersion()))) {
						match = feature;
						break;
					}
				}
			}
		}
		return match;
	}

	/**
	 * Returns the <code>IPluginVertex</code> (from the plugin vertex cache) matching the specified <code>id</code>, and is 
	 * included in the specified version range, or <code>null</code> if no match.
	 * 
	 * @param id    the plug-in id to match
	 * @param range the version range to match
	 * 
	 * @return the <code>IPluginVertex</code> (from the plugin vertex cache) matching the specified <code>id</code>, and is 
	 * included in the specified version range, or <code>null</code> if no match.
	 */
	private IPluginVertex findPluginVertexMatchingIdAndVersionRange(String id, VersionRange range) {
		IPluginVertex match = null;
		Iterator itr = pluginVertexCache.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			VertexCacheKey key = (VertexCacheKey) entry.getKey();
			if (id.equals(key.id) && range.isIncluded(Version.parseVersion(key.version))) {
				match = (IPluginVertex) entry.getValue();
				break;
			}
		}
		return match;
	}

	public DiGraph createFeatureGraph(final IFeatureVertex root) {
		Assert.isNotNull(root);
		clearCaches();
		try {
			DiGraph graph = new DiGraph();
			graph.addVertex(root);
			buildFeatureGraph(graph, root);
			return graph;
		}
		finally {
			clearCaches();
		}
	}
	
	private void buildFeatureGraph(DiGraph graph, IFeatureVertex source) {
		if (processedSources.contains(source)) {
			return;
		}
		processedSources.add(source);
		
		IFeature theFeature = source.getCorrespondingFeature();
		
		// Process the current feature's references to other features ("Included Features" tab).
		IFeatureChild[] children = theFeature.getIncludedFeatures();
		for (int i = 0; i < children.length; i++) {
			IFeatureVertex vertex = processIncludedFeatureDependency(graph, source, children[i]);
			if (vertex != null) {
				buildFeatureGraph(graph, vertex);
			}
		}
		
		// Process the current feature's required features/plug-ins ("Dependencies" tab).
		IFeatureImport[] imports = theFeature.getImports();
		for (int i = 0; i < imports.length; i++) {
			IFeatureImport fi = imports[i];
			if (fi.getType() == IFeatureImport.FEATURE) {
				IFeatureVertex vertex = processRequiredFeatureDependency(graph, source, fi);
				if (vertex != null) {
					buildFeatureGraph(graph, vertex);
				}
			}
			else if (fi.getType() == IFeatureImport.PLUGIN) {
				processRequiredPluginDependencies(graph, source, fi);
			}
		}
		
		// Process the current feature's plug-ins and fragments ("Plug-ins" tab).
		IFeaturePlugin[] plugins = theFeature.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			processFeaturePluginDependencies(graph, source, plugins[i]);
		}
	}
	
	/**
	 * Adds a dependency/edge between the specified <code>source</code> feature vertex, and an <code>IFeatureVertex</code> 
	 * representing the provided included feature <code>child</code>, if and only if the feature model manager contains a 
	 * model matching the corresponding id, and version.
	 *  
	 * <p>
	 * If a model cannot be found, then a <code>UnresolvedModelVertex</code> will be added to <code>graph</code>, with an 
	 * edge from the <code>source</code> vertex to the error vertex. 
	 * </p>
	 * 
	 * @param graph  the feature dependency <code>DiGraph</code>
	 * @param source the source <code>IFeatureVertex</code> from which the dependency/edge will be created from
	 * @param child  the <code>IFeatureChild</code> to process
	 * 
	 * @return the <code>IFeatureVertex</code> representing the included feature that has been connected to <code>source</code>,
	 * or <code>null</code> if no model matching the corresponding id and version could be found.
	 */
	private IFeatureVertex processIncludedFeatureDependency(DiGraph graph, IFeatureVertex source, IFeatureChild child) {
		String id = child.getId();
		
		// If the version is not specified, then we must treat this as error since version is mandatory for included features.
		String version = child.getVersion();
		if (version == null || version.trim().length() == 0) {
			internalAddFeatureDependencyErrorVertex(graph, source, child, new VertexCacheKey(id, "")); //$NON-NLS-1$
			return null;
		}
		
		IFeatureModel featureModel = PDECore.getDefault().getFeatureModelManager().findFeatureModel(id, version);
		if (featureModel == null) {
			internalAddFeatureDependencyErrorVertex(graph, source, child, new VertexCacheKey(id, version));
			return null;
		}
		
		IFeature correspondingFeature = featureModel.getFeature();
		String fVersion = safeVersion(correspondingFeature.getVersion());
		IFeatureVertex vertex = findFeatureVertexMatchingIdAndVersion(graph, id, fVersion);
		if (vertex == null) {
			vertex = new FeatureVertex(child, id, fVersion, correspondingFeature);
		}
		graph.addEdge(source, vertex);	
		return vertex;
	}
	
	/**
	 * Adds a dependency/edge between the specified <code>source</code> feature vertex, and an <code>IFeatureVertex</code> 
	 * representing the provided required feature <code>featureImport</code>, if and only if the feature model manager contains
	 * a model matching the corresponding id, and version (as specified by the <code>featureImport</code>'s version match rule).
	 *  
	 * <p>
	 * If a model cannot be found, then a <code>UnresolvedModelVertex</code> will be added to <code>graph</code>, with an 
	 * edge from the <code>source</code> vertex to the error vertex. 
	 * </p>
	 * 
	 * @param graph          the feature dependency <code>DiGraph</code>
	 * @param source         the source <code>IFeatureVertex</code> from which the dependency/edge will be created from
	 * @param featureImport  the <code>IFeatureImport</code> representing the required feature to process
	 * 
	 * @return the <code>IFeatureVertex</code> representing the required feature that has been connected to <code>source</code>,
	 * or <code>null</code> if no model matching the corresponding id and version could be found.
	 */
	private IFeatureVertex processRequiredFeatureDependency(DiGraph graph, IFeatureVertex source, IFeatureImport featureImport) {
		String id = featureImport.getId();
		
		// If the version is not specified, then we must treat this as error since version is mandatory for required features.
		String version = featureImport.getVersion();
		if (version == null || version.trim().length() == 0) {
			internalAddFeatureDependencyErrorVertex(graph, source, featureImport, new VertexCacheKey(id, "")); //$NON-NLS-1$
			return null;
		}		
		
		IFeatureVertex vertex = null;
		IFeature correspondingFeature = ((FeatureImport)featureImport).findFeature(id, version, featureImport.getMatch());
		if (correspondingFeature == null) {
			internalAddFeatureDependencyErrorVertex(graph, source, featureImport, new VertexCacheKey(id, version));
		}
		else {
			String fVersion = safeVersion(correspondingFeature.getVersion());
			vertex = findFeatureVertexMatchingIdAndVersion(graph, id, fVersion);
			if (vertex == null) {
				vertex = new FeatureVertex(featureImport, id, fVersion, correspondingFeature);
			}
			graph.addEdge(source, vertex);
		}			
		return vertex;
	}
	
	private void internalAddFeatureDependencyErrorVertex(DiGraph graph, IFeatureVertex source, Object featureObject, VertexCacheKey key) {
		UnresolvedModelVertex errorVertex = (UnresolvedModelVertex) featureErrorVertexCache.get(key);
		if (errorVertex == null) {
			errorVertex = new UnresolvedModelVertex(featureObject, key.id, key.version, UnresolvedModelVertex.TYPE_FEATURE);
			featureErrorVertexCache.put(key, errorVertex);
		}
		graph.addEdge(source, errorVertex);
	}
	
	/**
	 * Adds a dependency/edge between the specified <code>source</code> feature vertex, and an <code>IPluginVertex</code> 
	 * representing the provided required plug-in <code>featureImport</code>, if and only if the plugin registry contains a 
	 * model matching the corresponding id, version, and match rule. Further dependencies of the representative plugin vertex 
	 * will also be computed and added to <code>graph</code> automatically.
	 *  
	 * <p>
	 * If a model cannot be found, then a <code>UnresolvedModelVertex</code> will be added to <code>graph</code>, with an 
	 * edge from the <code>source</code> vertex to the error vertex. 
	 * </p>
	 * 
	 * @param graph          the feature dependency <code>DiGraph</code>
	 * @param source         the source <code>IFeatureVertex</code> from which the dependency/edge will be created from
	 * @param requiredPlugin the <code>IFeatureImport</code> representing the required plug-in to process
	 * 
	 * @see #calculateAndAddPluginDependencies(DiGraph, IPluginVertex)
	 */
	private void processRequiredPluginDependencies(DiGraph graph, IFeatureVertex source, IFeatureImport featureImport) {
		IPluginModelBase matchingPluginModel = PluginRegistry.findModel(featureImport.getId(), featureImport.getVersion(), featureImport.getMatch(), null);
		if (matchingPluginModel == null) {
			// Note that if the version is in fact null, then this implies that a model with the corresponding id could not
			// be found (i.e. version + match rule were never even considered during the search), so we need to make sure that
			// the "" string will be used for the error vertex's version.
			VertexCacheKey key = new VertexCacheKey(featureImport.getId(), featureImport.getVersion() == null ? "" : safeVersion(featureImport.getVersion())); //$NON-NLS-1$
			UnresolvedModelVertex errorVertex = (UnresolvedModelVertex) pluginErrorVertexCache.get(key);
			if (errorVertex == null) {
				errorVertex = new UnresolvedModelVertex(featureImport, key.id, key.version, UnresolvedModelVertex.TYPE_PLUGIN);
				pluginErrorVertexCache.put(key, errorVertex);
			}
			graph.addEdge(source, errorVertex);
		}
		else {
			BundleDescription bd = matchingPluginModel.getBundleDescription();
			if (bd != null) {
				VertexCacheKey key = new VertexCacheKey(bd.getName(), (bd.getVersion() == null ? safeVersion(null) : bd.getVersion().toString()));
				IPluginVertex vertex = (IPluginVertex) pluginVertexCache.get(key);
				if (vertex == null) {
					vertex = new PluginVertex(bd);
					pluginVertexCache.put(key, vertex);
				}
				graph.addEdge(source, vertex);
				calculateAndAddPluginDependencies(graph, vertex);
			}
		}
	}

	/**
	 * Adds a dependency/edge between the specified <code>source</code> feature vertex, and an <code>IPluginVertex</code> 
	 * representing the provided feature <code>plugin</code>, if and only if the plugin registry contains a model matching 
	 * the corresponding id, and version. Further dependencies of the representative plugin vertex will also be computed 
	 * and added to <code>graph</code> automatically.
	 *  
	 * <p>
	 * If a model cannot be found, then a <code>UnresolvedModelVertex</code> will be added to <code>graph</code>, with an 
	 * edge from the <code>source</code> vertex to the error vertex. 
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> If the feature <code>plugin</code>'s version is the <code>Version.emptyVersion</code>, then the 
	 * <code>IMatchRules.GREATER_OR_EQUAL</code> match rule will be used, otherwise the <code>IMatchRules.PERFECT</code> match 
	 * rule will be used. 
	 * </p>
	 * 
	 * @param graph  the feature dependency <code>DiGraph</code>
	 * @param source the source <code>IFeatureVertex</code> from which the dependency/edge will be created from
	 * @param plugin the <code>IFeaturePlugin</code> to process
	 * 
	 * @see #calculateAndAddPluginDependencies(DiGraph, IPluginVertex)
	 */
	private void processFeaturePluginDependencies(DiGraph graph, IFeatureVertex source, IFeaturePlugin plugin) {
		String id = plugin.getId();
		
		// If the version is not specified, then we must treat this as error since version is mandatory for feature plug-ins.
		String version = plugin.getVersion();
		if (version == null || version.trim().length() == 0) {
			internalAddFeaturePluginErrorVertex(graph, source, plugin, new VertexCacheKey(id, "")); //$NON-NLS-1$
			return;
		}
		
		int matchRules = (Version.emptyVersion.toString().equals(version) ? IMatchRules.GREATER_OR_EQUAL : IMatchRules.PERFECT);
		IPluginModelBase matchingPluginModel = PluginRegistry.findModel(id, version, matchRules, null);
		if (matchingPluginModel == null) {
			internalAddFeaturePluginErrorVertex(graph, source, plugin, new VertexCacheKey(id, safeVersion(version)));
		}
		else {
			BundleDescription bd = matchingPluginModel.getBundleDescription();
			if (bd != null) {
				VertexCacheKey key = new VertexCacheKey(bd.getName(), (bd.getVersion() == null ? safeVersion(null) : bd.getVersion().toString()));
				IPluginVertex vertex = (IPluginVertex) pluginVertexCache.get(key);
				if (vertex == null) {
					vertex = new PluginVertex(bd);
					pluginVertexCache.put(key, vertex);
				}
				graph.addEdge(source, vertex);
				calculateAndAddPluginDependencies(graph, vertex);
			}
		}
	}

	private void internalAddFeaturePluginErrorVertex(DiGraph graph, IFeatureVertex source, IFeaturePlugin plugin, VertexCacheKey key) {
		Map errorVertexCache = (plugin.isFragment() ? fragmentErrorVertexCache : pluginErrorVertexCache);
		UnresolvedModelVertex errorVertex = (UnresolvedModelVertex) errorVertexCache.get(key);
		if (errorVertex == null) {
			errorVertex = new UnresolvedModelVertex(plugin, key.id, key.version, 
					(plugin.isFragment() ? UnresolvedModelVertex.TYPE_FRAGMENT : UnresolvedModelVertex.TYPE_PLUGIN));
			errorVertexCache.put(key, errorVertex);
		}
		graph.addEdge(source, errorVertex);
	}
	
	/**
	 * Computes and adds all plug-in dependencies/edges from the specified <code>source</code> plug-in vertex. Further dependencies 
	 * of the representative plug-in vertex will also be computed and added to <code>graph</code> automatically.
	 * 
	 * @param graph  the feature dependency <code>DiGraph</code>
	 * @param source the source <code>IPluginVertex</code> from which the plug-in dependencies/edges will be created
	 * 
	 * @see {@link org.eclipse.pde.internal.visualization.dependency.views.AnalysisUtil#getDependencies(Object)}
	 */
	private void calculateAndAddPluginDependencies(DiGraph graph, IPluginVertex source) {
		if (processedSources.contains(source)) {
			return;
		}
		processedSources.add(source);
		
		Object[] deps = AnalysisUtil.getDependencies(source.getModelObject());
		for (int i = 0; i < deps.length; i++) {
			Object dep = deps[i];
			if (dep instanceof BaseDescription) {
				BaseDescription bd = (BaseDescription) dep;
				VertexCacheKey key = new VertexCacheKey(bd.getName(), (bd.getVersion() == null ? safeVersion(null) : bd.getVersion().toString()));
				IPluginVertex vertex = (IPluginVertex) pluginVertexCache.get(key);
				if (vertex == null) {
					vertex = new PluginVertex(dep);
					pluginVertexCache.put(key, vertex);
				}
				graph.addEdge(source, vertex);
				calculateAndAddPluginDependencies(graph, vertex);
			}
			else if (dep instanceof BundleSpecification) { // Unresolved bundle
				BundleSpecification bs = (BundleSpecification) dep;
				if (bs.isOptional()){ // add as a IPluginVertex because it is optional
					VersionRange range = bs.getVersionRange();
					if (range == null) {
						range = VersionRange.emptyRange;
					}
					
					IPluginVertex vertex = findPluginVertexMatchingIdAndVersionRange(bs.getName(), range);
					if (vertex == null) {
						vertex = new PluginVertex(bs);
						VertexCacheKey key = new VertexCacheKey(bs.getName(), range.toString());
						pluginVertexCache.put(key, vertex);
					}
					graph.addEdge(source, vertex);
					calculateAndAddPluginDependencies(graph, vertex);
				} 
				else { // add as a UnresolvedModelVertex because is a mandatory bundle.
					VertexCacheKey key = new VertexCacheKey(bs.getName(), bs.getBundle().getVersion() == null ? "" : safeVersion(bs.getBundle().getVersion().toString())); //$NON-NLS-1$
					UnresolvedModelVertex errorVertex = (UnresolvedModelVertex) pluginErrorVertexCache.get(key);
					if (errorVertex == null) {
						errorVertex = new UnresolvedModelVertex(bs, key.id, key.version, UnresolvedModelVertex.TYPE_PLUGIN);
						pluginErrorVertexCache.put(key, errorVertex);
					}
					graph.addEdge(source, errorVertex);
				}
			}
		}
	}
	
}
