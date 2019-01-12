package artgallery.geometricalElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import artgallery.GeometricAlgorithms;

public class Polygon {
	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private ArrayList<Hole> holes = new ArrayList<Hole>();
	private ArrayList<Polygon> triangulation = new ArrayList<Polygon>();
	private Map<Vertex, Integer> chainMap = new HashMap<Vertex, Integer>();

	public Polygon() {
	}

	@SuppressWarnings("unchecked")
	// Single constructor for both Edge and Vertex list inputs as Java does not
	// distinguish between generics at compile time, hence not allowing for two
	// different ones.
	public Polygon(ArrayList<?> elements) {
		// If the argument was a list of vertices
		if (elements.get(0) instanceof Vertex) {
			this.vertices = (ArrayList<Vertex>) elements;
			generateEdges();

		}
		// If the argument was a list of edges
		else if (elements.get(0) instanceof Edge) {
			this.edges = (ArrayList<Edge>) elements;
			for (Edge e : this.edges) {
				if (!this.vertices.contains(e.getStartVertex())) {
					this.vertices.add(e.getStartVertex());
				}
				if (!this.vertices.contains(e.getEndVertex())) {
					this.vertices.add(e.getEndVertex());
				}
			}
		}
	}

	public Polygon(ArrayList<Vertex> vertices, ArrayList<Edge> edges, ArrayList<Hole> holes) {
		this.vertices = vertices;
		this.edges = edges;
		this.holes = holes;
	}

	public Polygon(ArrayList<Vertex> vertices, ArrayList<Hole> holes) {
		this.vertices = vertices;
		generateEdges();

		this.holes = holes;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public ArrayList<Vertex> getVerticesAndHoles() {
		ArrayList<Vertex> allVertices = new ArrayList<Vertex>(vertices);
		for (Hole h : holes) {
			allVertices.addAll(new ArrayList<Vertex>(h.getVertices()));
		}
		return allVertices;
	}
	
	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
	}
	
