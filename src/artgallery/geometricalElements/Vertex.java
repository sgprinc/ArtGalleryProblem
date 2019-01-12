package artgallery.geometricalElements;

import java.util.ArrayList;

public class Vertex implements Comparable<Vertex> {
	// Basic vertex properties. 
	private String id;
	private double X;
	private double Y;
	private boolean art;
	private boolean exit;
	
	// List of edges "coming into" this vertex.
	private ArrayList<Edge> inEdges = new ArrayList<Edge>();
	// List of edges "coming out of" this vertex.
	private ArrayList<Edge> outEdges = new ArrayList<Edge>();
	// List of neighboring vertices for checks without edge-importance.
	private ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
	// Polygon object containing all that is visible from the given vertex.
	private ArrayList<Polygon> visibilityPolygon = new ArrayList<Polygon>();
	
	// Constructors
	public Vertex() {
		this.setArt(false);
		this.setExit(false);
	}

	public Vertex(double x, double y) {
		this.setId("("+(int)x+":"+(int)y+")");
		this.setX(x);
		this.setY(y);
		this.setArt(false);
		this.setExit(false);
	}

	public Vertex(double x, double y, String id, int artFlag, int exitFlag) {
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.setArt(artFlag == 1 ? true : false);
		this.setExit(exitFlag == 1 ? true : false);
	}

	// Basic property setters and accessors.	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(double y) {
		Y = y;
	}

	public boolean isArt() {
		return art;
	}

	public void setArt(boolean art) {
		this.art = art;
	}

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}	

	public Edge getInEdge(int i) {
		if(this.inEdges.size() > i) {
			return inEdges.get(i);
		}
		return null;
	}
	
	public void addInEdge(Edge e) {
		if(!this.inEdges.contains(e)) {
			this.inEdges.add(e);
		}		
	}

	public void setInEdges(ArrayList<Edge> inEdges) {
		this.inEdges = inEdges;
	}


	public boolean isInEdge(Edge e) {
		return this.inEdges.contains(e);
	}
	
	public Edge getEdgeOut(int i) {
		if(this.outEdges.size() > i) {
			return outEdges.get(i);
		}
		return null;
	}

	public void addOutEdge(Edge e) {
		if(!this.outEdges.contains(e)) {
			this.outEdges.add(e);
		}
	}
	
	public void setOutEdges(ArrayList<Edge> outEdges) {
		this.outEdges = outEdges;
	}
	
	public boolean isOutEdge(Edge e) {
		return this.outEdges.contains(e);
	}
	
	public void addNeighbor(Vertex n) {
		if (!neighbors.contains(n)) {
			neighbors.add(n);
		}
	}

	public void removeNeighbor(Vertex v) {
		if (neighbors.contains(v)) {
			v.neighbors.remove(this);
			neighbors.remove(v);
		}
	}

	public ArrayList<Vertex> getNeighbors() {
		return neighbors;
	}

	public boolean isNeighbor(Vertex v) {
		if (neighbors.contains(v)) {
			return true;
		}
		return false;
	}

	public ArrayList<Polygon> getVisibilityPolygon() {
		return visibilityPolygon;
	}

	public void setVisibilityPolygon(ArrayList<Polygon> visibilityPolygon) {
		this.visibilityPolygon = visibilityPolygon;
	}
	
	// Overridden functions from the Object class and interface methods to provide comparisons and printability.
	
	@Override
	// Overridden function to compare vertices on their coordinates rather than on object hashes.
	// X & Y can be deemed equal if they fall within an arbitrary tolerance to account for floating point error.
	public boolean equals(Object o) {
		if(o instanceof Vertex) {
			if(Math.abs(this.X - ((Vertex) o).getX()) < 0.01 && Math.abs(this.Y - ((Vertex) o).getY()) < 0.01) {
				return true;
			}
		}
		return false;		
	}
	
	@Override
	// Might need revision to ensure the correct orders are yielded
	public int compareTo(Vertex o) {
		if (this.equals(o)) {
			return 0;
		} else {
			return Double.compare(this.X, ((Vertex) o).getX());
		}
	}
	
	@Override
	// Returns a cloned vertex, preserving only it's basic properties (i.e. no neighbors or in/out edges) due to deep-copying issues.
	public Vertex clone(){
		Vertex clone = new Vertex(this.X, this.Y, this.id, this.art == true? 1: 0, this.exit == true ? 1: 0);
		return clone;
	}
	
	@Override
	// Simple method to obtain a readable representation of the vertex for labeling purposes.
	public String toString() {
		return "("+this.getX()+":"+this.getY()+")";
	}

}
