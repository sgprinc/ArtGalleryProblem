package artgallery;

import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.Collectors;

import artgallery.dataStructures.AVLTree;
import artgallery.geometricalElements.Edge;
import artgallery.geometricalElements.Hole;
import artgallery.geometricalElements.Polygon;
import artgallery.geometricalElements.Vertex;

public final class GeometricAlgorithms {
	public ArrayList<Edge> trapezoidalEdges = new ArrayList<Edge>();

	public GeometricAlgorithms() {
	}

	// Public method to trigger the triangulation of a complex polygon through
	// the method proposed by Seidel here: http://gamma.cs.unc.edu/SEIDEL/#FIG
	// (Input) Complex Polygon -> Trapezoidal Decomposition -> Monotone Polygons
	// -> (Output) Monotonic Triangulation.
	public ArrayList<Polygon> computeTriangulation(Polygon polygon) {
		// Create a deep copy of the polygon object to avoid modifying the
		// original gallery.
		Polygon polygonCopy = polygon.clone();

		// Decompose the complex (copied) gallery polygon into trapezoids.
		ArrayList<Polygon> trapezoids = trapezoidalDecomposition(polygonCopy);
		// Obtain a set of monotone polygons out of said trapezoids.
		ArrayList<Polygon> monotonePolygons = monotonePolygonization(trapezoids);
		// Triangulate each of said monotone polygons.
		ArrayList<Polygon> triangulation = new ArrayList<Polygon>();
		for (Polygon mp : monotonePolygons) {
			triangulation.addAll(triangulateMonotonePolygon(mp));
		}

		// Finally return the list containing a triangle (Polygon) for each
		// triangle in the triangulation.
		return triangulation;
	}

	// Not fully implemented - for now simply setting the global list of
	// trapezoidal edges
	// for display debugging purposes.

	public ArrayList<Polygon> trapezoidalDecomposition(Polygon fullPolygon) {
		ArrayList<Polygon> trapezoids = new ArrayList<Polygon>();
		ArrayList<Vertex> polygonVertices = fullPolygon.getVerticesAndHoles();
		ArrayList<Edge> polygonEdges = fullPolygon.getEdgesAndHoles();
		// ArrayList<Edge> trapezoidalEdges = new ArrayList<Edge>();

		// Get all vertices and sort in descending Y-order.
		polygonVertices.sort((v1, v2) -> Double.compare(v2.getY(), v1.getY()));

		// Make a huge sweepline (edge) to test for intersections.
		for (Vertex vertex : polygonVertices) {
			Edge sweepLine = new Edge(new Vertex((int) (-Math.floor(fullPolygon.getMaxDistance())), vertex.getY()),
					new Vertex((int) (Math.ceil(fullPolygon.getMaxDistance())), vertex.getY()));

			// Check for edge intersections

			PriorityQueue<Vertex> intersectionsLeft = new PriorityQueue<Vertex>(
					(v1, v2) -> Double.compare(computeDistance(vertex, v1), computeDistance(vertex, v2)));
			PriorityQueue<Vertex> intersectionsRight = new PriorityQueue<Vertex>(
					(v1, v2) -> Double.compare(computeDistance(vertex, v1), computeDistance(vertex, v2)));

			for (Edge edge : polygonEdges) {
				Vertex intersection = getIntersectionPoint(sweepLine, edge);
				// When a different, valid intersection occurs, store the
				// intersection point
				if (intersection != null && !intersection.equals(vertex)) {
					if (intersection.getX() < vertex.getX()) {
						intersectionsLeft.add(intersection);
					} else if (intersection.getX() > vertex.getX()) {
						intersectionsRight.add(intersection);
					}
				}
			}

			Vertex current = new Vertex(vertex.getX(), vertex.getY());
			// Consider only the two closest ones, as the trapezoidal
			if (intersectionsLeft.size() > 0) {
				// Make a non-final edge to test whether it would be valid
				Vertex intersection = intersectionsLeft.poll();
				Edge tempEdge = new Edge(intersection, current);

				// Check if the middlepoint of said edge is within the polygon
				if (insidePolygon(tempEdge.getMidpoint(), fullPolygon)) {
					intersection = fullPolygon.getMatchingVertex(intersection) == null ? intersection
							: fullPolygon.getMatchingVertex(intersection);
					// Check if the midpoint is inside the polygon
					if (!vertex.isNeighbor(intersection)) {
						if (!trapezoidalEdges.contains(tempEdge)) {
							trapezoidalEdges.add(tempEdge);
						}
					}
				}
			}

			if (intersectionsRight.size() > 0) {
				// Make a non-final edge to test whether it would be valid
				Vertex intersection = intersectionsRight.poll();
				Edge tempEdge = new Edge(current, intersection);

				// Check if the middlepoint of said edge is within the polygon
				if (insidePolygon(tempEdge.getMidpoint(), fullPolygon)) {
					intersection = fullPolygon.getMatchingVertex(intersection) == null ? intersection
							: fullPolygon.getMatchingVertex(intersection);
					// Check if the midpoint is inside the polygon
					if (!vertex.isNeighbor(intersection)) {
						if (!trapezoidalEdges.contains(tempEdge)) {
							trapezoidalEdges.add(tempEdge);
						}
					}
				}
			}
		}

		trapezoids.add(fullPolygon);
		return trapezoids;

	}

