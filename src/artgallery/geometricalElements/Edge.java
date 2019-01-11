package artgallery.geometricalElements;

import java.util.ArrayList;

public class Edge implements Comparable<Edge> {
	private String id;
	private int length;
	private Vertex firstVertex;
	private Vertex secondVertex;
	private ArrayList<Edge> neighbors = new ArrayList<Edge>();

	public Edge() {
		setFirstVertex(new Vertex());
		setSecondVertex(new Vertex());
	}

	public Edge(Vertex v1, Vertex v2) {
		setFirstVertex(v1);
		setSecondVertex(v2);
		setId(v1.getId() + "-" + v2.getId());
		v1.addNeighbor(v2);
		v2.addNeighbor(v1);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double length() {
		double length = Math.pow(firstVertex.getY() - secondVertex.getY(), 2)
				+ Math.pow((firstVertex.getX() - secondVertex.getX()), 2);
		return length;
	}

	public Vertex getFirstVertex() {
		return firstVertex;
	}

	public void setFirstVertex(Vertex firstVertex) {
		this.firstVertex = firstVertex;
	}

	public Vertex getSecondVertex() {
		return secondVertex;
	}

	public void setSecondVertex(Vertex secondVertex) {
		this.secondVertex = secondVertex;
	}

	public Vertex getOtherVertex(Vertex v) {
		if (this.containsVertex(v)) {
			return firstVertex.equals(v) ? secondVertex : firstVertex;
		}
		return null;
	}

	public boolean containsVertex(Vertex v) {
		if (firstVertex.equals(v) || secondVertex.equals(v)) {
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
	
	public Vertex getMidpoint() {
		double midX = firstVertex.getX() + (secondVertex.getX() - firstVertex.getX()) / 2;
		double midY = firstVertex.getY() + (secondVertex.getY() - firstVertex.getY()) / 2;
		return new Vertex(midX, midY);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		if (o instanceof Edge) {
			if (this.containsVertex(((Edge) o).getFirstVertex()) && this.containsVertex(((Edge) o).getSecondVertex())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(Edge edge) {
		if (this.equals(edge)) {
			return 0;
		} else {
			return this.getId().compareTo(edge.getId());
		}
	}

}
