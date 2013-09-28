package org.eclipse.pde.internal.visualization.dependency.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

public abstract class AbstractInterestingDependenciesCalculator {

	private Set interestingEntities = new HashSet(0);
	private Set interestingRelationships = new HashSet(0);
	
	/**
	 * Calculates the interesting entities and relationships
	 * @param provider - the label provider used to calculate the dependencies
	 */
	public final void calculate(AbstractDependencyVisualizationLabelProvider provider) {
		Assert.isNotNull(provider);
		interestingEntities.clear();
		interestingRelationships.clear();
		calculateInterestingDependencies(provider, interestingEntities, interestingRelationships);
	}

	/**
	 * Calculates the interesting entities and relationships
	 * @param provider - the label provider used to calculate the dependencies
	 * @param interestingEntities - the <code>set</code> of interesting entities
	 * @param interestingRelationships - the <code>set</code> of interesting relationships
	 */
	protected abstract void calculateInterestingDependencies(AbstractDependencyVisualizationLabelProvider provider,
			Set interestingEntities, Set interestingRelationships);

	/**
	 * Clears the set of interesting entities and relationships
	 */
	public void clear() {
		interestingEntities.clear();
		interestingRelationships.clear();
	}
	
	/**
	 * Returns the <code>set</code> of interesting entities
	 * @return the <code>set</code> of interesting entities
	 */
	public Set getInterestingEntities() {
		return Collections.unmodifiableSet(interestingEntities);
	}
	
	/**
	 * Returns the <code>set</code> of interesting relationships
	 * @return the <code>set</code> of interesting relationships
	 */
	public Set getInterestingRelationships() {
		return Collections.unmodifiableSet(interestingRelationships);
	}
	
}
