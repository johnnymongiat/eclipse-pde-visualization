package org.eclipse.pde.internal.visualization.dependency.ui.provisional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.service.resolver.BaseDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.model.IPluginVertex;
import org.eclipse.pde.internal.visualization.dependency.model.PluginVertex;
import org.eclipse.pde.internal.visualization.dependency.model.UnresolvedModelVertex;
import org.eclipse.pde.internal.visualization.dependency.views.AnalysisUtil;
import org.eclipse.pde.internal.visualization.dependency.views.DependencyUtil;
import org.osgi.framework.Version;

public final class PluginDependencyHelper {
	
	/** Set which stores visited source vertices of the plug-in dependency graph. */
	private final transient Set processedSources = new HashSet(0);
	
	/** Cache containing currently processed <code>IPluginVertex</code>(s) that form dependencies in the feature graph. */
	private final transient Map pluginVertexCache = new HashMap(0);
	
	/** Cache containing currently processed error vertices linked to plug-in model dependencies of a feature that could not be found. */
	private final transient Map pluginErrorVertexCache = new HashMap(0);
	
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
		pluginErrorVertexCache.clear();
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
	
	/**
	 * Computes and adds all plug-in dependencies/edges from the specified <code>source</code> plug-in vertex. 
	 * Further dependencies of the representative plug-in vertex will also be computed and added to <code>graph</code> 
	 * automatically.
	 * 
	 * @param graph  the plug-in dependency <code>DiGraph</code>
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

	public DiGraph createPluginDependencyGraph(final IPluginVertex root) {
		Assert.isNotNull(root);
		clearCaches();
		try {
			DiGraph graph = new DiGraph();
			graph.addVertex(root);
			// Make sure to add the root to vertex cache.
			Object model = root.getModelObject();
			if (model instanceof BaseDescription) {
				BaseDescription bd = (BaseDescription) model;
				VertexCacheKey key = new VertexCacheKey(bd.getName(), (bd.getVersion() == null ? safeVersion(null) : bd.getVersion().toString()));
				pluginVertexCache.put(key, root);
			}
			calculateAndAddPluginDependencies(graph, root);
			return graph;
		}
		finally {
			clearCaches();
		}
	}
	
	//TODO
	public DiGraph createReversedPluginDependencyGraph(final IPluginVertex root) {
		Assert.isNotNull(root);
		clearCaches();
		try {
			DiGraph graph = new DiGraph();
			graph.addVertex(root);
			// Make sure to add the root to vertex cache.
			Object model = root.getModelObject();
			if (model instanceof BaseDescription) {
				BaseDescription bd = (BaseDescription) model;
				VertexCacheKey key = new VertexCacheKey(bd.getName(), (bd.getVersion() == null ? safeVersion(null) : bd.getVersion().toString()));
				pluginVertexCache.put(key, root);
			}
			xxx(graph, root);
			return graph;
		}
		finally {
			clearCaches();
		}
	}
	
	private void xxx(DiGraph graph, IPluginVertex source) {
		if (processedSources.contains(source)) {
			return;
		}
		processedSources.add(source);
		
		Object[] deps = DependencyUtil.getDirectDependendBundles(source.getModelObject());
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
				//graph.addEdge(source, vertex);
				graph.addEdge(vertex, source);
				xxx(graph, vertex);
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
					//graph.addEdge(source, vertex);
					graph.addEdge(vertex, source);
					xxx(graph, vertex);
				} 
				else { // add as a UnresolvedModelVertex because is a mandatory bundle.
					VertexCacheKey key = new VertexCacheKey(bs.getName(), bs.getBundle().getVersion() == null ? "" : safeVersion(bs.getBundle().getVersion().toString())); //$NON-NLS-1$
					UnresolvedModelVertex errorVertex = (UnresolvedModelVertex) pluginErrorVertexCache.get(key);
					if (errorVertex == null) {
						errorVertex = new UnresolvedModelVertex(bs, key.id, key.version, UnresolvedModelVertex.TYPE_PLUGIN);
						pluginErrorVertexCache.put(key, errorVertex);
					}
					//graph.addEdge(source, errorVertex);
					graph.addEdge(errorVertex, source);
				}
			}
		}
	}
	
}
