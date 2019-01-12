import static org.junit.Assert.*;

import java.util.ArrayList;

import artgallery.*;
import artgallery.geometricalElements.*;




public class Test {
	
	
	
	@org.junit.Test
	public void test() {
		GeometricAlgorithms GA = new GeometricAlgorithms();
		
		Vertex v1 = new Vertex(0, 0);
		Vertex v2 = new Vertex(100, -100);
		Vertex v3 = new Vertex(0, -100);
		Vertex v4 = new Vertex(-100, -100);
		Vertex v5 = new Vertex(100, 100);
				
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(v3);
		vertices.add(v4);
		vertices.add(v2);
		vertices.add(v5);
		
		Polygon galleryPolygon = new Polygon(vertices, new ArrayList<Hole>());
		GalleryModel gallery = new GalleryModel();
		gallery.setGallery(galleryPolygon);	
		
		
		vertices.sort((o1, o2) -> GA.compareAngleAndProximity(v1, o1, o2));
		vertices.stream().forEach(v -> System.out.println(v.toString()));
		
		//GA.computeVisibilityPolygon(v1, galleryPolygon);
		
	}

}