	// Not implemented - for now simply returning an empty list
	private ArrayList<Polygon> monotonePolygonization(ArrayList<Polygon> trapezoid) {
		ArrayList<Polygon> monotonePolygons = new ArrayList<Polygon>();

		return monotonePolygons;
	}

	/*
	 * Implementation of "TriangulateMonotonePolygon(polygon P as DCEL)". From
	 * the course slides "3 - Art Gallery Triangulation", page 158. Input:
	 * Y-Monotone polygon "p". Output: ArrayList of triangular polygons.
	 */
	private ArrayList<Polygon> triangulateMonotonePolygon(Polygon monotonePolygon) {
		ArrayList<Vertex> triangulationVertices = monotonePolygon.getVertices();
		ArrayList<Edge> triangulationEdges = monotonePolygon.getEdges();

		// Sorts the vertices on Y-Descending order.
		Collections.sort(triangulationVertices, (v1, v2) -> Double.compare(v2.getY(), v1.getY()));
		monotonePolygon.constructChains();

		// Implementation translation from the book.
		Stack<Vertex> s = new Stack<Vertex>();
		s.push(triangulationVertices.get(0));
		s.push(triangulationVertices.get(1));

		// Begin generating the internal edges
		for (int j = 2; j < triangulationVertices.size() - 1; ++j) {
			Vertex uj = triangulationVertices.get(j);

			// HERE IS WHERE THE CHAIN CHECK SHOULD BE ADDED <---
			if (!s.peek().isNeighbor(uj)) {
				while (s.size() > 1) {
					Vertex v = s.pop();
					triangulationEdges.add(new Edge(uj, v));
				}
				s.pop();
				s.push(triangulationVertices.get(j - 1));
				s.push(uj);
			} else {
				Vertex v = s.pop();
				// Must implement "sees"
				while (s.size() > 0) {
					v = s.pop();
					triangulationEdges.add(new Edge(uj, v));
				}
				s.push(v);
				s.push(uj);
			}
		}

		s.pop();
		while (s.size() > 1) {
			Vertex v = s.pop();
			triangulationEdges.add(new Edge(triangulationVertices.get(triangulationVertices.size() - 1), v));
		}

		// Finally convert the loose list of edges into solid triangular
		// polygons and return.
		ArrayList<Polygon> triangles = constructTriangles(triangulationEdges);
		return triangles;
	}

