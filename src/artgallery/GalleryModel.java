package artgallery;
import java.util.ArrayList;

import artgallery.Actors.Guard;
import artgallery.Actors.Thief;
import artgallery.geometricalElements.Polygon;

public class GalleryModel {
	private ArrayList<Guard> guards = new ArrayList<Guard>();
	private ArrayList<Thief> thieves = new ArrayList<Thief>();
	private Polygon galleryPolygon;
	private boolean actors = false;
	private boolean legend = false;
	private boolean guardsPaths = false;
	private boolean thievesPaths = false;
	private boolean triangulation = false;
	private boolean visibility = false;
	private int globalTime;

	public GalleryModel() {
		this.setGallery(new Polygon());
	}

	public GalleryModel(Polygon polygon, int globalTime) {
		this.setGallery(polygon);
		this.setGlobalTime(globalTime);
	}

	public Polygon getGallery() {
		return galleryPolygon;
	}

	public void setGallery(Polygon galleryPolygon) {
		this.galleryPolygon = galleryPolygon;
	}
	public int getGlobalTime() {
		return globalTime;
	}

	public void setGlobalTime(int globalTime) {
		this.globalTime = globalTime;
	}
	
	/* Callable method to begin the sequence of operation required to triangulate the art gallery.
	 * 1) Decompose the gallery into trapezoids.
	 * 2) Decompose the trapezoids into Y-Monotone polygons.
	 * 3) Triangulate the Y-Monotone polygons.
	 * 4) Merge results to obtain triangulation mesh.
	 */
	
	public void toggleActors() {
		this.actors = this.actors == true ? false : true;
	}
	public void toggleLegend() {
		this.legend = this.legend == true ? false : true;
	}
	public void toggleGuardsPaths() {
		this.guardsPaths = this.guardsPaths == true ? false : true;
	}
	public void toggleThievesPaths() {
		this.thievesPaths = this.thievesPaths == true ? false : true;
	}
	public void toggleTriangulation() {
		this.triangulation = this.triangulation == true ? false : true;
	}
	public void toggleVisibility() {
		this.visibility = this.visibility == true ? false : true;
	}

	public boolean showActors() {
		return actors;
	}
	public boolean showLegend() {
		return legend;
	}
	public boolean showGuardsPaths() {
		return guardsPaths;
	}
	public boolean showThievesPaths() {
		return thievesPaths;
	}
	public boolean showTriangulation() {
		return triangulation;
	}
	public boolean showVisibility() {
		return visibility;
	}

	public ArrayList<Guard> getGuards() {
		return guards;
	}

	public void setGuards(ArrayList<Guard> guards) {
		this.guards = guards;
	}

	public ArrayList<Thief> getThieves() {
		return thieves;
	}

	public void setThieves(ArrayList<Thief> thieves) {
		this.thieves = thieves;
	}
}
