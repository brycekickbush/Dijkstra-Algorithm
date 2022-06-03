//--== CS400 File Header Information ==--
//Name: <Bryce Kickbush>
//Email: <kickbush@wisc.edu>
//Team: <Blue>
//Group: <EF>
//TA: <Yelun Bao>
//Lecturer: <Gary Dahl>
//Notes to Grader:
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.NoSuchElementException;

public class CS400Graph<T> implements GraphADT<T> {

  /**
   * Vertex objects group a data field with an adjacency list of weighted directed edges that lead
   * away from them.
   */
  protected class Vertex {
    public T data; // vertex label or application specific data
    public LinkedList<Edge> edgesLeaving;

    public Vertex(T data) {
      this.data = data;
      this.edgesLeaving = new LinkedList<>();
    }
  }

  /**
   * Edge objects are stored within their source vertex, and group together their target destination
   * vertex, along with an integer weight.
   */
  protected class Edge {
    public Vertex target;
    public int weight;

    public Edge(Vertex target, int weight) {
      this.target = target;
      this.weight = weight;
    }
  }

  protected Hashtable<T, Vertex> vertices; // holds graph verticies, key=data

  public CS400Graph() {
    vertices = new Hashtable<>();
  }

  /**
   * Insert a new vertex into the graph.
   * 
   * @param data the data item stored in the new vertex
   * @return true if the data can be inserted as a new vertex, false if it is already in the graph
   * @throws NullPointerException if data is null
   */
  public boolean insertVertex(T data) {
    if (data == null)
      throw new NullPointerException("Cannot add null vertex");
    if (vertices.containsKey(data))
      return false; // duplicate values are not allowed
    vertices.put(data, new Vertex(data));
    return true;
  }

  /**
   * Remove a vertex from the graph. Also removes all edges adjacent to the vertex from the graph
   * (all edges that have the vertex as a source or a destination vertex).
   * 
   * @param data the data item stored in the vertex to remove
   * @return true if a vertex with *data* has been removed, false if it was not in the graph
   * @throws NullPointerException if data is null
   */
  public boolean removeVertex(T data) {
    if (data == null)
      throw new NullPointerException("Cannot remove null vertex");
    Vertex removeVertex = vertices.get(data);
    if (removeVertex == null)
      return false; // vertex not found within graph
    // search all vertices for edges targeting removeVertex
    for (Vertex v : vertices.values()) {
      Edge removeEdge = null;
      for (Edge e : v.edgesLeaving)
        if (e.target == removeVertex)
          removeEdge = e;
      // and remove any such edges that are found
      if (removeEdge != null)
        v.edgesLeaving.remove(removeEdge);
    }
    // finally remove the vertex and all edges contained within it
    return vertices.remove(data) != null;
  }