	// Iterates through all of the loose edges in the list and attempts to put
	// together triangles by matching their vertices. Unfortunately O(n^3) as
	// all edges have to be checked in each of the levels required (triangles =
	// 3 levels). Perhaps could be improved through filtering, a better
	// algorithm, or better usage of references later on.
	private ArrayList<Polygon> constructTriangles(ArrayList<Edge> edges) {
		ArrayList<Polygon> triangles = new ArrayList<Polygon>();

		for (Edge firstEdge : edges) {
			Vertex firstVertex = firstEdge.getStartVertex();
			for (Edge secondEdge : edges) {
				if (!secondEdge.equals(firstEdge) && secondEdge.containsVertex(firstVertex)) {
					Vertex secondVertex = secondEdge.getOtherVertex(firstVertex);
					for (Edge thirdEdge : edges) {
						if (!thirdEdge.equals(secondEdge) && !thirdEdge.equals(firstEdge)
								&& thirdEdge.containsVertex(secondVertex)) {
							if (thirdEdge.containsVertex(firstEdge.getOtherVertex(firstVertex))) {
								ArrayList<Edge> triangleEdges = new ArrayList<Edge>();
								triangleEdges.add(firstEdge);
								triangleEdges.add(secondEdge);
								triangleEdges.add(thirdEdge);

								Polygon triangle = new Polygon(triangleEdges);

								if (!triangles.stream().anyMatch(t -> t.equals(triangle))) {
									System.out.println("Adding triangle: " + firstEdge.toString() + " - "
											+ secondEdge.toString() + " - " + thirdEdge.toString());
									triangles.add(triangle);
								}
							}
						}
					}
				}
			}
		}
		return triangles;
	}

	// Simple visibility algorithm based on testing each vertex around the viewpoint.
	// Not finished and somewhat buggy on edge-cases.
	public ArrayList<Polygon> computeVisibilityPolygon(Vertex viewPoint, Polygon p) {
		// Array of triangles comprising the visibility polygon
		ArrayList<Polygon> visiblePolygons = new ArrayList<Polygon>();

		// Array containing all points in the scene but the view point, and
		// sorted on angle+proximity.
		ArrayList<Vertex> visibleVertices = p.getVerticesAndHoles();
		visibleVertices.remove(viewPoint);
		visibleVertices.sort((v1, v2) -> compareAngleAndProximity(viewPoint, v1, v2));

		// Extend sightlines (edges) from the viewpoint into each of the scene
		// vertices.
		ArrayList<Edge> sightLines = new ArrayList<Edge>();
		for (Vertex w : visibleVertices) {
			double angle = computeCCWAngle(w, viewPoint);
			int endX = (int) (viewPoint.getX() + (Math.cos(Math.toRadians(angle)) * p.getMaxDistance()));
			int endY = (int) (viewPoint.getY() + (Math.sin(Math.toRadians(angle)) * p.getMaxDistance()));
			Vertex endOfLine = new Vertex(endX, endY);
			sightLines.add(new Edge(viewPoint, endOfLine));
			System.out.println(w.getId() + " - " + angle);
		}

		ArrayList<Edge> obstacles = p.getEdgesAndHoles();
		ArrayList<Vertex> visibilityPolygonVertices = new ArrayList<Vertex>();
		for (Edge sightLine : sightLines) {
			PriorityQueue<Vertex> queue = new PriorityQueue<Vertex>(
					(v1, v2) -> Double.compare(computeDistance(viewPoint, v1), computeDistance(viewPoint, v2)));
			for (Edge obstacle : obstacles) {
				Vertex intersection = getIntersectionPoint(sightLine, obstacle);
				if (intersection != null) {
					visibilityPolygonVertices.add(intersection);
				}
			}
		}

		for (int i = 0; i < visibilityPolygonVertices.size() - 1; ++i) {
			ArrayList<Vertex> visTriangleVertices = new ArrayList<Vertex>();
			visTriangleVertices.add(viewPoint);
			visTriangleVertices.add(visibilityPolygonVertices.get(i));
			visTriangleVertices.add(visibilityPolygonVertices.get((i + 1)));
			Polygon visibleTriangle = new Polygon(visTriangleVertices);
			visiblePolygons.add(visibleTriangle);
		}

		return visiblePolygons;
	}

