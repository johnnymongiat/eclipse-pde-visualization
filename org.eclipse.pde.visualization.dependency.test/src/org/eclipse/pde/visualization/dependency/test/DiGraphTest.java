package org.eclipse.pde.visualization.dependency.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.pde.internal.visualization.dependency.graph.DiGraph;
import org.eclipse.pde.internal.visualization.dependency.graph.IVertex;
import org.eclipse.pde.internal.visualization.dependency.graph.Vertex;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DiGraphTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddVertex() {
		final DiGraph g = new DiGraph();
		final Vertex v = new Vertex();
		assertTrue("Vertex should have been added to the DiGraph!", g.addVertex(v));
		final IVertex[] vs = g.getVertices();
		assertEquals(1, vs.length);
		assertEquals(v, vs[0]);
	}

	@Test
	public void testAddEdge() {
		final DiGraph g = new DiGraph();
		final Vertex v1 = new Vertex();
		final Vertex v2 = new Vertex();
		assertTrue("Edge (v1, v2) should have been added to the DiGraph!", g.addEdge(v1, v2));
		assertEquals(1, g.numberOfEdges());
	}

	@Ignore
	@Test
	public void testIsEmpty() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testNumberOfVertices() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testNumberOfEdges() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetConnectedTo() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetVertices() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testContainsVertex() {
		fail("Not yet implemented");
	}

}