  /**
   * Insert a new directed edge with a positive edge weight into the graph.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @param weight the weight for the edge (has to be a positive integer)
   * @return true if the edge could be inserted or its weight updated, false if the edge with the
   *         same weight was already in the graph
   * @throws IllegalArgumentException if either source or target or both are not in the graph, or if
   *                                  its weight is < 0
   * @throws NullPointerException     if either source or target or both are null
   */
  public boolean insertEdge(T source, T target, int weight) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot add edge with null source or target");
    Vertex sourceVertex = this.vertices.get(source);
    Vertex targetVertex = this.vertices.get(target);
    if (sourceVertex == null || targetVertex == null)
      throw new IllegalArgumentException("Cannot add edge with vertices that do not exist");
    if (weight < 0)
      throw new IllegalArgumentException("Cannot add edge with negative weight");
    // handle cases where edge already exists between these verticies
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex) {
        if (e.weight == weight)
          return false; // edge already exists
        else
          e.weight = weight; // otherwise update weight of existing edge
        return true;
      }
    // otherwise add new edge to sourceVertex
    sourceVertex.edgesLeaving.add(new Edge(targetVertex, weight));
    return true;
  }

  /**
   * Remove an edge from the graph.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @return true if the edge could be removed, false if it was not in the graph
   * @throws IllegalArgumentException if either source or target or both are not in the graph
   * @throws NullPointerException     if either source or target or both are null
   */
  public boolean removeEdge(T source, T target) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot remove edge with null source or target");
    Vertex sourceVertex = this.vertices.get(source);
    Vertex targetVertex = this.vertices.get(target);
    if (sourceVertex == null || targetVertex == null)
      throw new IllegalArgumentException("Cannot remove edge with vertices that do not exist");
    // find edge to remove
    Edge removeEdge = null;
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex)
        removeEdge = e;
    if (removeEdge != null) { // remove edge that is successfully found
      sourceVertex.edgesLeaving.remove(removeEdge);
      return true;
    }
    return false; // otherwise return false to indicate failure to find
  }

  /**
   * Check if the graph contains a vertex with data item *data*.
   * 
   * @param data the data item to check for
   * @return true if data item is stored in a vertex of the graph, false otherwise
   * @throws NullPointerException if *data* is null
   */
  public boolean containsVertex(T data) {
    if (data == null)
      throw new NullPointerException("Cannot contain null data vertex");
    return vertices.containsKey(data);
  }

  /**
   * Check if edge is in the graph.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @return true if the edge is in the graph, false if it is not in the graph
   * @throws NullPointerException if either source or target or both are null
   */
  public boolean containsEdge(T source, T target) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot contain edge adjacent to null data");
    Vertex sourceVertex = vertices.get(source);
    Vertex targetVertex = vertices.get(target);
    if (sourceVertex == null)
      return false;
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex)
        return true;
    return false;
  }

  /**
   * Return the weight of an edge.
   * 
   * @param source the data item contained in the source vertex for the edge
   * @param target the data item contained in the target vertex for the edge
   * @return the weight of the edge (0 or positive integer)
   * @throws IllegalArgumentException if either sourceVertex or targetVertex or both are not in the
   *                                  graph
   * @throws NullPointerException     if either sourceVertex or targetVertex or both are null
   * @throws NoSuchElementException   if edge is not in the graph
   */
  public int getWeight(T source, T target) {
    if (source == null || target == null)
      throw new NullPointerException("Cannot contain weighted edge adjacent to null data");
    Vertex sourceVertex = vertices.get(source);
    Vertex targetVertex = vertices.get(target);
    if (sourceVertex == null || targetVertex == null)
      throw new IllegalArgumentException(
          "Cannot retrieve weight of edge between vertices that do not exist");
    for (Edge e : sourceVertex.edgesLeaving)
      if (e.target == targetVertex)
        return e.weight;
    throw new NoSuchElementException("No directed edge found between these vertices");
  }

  /**
   * Return the number of edges in the graph.
   * 
   * @return the number of edges in the graph
   */
  public int getEdgeCount() {
    int edgeCount = 0;
    for (Vertex v : vertices.values())
      edgeCount += v.edgesLeaving.size();
    return edgeCount;
  }

  /**
   * Return the number of vertices in the graph
   * 
   * @return the number of vertices in the graph
   */
  public int getVertexCount() {
    return vertices.size();
  }

  /**
   * Check if the graph is empty (does not contain any vertices or edges).
   * 
   * @return true if the graph does not contain any vertices or edges, false otherwise
   */
  public boolean isEmpty() {
    return vertices.size() == 0;
  }

  /**
   * Path objects store a discovered path of vertices and the overal distance of cost of the
   * weighted directed edges along this path. Path objects can be copied and extended to include new
   * edges and verticies using the extend constructor. In comparison to a predecessor table which is
   * sometimes used to implement Dijkstra's algorithm, this eliminates the need for tracing paths
   * backwards from the destination vertex to the starting vertex at the end of the algorithm.
   */
  protected class Path implements Comparable<Path> {
    public Vertex start; // first vertex within path
    public int distance; // sumed weight of all edges in path
    public List<T> dataSequence; // ordered sequence of data from vertices in path
    public Vertex end; // last vertex within path

    /**
     * Creates a new path containing a single vertex. Since this vertex is both the start and end of
     * the path, it's initial distance is zero.
     * 
     * @param start is the first vertex on this path
     */
    public Path(Vertex start) {
      this.start = start;
      this.distance = 0;
      this.dataSequence = new LinkedList<>();
      this.dataSequence.add(start.data);
      this.end = start;
    }

    /**
     * This extension constructor makes a copy of the path passed into it as an argument without
     * affecting the original path object (copyPath). The path is then extended by the Edge object
     * extendBy.
     * 
     * @param copyPath is the path that is being copied
     * @param extendBy is the edge the copied path is extended by
     */
    public Path(Path copyPath, Edge extendBy) {
      Path copiedPath = copyPath; // Creates a copiedPath object
      this.start = copiedPath.start;
      this.end = extendBy.target; // Makes the end of copyPath the end of the new Edge
      this.distance = copyPath.distance + extendBy.weight; // Adds the weight of extendBy to the
                                                            // copied paths distance
      this.dataSequence = new LinkedList<>();
      for (int x = 0; x < copiedPath.dataSequence.size(); x++) {
        this.dataSequence.add(copiedPath.dataSequence.get(x));
      }
      this.dataSequence.add(extendBy.target.data);
    }

    /**
     * Allows the natural ordering of paths to be increasing with path distance. When path distance
     * is equal, the string comparison of end vertex data is used to break ties.
     * 
     * @param other is the other path that is being compared to this one
     * @return -1 when this path has a smaller distance than the other, +1 when this path has a
     *         larger distance that the other, and the comparison of end vertex data in string form
     *         when these distances are tied
     */
    public int compareTo(Path other) {
      int cmp = this.distance - other.distance;
      if (cmp != 0)
        return cmp; // use path distance as the natural ordering
      // when path distances are equal, break ties by comparing the string
      // representation of data in the end vertex of each path
      return this.end.data.toString().compareTo(other.end.data.toString());
    }
  }

  /**
   * Uses Dijkstra's shortest path algorithm to find and return the shortest path between two
   * vertices in this graph: start and end. This path contains an ordered list of the data within
   * each node on this path, and also the distance or cost of all edges that are a part of this
   * path.
   * 
   * @param start data item within first node in path
   * @param end   data item within last node in path
   * @return the shortest path from start to end, as computed by Dijkstra's algorithm
   * @throws NoSuchElementException when no path from start to end can be found, including when no
   *                                vertex containing start or end can be found
   */
  protected Path dijkstrasShortestPath(T start, T end) {
    if (!this.containsVertex(start)) {
      throw new NoSuchElementException();
    } else if (!this.containsVertex(end)) {
      throw new NoSuchElementException();
    }

    List<Vertex> unvisited = new ArrayList<Vertex>(); //List of unvisited nodes
    List<Path> shortestPaths = new ArrayList<Path>(); // List of the shortest path to each node
    List<Vertex> vertexTracker = new ArrayList<Vertex>(); //Stores vertexes
    Set<T> keys = vertices.keySet(); //Used to get all the vertices from the hashTable
    
    for(T key: keys){ //Builds the shortestPaths array, univisited array, and vertexTracker array
      shortestPaths.add(null);
      unvisited.add(vertices.get(key));
      vertexTracker.add(vertices.get(key));
    }
    
    int[] weights = new int[vertexTracker.size()]; //Creates an array of weight values
    for(int z = 0; z < vertexTracker.size(); z++) {
      weights[z] = Integer.MAX_VALUE;
    }
    
    PriorityQueue<Path> frontier = new <Path>PriorityQueue(); // Creates priority queue for paths
    Path potential = new Path(vertices.get(start));  //First path
    frontier.add(potential); // Adds first path to queue
    
    while (!frontier.isEmpty()) { //Keeps running while there are no more vertexes to check
      Path temp = frontier.remove(); 
      unvisited.remove(temp.end); //removes the first node from frontier and removes it from the unvisited list
      for(int x = 0; x<temp.end.edgesLeaving.size(); x++) //Runs this loop for every edge leaving the node to create all possible paths
      {
        Path potentialPath = new Path(temp, temp.end.edgesLeaving.get(x)); //Creates a potential path for every edgeLeaving
        //Checks if node has been unvisited if so adds to frontier
        if(unvisited.contains(potentialPath.end)) {
          unvisited.remove(potentialPath.end);
          frontier.add(potentialPath); 
        }
        //if the node is added to frontier check if the weight of that node is less than the node already at that position
          if(weights[vertexTracker.indexOf(potentialPath.end)] > potentialPath.distance){
            shortestPaths.set(vertexTracker.indexOf(potentialPath.end), potentialPath); //Set new shortest path
            weights[vertexTracker.indexOf(potentialPath.end)] = potentialPath.distance; //Set new weight
          }
      }
    }
    return shortestPaths.get(vertexTracker.indexOf(vertices.get(end))); //Return the shortest path of that specific node
  }

  /**
   * Returns the shortest path between start and end. Uses Dijkstra's shortest path algorithm to
   * find the shortest path.
   * 
   * @param start the data item in the starting vertex for the path
   * @param end   the data item in the destination vertex for the path
   * @return list of data item in vertices in order on the shortest path between vertex with data
   *         item start and vertex with data item end, including both start and end
   * @throws NoSuchElementException when no path from start to end can be found including when no
   *                                vertex containing start or end can be found
   */
  public List<T> shortestPath(T start, T end) {
    return dijkstrasShortestPath(start, end).dataSequence;
  }

  /**
   * Returns the cost of the path (sum over edge weights) between start and end. Uses Dijkstra's
   * shortest path algorithm to find the shortest path.
   * 
   * @param start the data item in the starting vertex for the path
   * @param end   the data item in the end vertex for the path
   * @return the cost of the shortest path between vertex with data item start and vertex with data
   *         item end, including all edges between start and end
   * @throws NoSuchElementException when no path from start to end can be found including when no
   *                                vertex containing start or end can be found
   */
  public int getPathCost(T start, T end) {
    return dijkstrasShortestPath(start, end).distance;
  }

}