	/*
	 * Implementation of "VisibleVertices(P, S)". From the book
	 * "Computational Geometry: Algorithms and Applications - Third Edition" by
	 * M. Berg , page 328. Input: Point P, set of polygonal obstacles S. Output:
	 * The set of visible vertices from P.
	 */
	// Not currently working and the subroutine wasn't implemented.
	private ArrayList<Vertex> visibleVertices(Vertex v, Polygon p) {
		// 1) Sort the obstacle vertices according to the clockwise angle that
		// the half-line from p to each vertex makes with the positive x-axis.
		ArrayList<Vertex> obstacleVertices = p.getVerticesAndHoles();
		obstacleVertices.sort((v1, v2) -> compareAngleAndProximity(v, v1, v2));

		// Find the obstacle edges that are properly intersected by p parallel
		// to the x-axis and store them in a
		// balanced search tree in the order they intersect p.
		AVLTree<Edge> tree = new AVLTree<Edge>();
		Edge sweepLine = new Edge(v, new Vertex((int) (Math.ceil(p.getMaxDistance())), v.getY()));

		ArrayList<Edge> intersectedEdges = new ArrayList<Edge>();
		for (Edge obstacleEdge : p.getEdges()) {
			if (getIntersectionPoint(sweepLine, obstacleEdge) != null) {
				intersectedEdges.add(obstacleEdge);
			}
		}
		intersectedEdges.sort((e1, e2) -> compareIntersectionDistance(v, sweepLine, e1, e2));
		for (Edge edge : intersectedEdges) {
			tree.insert(edge);
		}

		ArrayList<Vertex> visibleVertices = new ArrayList<Vertex>();

		for (int i = 0; i < obstacleVertices.size(); ++i) {
			Vertex vi = obstacleVertices.get(i);
			if (isVisible(obstacleVertices, i)) {
				visibleVertices.add(vi);

				List<Edge> incidentEdges = (List<Edge>) intersectedEdges.stream().filter(e -> e.containsVertex(vi))
						.collect(Collectors.toList());
				for (Edge e : incidentEdges) {
					// Get the other vertex (not "vi") of the incident edge
					Vertex otherVertex = e.getOtherVertex(vi);
					// If the orientation if clockwise w.r.t. the sweepline, add
					// the edge to the tree.
					if (orientation(v, vi, otherVertex) == 1) {
						tree.insert(e);
					}
					// If the orientation if counter-clockwise w.r.t. the
					// sweepline, remove the edge to the tree.
					if (orientation(v, vi, otherVertex) == 2) {
						tree.remove(e);
					}
				}
			}
		}
		return visibleVertices;
	}

	/*
	 * Implementation of "Visible(W)" subroutine. From the book
	 * "Computational Geometry: Algorithms and Applications - Third Edition" by
	 * M. Berg , page 329. Input: Point W. Output: The set of visible vertices
	 * from P.
	 */
	private boolean isVisible(ArrayList<Vertex> w, int i) {
		//Not currently implemented as I switched to a simpler algorithm.
		return true;
	}

	// Computes simple euclidian distance between two vertices.
	private double computeDistance(Vertex v1, Vertex v2) {
		return Math.sqrt(Math.pow(v1.getY() - v2.getY(), 2) + Math.pow((v1.getX() - v2.getX()), 2));
	}

	// Computes the CounterClockWise angle with respect to the X-Axis between two vertices.
	private double computeCCWAngle(Vertex v, Vertex reference) {
		double deltaX = v.getX() - reference.getX();
		double deltaY = v.getY() - reference.getY();
		double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
		if (angle < 0)
			angle += 360;
		return angle;
	}

	// Comparison based on the angle
	// If the angles match (colinear) then prioritize the vertex closer to the
	// reference.
	public int compareAngleAndProximity(Vertex reference, Vertex v1, Vertex v2) {
		int result = Double.compare(computeCCWAngle(v1, reference), computeCCWAngle(v2, reference));
		if (result == 0) {
			Double d1 = computeDistance(v1, reference);
			Double d2 = computeDistance(v2, reference);
			return d1 > d2 ? 1 : -1;
		}
		return result;
	}
	
	
	// Finds the intersection point between two edges (or null if there is none)
	//and returns it as a new Vertex.
	private Vertex getIntersectionPoint(Edge e1, Edge e2) {
		double x1 = e1.getStartVertex().getX();
		double y1 = e1.getStartVertex().getY();
		double x2 = e1.getEndVertex().getX();
		double y2 = e1.getEndVertex().getY();

		double x3 = e2.getStartVertex().getX();
		double y3 = e2.getStartVertex().getY();
		double x4 = e2.getEndVertex().getX();
		double y4 = e2.getEndVertex().getY();

		double ax = x2 - x1;
		double ay = y2 - y1;
		double bx = x4 - x3;
		double by = y4 - y3;

		double denominator = ax * by - ay * bx;

		if (denominator == 0)
			if (e1.getStartVertex().equals(e2.getStartVertex()) || e1.getStartVertex().equals(e2.getEndVertex())) {
				return e1.getStartVertex();
			} else if (e1.getEndVertex().equals(e2.getStartVertex()) || e1.getEndVertex().equals(e2.getEndVertex())) {
				return e1.getEndVertex();
			} else {
				return null;
			}

		double cx = x3 - x1;
		double cy = y3 - y1;

		double t = (cx * by - cy * bx) / (denominator);
		if (t < 0 || t > 1)
			return null;

		double u = (cx * ay - cy * ax) / (denominator);
		if (u < 0 || u > 1)
			return null;

		Vertex intersection = new Vertex((x1 + t * ax), (y1 + t * ay));
		return intersection;
	}
	
