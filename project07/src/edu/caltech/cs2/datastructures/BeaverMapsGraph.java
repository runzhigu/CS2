package edu.caltech.cs2.datastructures;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.ISet;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.io.FileReader;
import java.io.IOException;



public class BeaverMapsGraph extends Graph<Long, Double> {
    private IDictionary<Long, Location> ids;
    private ISet<Location> buildings;

    public BeaverMapsGraph() {
        super();
        this.buildings = new ChainingHashSet<>();
        this.ids = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    /**
     * Reads in buildings, waypoinnts, and roads file into this graph.
     * Populates the ids, buildings, vertices, and edges of the graph
     * @param buildingsFileName the buildings filename
     * @param waypointsFileName the waypoints filename
     * @param roadsFileName the roads filename
     */
    public BeaverMapsGraph(String buildingsFileName, String waypointsFileName, String roadsFileName) {
        this();
        JsonElement bs = fromFile(buildingsFileName);
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            //System.out.println("One of the locations is " + loc);
            this.ids.put(loc.id, loc);
            this.buildings.add(loc);
            this.addVertex(loc.id);
        }
        JsonElement waypoint = fromFile(waypointsFileName);
        for (JsonElement w : waypoint.getAsJsonArray()) {
            Location loc = new Location(w.getAsJsonObject());
            //System.out.println("One of the waypoints is " + loc);
            this.ids.put(loc.id, loc);
            this.addVertex(loc.id);
        }
        ISet<Long> verticesSet = this.vertices();
        JsonElement road = fromFile(roadsFileName);
        for (JsonElement r : road.getAsJsonArray()) {
            JsonArray roadIDs = r.getAsJsonArray();
            long prev = 0;
            for (JsonElement o : roadIDs) {
                long curr = o.getAsLong();
                if (prev != 0) {
                    double distance = this.ids.get(curr).getDistance(this.ids.get(prev));
                    this.addEdge(curr, prev, distance);
                    this.addEdge(prev, curr, distance);
                }
                prev = curr;
            }
            //
            //System.out.println("One of the waypoints is " + loc);
        }

    }

    /**
     * Returns a deque of all the locations with the name locName.
     * @param locName the name of the locations to return
     * @return a deque of all location with the name locName
     */
    public IDeque<Location> getLocationByName(String locName) {
        IDeque<Location> allLocations = new ArrayDeque<>();
        for (Location loc : this.ids.values()) {
            if (loc.name != null && loc.name.equals(locName)) {
                allLocations.add(loc);
            }
        }
        return allLocations;
    }

    /**
     * Returns the Location object corresponding to the provided id
     * @param id the id of the object to return
     * @return the location identified by id
     */
    public Location getLocationByID(long id) {
        return this.ids.get(id);
    }

    /**
     * Adds the provided location to this map.
     * @param n the location to add
     * @return true if n is a new location and false otherwise
     */
    public boolean addVertex(Location n) {
        if (this.ids.containsKey(n.id)) {
            return false;
        }
        else {
            this.ids.put(n.id, n);
            this.addVertex(n.id);
            if (n.type.equals("building")) {
                this.buildings.add(n);
            }
            return true;
        }
    }

    /**
     * Returns the closest building to the location (lat, lon)
     * @param lat the latitude of the location to search near
     * @param lon the longitute of the location to search near
     * @return the building closest to (lat, lon)
     */
    public Location getClosestBuilding(double lat, double lon) {
        Location closest = null;
        double min = Double.MAX_VALUE;
        for (Location building : this.buildings) {
            double curr = building.getDistance(lat, lon);
            if (curr < min) {
                min = curr;
                closest = building;
            }
        }
        return closest;
    }