	public boolean containsVertex(Vertex v) {
		if(this.vertices.contains(v)) {
			return true;
		}
		return false;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public ArrayList<Edge> getEdgesAndHoles() {
		ArrayList<Edge> allEdges = new ArrayList<Edge>(edges);
		for (Hole h : holes) {
			allEdges.addAll(new ArrayList<Edge>(h.getEdges()));
		}
		return allEdges;
	}
	
	public boolean containsEdge(Edge e) {
		if(this.edges.contains(e)) {
			return true;
		}
		return false;
	}

	public Vertex getMatchingVertex(Vertex vertex) {
		for (Vertex localVertex : this.getVerticesAndHoles()) {
			if (localVertex.equals(vertex)) {
				return localVertex;
			}
		}
		return null;
	}

	// Method to automatically generate the edges in a circularly-linked polygon
	// from its vertices. Assumed the order of the vertices is correct, and was
	// given in a clockwise manner.
	public void generateEdges() {
		// Construct the edges for the gallery bounds by connecting points
		// clockwise as they were provided.
		for (int i = 0; i < vertices.size(); ++i) {
			Vertex v1 = vertices.get(i);
			Vertex v2 = vertices.get((i + 1) % vertices.size());
			Edge newEdge = new Edge(v1, v2);
			this.edges.add(newEdge);
			v1.addNeighbor(v2);
			v2.addNeighbor(v1);
			v1.addOutEdge(newEdge);
			v2.addInEdge(newEdge);

			// Connect edges to their respective predecessors and successors.
			if (i > 0) {
				Edge e1 = edges.get(i - 1);
				Edge e2 = edges.get((i) % edges.size());
				e1.addNeighbor(e2);
				e2.addNeighbor(e1);
				e1.setNext(e2);
				e2.setPrev(e1);
			}
		}
	}

	// Methods relying on the Geometrical Algorithms class

	public ArrayList<Polygon> computeTriangulation() {
		if (this.triangulation.isEmpty()) {
			GeometricAlgorithms GA = new GeometricAlgorithms();
			triangulation = GA.computeTriangulation(this);
			return triangulation;
		} else {
			return this.triangulation;
		}
	}

	public void computeVisibility(Vertex v) {
		GeometricAlgorithms GA = new GeometricAlgorithms();
		ArrayList<Polygon> visibilityPolygons = GA.computeVisibilityPolygon(v, this);
		v.setVisibilityPolygon(visibilityPolygons);
	}

	// Returns the maximum euclidian distance between any two vertices in the
	// graph. Used for the sweep-line endpoint.
	public double getMaxDistance() {
		double maxDistance = 0;
		for (Vertex v1 : vertices) {
			for (Vertex v2 : vertices) {
				double tempDistance = Math
						.sqrt(Math.pow(v1.getY() - v2.getY(), 2) + Math.pow((v1.getX() - v2.getX()), 2));
				if (tempDistance > maxDistance) {
					maxDistance = tempDistance;
				}
			}
		}
		return maxDistance;
	}

	// Bad implementation for splitting the vertices in a polygon boundary into
	// two monotone-chains. Used for the monotone-triangulation.
	// Should be changed into finding the breakpoint (index) at which the chains split.
	public void constructChains() {
		int top = 0;
		for (int i = 0; i < vertices.size() - 1; ++i) {
			if (vertices.get(i).getY() > vertices.get(top).getY()) {
				top = i;
			}
		}
	}

	public int getChain(Vertex v) {
		return chainMap.get(v);
	}

	public ArrayList<Hole> getHoles() {
		return holes;
	}

	public void setHoles(ArrayList<Hole> holes) {
		this.holes = holes;
	}

	public int[] getCentroid() {
		int[] centroid = { 0, 0 };
		for (Vertex v : vertices) {
			centroid[0] += v.getX();
			centroid[1] += v.getY();
		}
		centroid[0] /= vertices.size();
		centroid[1] /= vertices.size();
		return centroid;
	}
	

	// Modified the vertices positions to avoid horizontal lines in the polygon.
	// Implementation probably broken as the professors didn't specify the origin point for the galleries.
	public void tiltPolygon() {
		int[] centroid = getCentroid();
		double tiltAngle = Math.toRadians(10);
		
		for(Vertex vertex : this.vertices) {
			double x = vertex.getX();
			double y = vertex.getY();
			double newX = ((x - centroid[0]) * Math.cos(tiltAngle) + (y - centroid[1]) * Math.sin(tiltAngle)) + centroid[0];
			double newY = (-(x - centroid[0]) * Math.sin(tiltAngle) + (y - centroid[1]) * Math.cos(tiltAngle)) + centroid[1];
			vertex.setX(newX);
			vertex.setY(newY);
		}
	}

	// Overridden functions from the Object class and interface methods to provide comparisons and printability.
	
	@Override
	// Compares the equality of two polygons based on the exact similarity of their edges.
	// Used to avoid polygon duplicates when constructing triangulations from edges.
	public boolean equals(Object p) {
		if (p instanceof Polygon) {
			boolean equal = true;
			boolean contained = false;
			
			// Check if all of this polygon's edges are in the other polygon
			for (Edge e : this.getEdges()) {
				if(((Polygon) p).containsEdge(e)) {
					contained = true;
				}
				equal = equal & contained;
			}
			
			// Check if all of the other polygon's edges are in this polygon.
			contained = false;
			for (Edge e : ((Polygon) p).getEdges()) {
				if(this.containsEdge(e)){
					contained = true;
				}
				equal = equal & contained;
			}
			return equal;
		}
		return false;
	}

	@Override
	// Attempts to return a deep copy of the polygon, but potentially broken due to reference hell.
	public Polygon clone() {
		Polygon clone = new Polygon();
		ArrayList<Vertex> cloneVertices = new ArrayList<Vertex>();
		for (Vertex v : this.getVertices()) {
			cloneVertices.add(v.clone());
		}
		clone.setVertices(cloneVertices);
		clone.setHoles(new ArrayList<Hole>(this.getHoles()));
		clone.generateEdges();
		return clone;
	}

}