	// Computes the distance between a vertex "v" and the intersection point of two edges "e1" and "e2".
	// Useful to discern what intersections in a sweep-line are closed to the origin point. 
	private double getIntersectionDistance(Vertex v, Edge e1, Edge e2) {
		double x1, x2, x3, x4, y1, y2, y3, y4;

		x1 = e1.getStartVertex().getX();
		y1 = e1.getStartVertex().getY();
		x2 = e1.getEndVertex().getX();
		y2 = e1.getEndVertex().getY();
		x3 = e2.getStartVertex().getX();
		y3 = e2.getStartVertex().getY();
		x4 = e2.getEndVertex().getX();
		y4 = e2.getEndVertex().getY();

		double x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
				/ ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		double y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
				/ ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		return Math.sqrt(Math.pow(y - v.getY(), 2) + Math.pow((x - v.getX()), 2));
	}

	// Given an origin point "v" and a segment "e" coming out of it, compare the distance from said point
	// to the intersection points of two other segments with said segment.
	private int compareIntersectionDistance(Vertex v, Edge e, Edge e1, Edge e2) {
		return Double.compare(getIntersectionDistance(v, e, e1), getIntersectionDistance(v, e, e2));
	}

	// Computes a bounding box - Not currently used but might be useful at some point.
	private Polygon computeBoundingBox(Polygon p) {
		double minX, minY, maxX, maxY;
		minX = minY = Integer.MAX_VALUE;
		maxX = maxY = Integer.MIN_VALUE;

		for (Vertex v : p.getVertices()) {
			if (v.getX() > maxX)
				maxX = v.getX();
			if (v.getX() < minX)
				minX = v.getX();
			if (v.getY() > maxY)
				maxY = v.getY();
			if (v.getY() < minY)
				minY = v.getY();
		}
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(new Vertex(maxX, maxY));
		vertices.add(new Vertex(maxX, minY));
		vertices.add(new Vertex(minX, minY));
		vertices.add(new Vertex(minX, maxY));
		Polygon boundingBox = new Polygon(vertices);
		return boundingBox;
	}
	
	// Uses the Java AWT.Polygon class to check whether a point falls within a polygon.
	// First considers the bounding polygon, and subsequently checks for each hole polygon.
	private boolean insidePolygon(Vertex v, Polygon p) {
		java.awt.Polygon polygon = new java.awt.Polygon();
		p.getVertices().forEach(i -> polygon.addPoint((int) (i.getX()), (int) (i.getY())));

		// Construct the "hole" polygons.
		ArrayList<java.awt.Polygon> holes = new ArrayList<java.awt.Polygon>();
		for (Hole h : p.getHoles()) {
			java.awt.Polygon hole = new java.awt.Polygon();
			h.getVertices().forEach(i -> hole.addPoint((int) (i.getX()), (int) (i.getY())));
			holes.add(hole);
		}
		boolean inHole = holes.stream().anyMatch(h -> h.contains(v.getX(), v.getY()));
		
		// If the point is inside the main polygon, and inside no hole, then return true.
		return polygon.contains((v.getX()), (v.getY())) && !inHole;
	}

	private boolean areColinear(Vertex v1, Vertex v2, Vertex v3) {
		double area = v1.getX() * (v2.getY() - v3.getY()) + v2.getX() * (v3.getY() - v1.getY())
				+ v3.getX() * (v1.getY() - v2.getY());
		return (area == 0);
	}

	private int orientation(Vertex p, Vertex q, Vertex r) {
		double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
		if (val < 0) {
			return 0; // colinear
		} else {
			return (val > 0) ? 1 : 2; // clock or counterclock wise
		}

	}
}