    /**
     * Returns a set of locations which are reachable along a path that goes no further than `threshold` feet from start
     * @param start the location to search around
     * @param threshold the number of feet in the search radius
     * @return
     */
    public ISet<Location> dfs(Location start, double threshold) {
        long current = start.id;
        ISet<Location> reachableLocations = new ChainingHashSet<>();
        LinkedDeque<Long> nodeStack = new LinkedDeque<>();
        nodeStack.push(start.id);
        ISet<Long> visited = new ChainingHashSet<>();
        while(!nodeStack.isEmpty()) {
            current = nodeStack.pop();
             if (start.getDistance(this.ids.get(current)) <= threshold){
                reachableLocations.add(this.ids.get(current));
                for (Long vertex : neighbors(current)) {
                    if (!reachableLocations.contains(this.ids.get(vertex))) {
                        nodeStack.push(vertex);
                    }
                }
            }
        }
        return reachableLocations;
    }
//    public void solveDFSIterative() {
//
//        //selectPoint(current);
//
//        while (!current.equals(this.end)) {
//            for (Point p : neighbors(current)) {
//                pointsStack.push(p);
//            }
//            current = pointsStack.pop();
//            selectPoint(current);
//        }
//    }
    /**
     * Returns a list of Locations corresponding to
     * buildings in the current map.
     * @return a list of all building locations
     */
    public ISet<Location> getBuildings() {
        return this.buildings;
    }

    /**
     * Returns a shortest path (i.e., a deque of vertices) between the start
     * and target locations (including the start and target locations).
     * @param start the location to start the path from
     * @param target the location to end the path at
     * @return a shortest path between start and target
     */
    public IDeque<Location> dijkstra(Location start, Location target) {
        if (start.equals(target)) {
            LinkedDeque<Location> path = new LinkedDeque<>();
            path.add(start);
            return path;
        }
        // create dictionary of distances
        ChainingHashDictionary<Long, Double> distanceDict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        ChainingHashDictionary<Long, Long> parentDict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

        // worklist of locations where the distance in the priority
        MinFourHeap<Long> workList = new MinFourHeap();

        // populate distanceDict; iterate thru vertices (waypoints / buildings)
        for (Long vertex : this.vertices()) {
            if (vertex.equals(start.id)) {
                distanceDict.put(vertex, 0.0);
            }
            else {
                distanceDict.put(vertex, Double.MAX_VALUE);
            }
            //
            workList.enqueue(new IPriorityQueue.PQElement<Long>(vertex, distanceDict.get(vertex)));
        }

        // now Dijkstra
        while (!workList.peek().data.equals(target.id) && workList.size() != 0) {
            IPriorityQueue.PQElement<Long> vertex = workList.dequeue();
            for (Long neighbor : neighbors(vertex.data)) {
                if (!neighbor.equals(target.id) && ids.get(neighbor).type == Location.Type.BUILDING) {
                    continue;
                }
                // if distance changes
                double potentialDistance = distanceDict.get(vertex.data) + adjacent(vertex.data, neighbor);
                if (distanceDict.get(neighbor) > potentialDistance) {
                    parentDict.put(neighbor, vertex.data);
                    distanceDict.put(neighbor, potentialDistance);
                    workList.decreaseKey(new IPriorityQueue.PQElement<Long>(neighbor, distanceDict.get(neighbor)));
                }
//                double min = Math.min(
//                        distanceDict.get(neighbor),
//                        distanceDict.get(vertex.data) + adjacent(vertex.data, neighbor)
//                );
//                distanceDict.put(neighbor, min);
            }
        }

        LinkedDeque<Location> path = new LinkedDeque<>();
        Long current = target.id;
        path.addFront(target);
        while (!current.equals(start.id)) {
            if (!parentDict.containsKey(current)) {
                return null;
            }
            path.addFront(ids.get(parentDict.get(current)));
            current = parentDict.get(current);
        }

        return path;
    }

    /**
     * Returns a JsonElement corresponding to the data in the file
     * with the filename filename
     * @param filename the name of the file to return the data from
     * @return the JSON data from filename
     */
    private static JsonElement fromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            return null;
        }
    }
}
