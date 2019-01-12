package artgallery.geometricalElements;

import java.util.ArrayList;

public class Edge implements Comparable<Edge> {
	// Basic vertex properties.
	private Vertex startVertex;
	private Vertex endVertex;
	
	// List of neighboring edges for checks without edge-importance.
	private ArrayList<Edge> neighbors = new ArrayList<Edge>();
	
	private Edge next, prev;

	// Constructors
	public Edge() {
		setStartVertex(new Vertex());
		setEndVertex(new Vertex());
		setNext(null);
		setPrev(null);
	}

	public Edge(Vertex v1, Vertex v2) {
		setStartVertex(v1);
		setEndVertex(v2);
		v1.addNeighbor(v2);
		v2.addNeighbor(v1);
	}

	// Basic property setters and accessors.
	
	public Vertex getStartVertex() {
		return startVertex;
	}

	public void setStartVertex(Vertex startVertex) {
		this.startVertex = startVertex;
	}

	public Vertex getEndVertex() {
		return endVertex;
	}

	public void setEndVertex(Vertex endVertex) {
		this.endVertex = endVertex;
	}

	// Returns the vertex in the edge that is not the specified one.
	public Vertex getOtherVertex(Vertex v) {
		if (this.containsVertex(v)) {
			return startVertex.equals(v) ? endVertex : startVertex;
		}
		return null;
	}
	
	public Edge getNext() {
		return next;
	}

	public void setNext(Edge next) {
		this.next = next;
	}

	public Edge getPrev() {
		return prev;
	}

	public void setPrev(Edge prev) {
		this.prev = prev;
	}

	public boolean containsVertex(Vertex v) {
		if (startVertex.equals(v) || endVertex.equals(v)) {
			return true;
		}
		return false;
	}

	public void addNeighbor(Edge e) {
		if (!neighbors.contains(e)) {
			neighbors.add(e);
		}
	}

	public void removeNeighbor(Edge e) {
		if (neighbors.contains(e)) {
			e.neighbors.remove(this);
			neighbors.remove(e);
		}
	}

	public ArrayList<Edge> getNeighbors() {
		return neighbors;
	}

	public boolean isNeighbor(Edge e) {
		if (neighbors.contains(e)) {
			return true;
		}
		return false;
	}
	
	// Returns a shallow Vertex object with Cartesian coordinates equal to the midpoint of the edge.
	public Vertex getMidpoint() {
		double midX = startVertex.getX() + (endVertex.getX() - startVertex.getX()) / 2;
		double midY = startVertex.getY() + (endVertex.getY() - startVertex.getY()) / 2;
		return new Vertex(midX, midY);
	}	

	// Returns the euclidian distance between the two defining vertices.
	public double size() {
		double length = Math.pow(startVertex.getY() - endVertex.getY(), 2)
				+ Math.pow((startVertex.getX() - endVertex.getX()), 2);
		return length;
	}
	

	// Overridden functions from the Object class and interface methods to provide comparisons and printability.
	
	@Override
	// Overridden function to compare edges on the equality of its vertices.
	public boolean equals(Object o) {
		if (o instanceof Edge) {
			if (this.containsVertex(((Edge) o).getStartVertex()) && this.containsVertex(((Edge) o).getEndVertex())) {
				return true;
			}
		}
		return false;
	}

	@Override
	// Might need revision to ensure the correct orders are yielded
	public int compareTo(Edge edge) {
		if (this.equals(edge)) {
			return 0;
		} else {
			return -1;
		}
	}
	
	@Override
	// Simple method to obtain a readable representation of the edge for labeling purposes.
	public String toString() {
		return this.startVertex.toString() + this.endVertex.toString();
	}


}
