package artgallery.Actors;

import java.util.ArrayList;

import artgallery.geometricalElements.Edge;
import artgallery.geometricalElements.RoutePoint;

public abstract class Actor {
	private int x;
	private int y;
	private int currentPointIndex;
	private int orientation;
	private int maxSpeed;
	private ArrayList<RoutePoint> routePoints = new ArrayList<RoutePoint>();
	private ArrayList<Edge> routeEdges = new ArrayList<Edge>();
	private int routeLength;
	boolean loopPath = false;

}
