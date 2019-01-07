package artgallery;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import artgallery.Actors.*;
import artgallery.geometricalElements.*;

@SuppressWarnings("serial")
public class GalleryView extends JPanel {

	private GalleryModel gallery;
	private BufferedImage guardIcon;
	private BufferedImage thiefIcon;

	GalleryView() {
		gallery = null;
		loadResources();

	}

	GalleryView(GalleryModel gallery) {
		this.gallery = gallery;
		loadResources();
	}

	private void loadResources() {
		// Fetch the resources for the drawing of the library actors.
		try {
			guardIcon = ImageIO.read(getClass().getResource("/resources/guard.png"));
			thiefIcon = ImageIO.read(getClass().getResource("/resources/thief.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void updateGallery(GalleryModel updatedGallery) {
		this.gallery = updatedGallery;
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Hints for smooth rendering of shapes using anti-aliasing.
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);

		// Get dimensions of the canvas for scaling of the components rendering.
		Dimension size = getSize();
		double w = size.getWidth();
		double h = size.getHeight();

		// Transform the Java coordinate system to the Cartesian system.
		g2d.translate(w / 2, h / 2);
		g2d.scale(1, -1);

		if (this.gallery != null) {
			// Draw base surface polygon for the gallery.
			drawArea(g2d, gallery.getGallery(), null);

			// If selected, display the triangles from the gallery polygon
			// triangulation.
			if (gallery.showTriangulation()) {
				gallery.getGallery().computeTriangulation();
				drawTriangulation(g2d, gallery.getGallery().getTriangulation());
			}

			// If selected, display the visibility polygon of the guards.
			// Currently not fully implemented - only showing the polygon for vertex 1.
			// Perhaps tweak in the future to work on a "clicked guard" to make interactive & avoid overlapping.
			if (gallery.showVisibility()) {
				gallery.getGallery().computeVisibility();
				Color copBlue = new Color(100, 200, 250);
				
				for(artgallery.geometricalElements.Polygon visibleTriangle : gallery.getGallery().getVertices().get(0).getVisibilityPolygon()) {
					drawArea(g2d, visibleTriangle, copBlue);
				}
			}

			// Draw the gallery bounding edges.
			for (Edge edge : gallery.getGallery().getEdges()) {
				drawEdge(g2d, edge, null);
			}
			// Draw the gallery vertices.
			for (Vertex vertex : gallery.getGallery().getVertices()) {
				drawVertex(g2d, vertex, 12, null);
			}
			// Draw the gallery holes.
			for (Hole hole : gallery.getGallery().getHoles()) {
				drawHole(g2d, hole);
			}

			//Could be modified with the uninplemented abstract class for actors
			if (gallery.showActors()) {
				for (Guard guard : gallery.getGuards()) {
					if (gallery.showGuardsPaths()) {
						Color copBlue = new Color(100, 200, 250);
						for (Edge edge : guard.getRouteEdges()) {
							drawEdge(g2d, edge, copBlue);
						}
						for (RoutePoint point : guard.getRoutePoints()) {
							drawVertex(g2d, point.toVertex(), 6, copBlue);
						}
					}
					drawGuard(g2d, guard);
				}

				for (Thief thief : gallery.getThieves()) {
					if (gallery.showThievesPaths()) {
						Color thiefOrange = new Color(250, 150, 0);
						for (Edge edge : thief.getRouteEdges()) {
							drawEdge(g2d, edge, thiefOrange);
						}
						for (RoutePoint point : thief.getRoutePoints()) {
							drawVertex(g2d, point.toVertex(), 6, thiefOrange);
						}
					}
					drawThief(g2d, thief);
				}
			}

			if (gallery.showLegend()) {
				drawLegend(g2d, w, h);
			}
		}
	}

	private void drawVertex(Graphics2D g2d, Vertex v, int size, Color c) {
		Ellipse2D e = new Ellipse2D.Double(v.getX() - size / 2, v.getY() - size / 2, size, size);
		if (c == null) {
			if (v.isArt()) {
				g2d.setColor(Color.YELLOW);
				g2d.fill(e);
				g2d.setColor(g2d.getColor().brighter());
			} else if (v.isExit()) {
				g2d.setColor(Color.RED);
				g2d.fill(e);
				g2d.setColor(g2d.getColor().brighter());
			} else {
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.fill(e);
				g2d.setColor(g2d.getColor().brighter());
			}
		} else {
			g2d.setColor(c);
			g2d.fill(e);
			g2d.setColor(c.brighter());
		}
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(e);
	}

	private void drawEdge(Graphics2D g2d, Edge e, Color c) {
		Vertex v1 = e.getFirstVertex();
		Vertex v2 = e.getSecondVertex();
		c = c != null ? c : Color.LIGHT_GRAY;
		g2d.setColor(c);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(new Line2D.Double(v1.getX(), v1.getY(), v2.getX(), v2.getY()));
	}

	private void drawHole(Graphics2D g2d, Hole hole) {
		for (Edge e : hole.getEdges()) {
			drawEdge(g2d, e, null);
		}
		for (Vertex v : hole.getVertices()) {
			drawVertex(g2d, v, 8, null);
		}
	}

	private void drawArea(Graphics2D g2d, artgallery.geometricalElements.Polygon galleryPolygon, Color c) {
		// Create a Java polygon and load the vertices contained in the gallery
		// (non-Java) Polygon.
		Polygon p = new Polygon();
		for (Vertex v : galleryPolygon.getVertices()) {
			p.addPoint(v.getX(), v.getY());
		}
		Area surface = new Area(p);

		// Subtract all the holes from the gallery surface
		for (Hole hole : galleryPolygon.getHoles()) {
			Polygon h = new Polygon();
			for (Vertex v : hole.getVertices()) {
				h.addPoint(v.getX(), v.getY());
			}
			Area currentHole = new Area(h);
			surface.subtract(currentHole);
		}
		
		if(c == null) {
			Color surfaceGray = new Color(45, 45, 45);
			c = surfaceGray; 
		}		
		g2d.setColor(c);
		g2d.fill(surface);
	}
	
	private void drawTriangulation(Graphics2D g2d, ArrayList<artgallery.geometricalElements.Polygon> triangulation) {
		for (artgallery.geometricalElements.Polygon p : triangulation) {
			int vertexSize = 8;
			Vertex v = new Vertex(p.getCentroid()[0], p.getCentroid()[1] + vertexSize / 2);
			drawVertex(g2d, v, vertexSize, Color.GREEN);
			for (Edge e : p.getEdges()) {
				drawEdge(g2d, e, new Color(0, 255, 0));
			}
		}

		/*
		 * //Delete for (Vertex vertex: gallery.getGallery().getVertices()){
		 * drawVertex(g2d, vertex, 12, null); g2d.scale(1, -1);
		 * g2d.drawString("" +
		 * gallery.getTriangulationVertices().indexOf(vertex),
		 * (float)vertex.getX() - 4, -(float)vertex.getY() - 15); g2d.scale(1,
		 * -1); }
		 */

	}

	private void drawGuard(Graphics2D g2d, Guard guard) {
		g2d.scale(0.2, -0.2);
		g2d.drawImage(guardIcon, guard.getX() * 5 - 50, -guard.getY() * 5 - 50, null);
		g2d.scale(5, -5);
	}

	private void drawThief(Graphics2D g2d, Thief thief) {
		g2d.scale(0.2, -0.2);
		g2d.drawImage(thiefIcon, thief.getX() * 5 - 50, -thief.getY() * 5 - 50, null);
		g2d.scale(5, -5);
	}

	private void drawLegend(Graphics2D g2d, double w, double h) {
		Rectangle2D r = new Rectangle2D.Double(w / 2 - 185, -h / 2 + 5, 180, 50);
		int alpha = 127; // 50% transparent
		Color translucidGray = new Color(150, 150, 150, alpha);
		g2d.setColor(translucidGray);
		g2d.fill(r);
		g2d.draw(r);

		int vertexSize = 10;

		Ellipse2D e = new Ellipse2D.Double(w / 2 - 110, -h / 2 + 40, vertexSize, vertexSize);
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fill(e);
		g2d.setColor(g2d.getColor().brighter());
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(e);
		g2d.scale(1, -1);
		g2d.drawString("Normal Vertex", (float) (w / 2 - 90), (float) (h / 2 - 40));
		g2d.scale(1, -1);

		e = new Ellipse2D.Double(w / 2 - 110, -h / 2 + 25, vertexSize, vertexSize);
		g2d.setColor(Color.RED);
		g2d.fill(e);
		g2d.setColor(g2d.getColor().brighter());
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(e);
		g2d.setColor(Color.WHITE);
		g2d.scale(1, -1);
		g2d.drawString("Exit Point", (float) (w / 2 - 90), (float) (h / 2 - 25));
		g2d.scale(1, -1);

		e = new Ellipse2D.Double(w / 2 - 110, -h / 2 + 10, vertexSize, vertexSize);
		g2d.setColor(Color.YELLOW);
		g2d.fill(e);
		g2d.setColor(g2d.getColor().brighter());
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(e);
		g2d.setColor(Color.WHITE);
		g2d.scale(1, -1);
		g2d.drawString("Art Piece", (float) (w / 2 - 90), (float) (h / 2 - 10));
		g2d.scale(1, -1);

		g2d.scale(0.2, -0.2);
		g2d.drawImage(guardIcon, (int) (w / 2 - 180) * 5, (int) (h / 2 - 50) * 5, null);
		g2d.drawImage(thiefIcon, (int) (w / 2 - 180) * 5, (int) (h / 2 - 28) * 5, null);
		g2d.scale(5, 5);
		g2d.drawString("Guard", (float) (w / 2 - 155), (float) (h / 2 - 38));
		g2d.drawString("Thief", (float) (w / 2 - 155), (float) (h / 2 - 15));
		g2d.scale(1, -1);
	}
}
