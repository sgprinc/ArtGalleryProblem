package artgallery.geometricalElements;

public class Edge implements Comparable<Edge>{
	private String id;
	private Vertex firstVertex;
	private Vertex secondVertex;
	
	public Edge(){}
	public Edge(Vertex v1, Vertex v2){
		setFirstVertex(v1);
		setSecondVertex(v2);
		setId(v1 + "-" + v2);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Vertex getFirstVertex() {
		return firstVertex;
	}
	public void setFirstVertex(Vertex firstVertex) {
		this.firstVertex = firstVertex;
		this.firstVertex.addInEdge(this);
	}	
	public Vertex getSecondVertex() {
		return secondVertex;
	}
	public void setSecondVertex(Vertex secondVertex) {
		this.secondVertex = secondVertex;
		this.secondVertex.addInEdge(this);
	}
	
	public boolean containsVertex(Vertex v){
		if(firstVertex.equals(v) || secondVertex.equals(v)){
			return true;
		}
		return false;
	}
		
	@Override
	public int compareTo(Edge edge) {
		return this.getId().compareTo(edge.getId());
	}
	
}
