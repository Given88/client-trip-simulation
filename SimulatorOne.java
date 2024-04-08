/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.*;

// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public GraphException( String name )
    {
        super( name );
    }
}

// Represents an edge in the graph.
class Edge
{
    public Vertex     dest;   // Second vertex in Edge
    public double     cost;   // Edge cost

    public Edge( Vertex d, double c )
    {
        dest = d;
        cost = c;
    }
}

// Represents an entry in the priority queue for Dijkstra's algorithm.
class Path implements Comparable<Path>
{
    public Vertex     dest;   // w
    public double     cost;   // d(w)

    public Path( Vertex d, double c )
    {
        dest = d;
        cost = c;
    }

    public int compareTo( Path rhs )
    {
        double otherCost = rhs.cost;

        return cost < otherCost ? -1 : cost > otherCost ? 1 : 0;
    }
}

// Represents a vertex in the graph.
class Vertex
{
    public String     name;   // Vertex name
    public List<Edge> adj;    // Adjacent vertices
    public double     dist;   // Cost
    public Vertex     prev;   // Previous vertex on shortest path
    public int        scratch;// Extra variable used in algorithm
    public int num_paths;

    public Vertex( String nm )
    { name = nm; adj = new LinkedList<Edge>( ); num_paths = 0; reset( ); }

    public void reset( )
    //  { dist = Graph.INFINITY; prev = null; pos = null; scratch = 0; }
    { dist = SimulatorOne.INFINITY; prev = null; scratch = 0; }

    // public PairingHeap.Position<Path> pos;  // Used for dijkstra2 (Chapter 23)
}

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted
// void dijkstra( String s )    --> Single-source weighted
// void negative( String s )    --> Single-source negative weighted
// void acyclic( String s )     --> Single-source acyclic
// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.

public class SimulatorOne
{
    public static final double INFINITY = Double.MAX_VALUE;
    private static Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );

    /**
     * Add a new edge to the graph.
     */
    public void addEdge( String sourceName, String destName, double cost )
    {
        Vertex v = getVertex( sourceName );
        Vertex w = getVertex( destName );
        v.adj.add( new Edge( w, cost ) );
    }

    /**
     * Driver routine to handle unreachables and print total cost.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
     */

    public void printPath( String destName )
    {
        Vertex w = vertexMap.get( destName );
        if( w == null )
            throw new NoSuchElementException( "Destination vertex not found" );
        else if( w.dist == INFINITY ) {
             System.out.println( destName + " is unreachable" );
        }
        else
        {
            System.out.print( "(Cost is: " + w.dist + ") " );//changed
            printPath( w );
            System.out.println( );//changed
            //found_cost = w.dist;//added

        }
    }

    /**
     * If vertexName is not present, add it to vertexMap.
     * In either case, return the Vertex.
     */
    private static Vertex getVertex( String vertexName )
    {
        Vertex v = vertexMap.get( vertexName );
        if( v == null )
        {
            v = new Vertex( vertexName );
            vertexMap.put( vertexName, v );
        }
        return v;
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist.
     */


    private void printPath( Vertex dest)
    {

        if( dest.prev != null )
        {
            printPath( dest.prev);

        }
        System.out.print(dest.name + " ");
    }


    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
     */
    private void clearAll( )
    {
        for( Vertex v : vertexMap.values( ) )
            v.reset( );
    }

    /**
     * Single-source unweighted shortest-path algorithm.
     */
    public void unweighted( String startName )
    {
        clearAll( );

        Vertex start = vertexMap.get( startName );
        if( start == null )
            throw new NoSuchElementException( "Start vertex not found" );

        Queue<Vertex> q = new LinkedList<Vertex>( );
        q.add( start ); start.dist = 0;

        while( !q.isEmpty( ) )
        {
            Vertex v = q.remove( );

            for( Edge e : v.adj )
            {
                Vertex w = e.dest;
                if( w.dist == INFINITY )
                {
                    w.dist = v.dist + 1;
                    w.prev = v;
                    q.add( w );
                }
            }
        }
    }

    /**
     * Single-source weighted shortest-path algorithm. (Dijkstra)
     * using priority queues based on the binary heap
     */
    int count = 0;
    public void dijkstra( String startName )
    {
        PriorityQueue<Path> pq = new PriorityQueue<Path>( );

        Vertex start = vertexMap.get( startName );
        if( start == null )
            throw new NoSuchElementException( "Start vertex not found" );

        clearAll( );
        pq.add( new Path( start, 0 ) ); start.dist = 0;

        int nodesSeen = 0;
        while( !pq.isEmpty( ) && nodesSeen < vertexMap.size( ) )
        {
            Path vrec = pq.remove( );
            Vertex v = vrec.dest;
            if( v.scratch != 0 )  // already processed v
                continue;

            v.scratch = 1;
            nodesSeen++;

            for( Edge e : v.adj )//iterates through all the adj nodes
            {
                Vertex w = e.dest;//second vertex in the list
                double cvw = e.cost;

                if( cvw < 0 )
                    throw new GraphException( "Graph has negative edges" );

                if( w.dist > v.dist + cvw )
                {
                  // w.num_paths = v.num_paths;
                  //  v.num_paths = 1;
                    count = 1;
                    w.dist = v.dist +cvw;
                    w.prev = v;
                    pq.add( new Path( w, w.dist ) );
                }else if(w.dist == v.dist + cvw){
                    count++;
                    w.num_paths = count;
                }
                else{
                    w.num_paths = 0;
                }
            }
        }
    }

    /**
     * Single-source negative-weighted shortest-path algorithm.
     * Bellman-Ford Algorithm
     */
    public void negative( String startName )
    {
        clearAll( );

        Vertex start = vertexMap.get( startName );
        if( start == null )
            throw new NoSuchElementException( "Start vertex not found" );

        Queue<Vertex> q = new LinkedList<Vertex>( );
        q.add( start ); start.dist = 0; start.scratch++;

        while( !q.isEmpty( ) )
        {
            Vertex v = q.remove( );
            if( v.scratch++ > 2 * vertexMap.size( ) )
                throw new GraphException( "Negative cycle detected" );

            for( Edge e : v.adj )
            {
                Vertex w = e.dest;
                double cvw = e.cost;

                if( w.dist > v.dist + cvw )
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                    // Enqueue only if not already on the queue
                    if( w.scratch++ % 2 == 0 )
                        q.add( w );
                    else
                        w.scratch--;  // undo the enqueue increment
                }
            }
        }
    }

    /**
     * Single-source negative-weighted acyclic-graph shortest-path algorithm.
     */
    public void acyclic( String startName )
    {
        Vertex start = vertexMap.get( startName );
        if( start == null )
            throw new NoSuchElementException( "Start vertex not found" );

        clearAll( );
        Queue<Vertex> q = new LinkedList<Vertex>( );
        start.dist = 0;

        // Compute the indegrees
        Collection<Vertex> vertexSet = vertexMap.values( );
        for( Vertex v : vertexSet )
            for( Edge e : v.adj )
                e.dest.scratch++;

        // Enqueue vertices of indegree zero
        for( Vertex v : vertexSet )
            if( v.scratch == 0 )
                q.add( v );

        int iterations;
        for( iterations = 0; !q.isEmpty( ); iterations++ )
        {
            Vertex v = q.remove( );

            for( Edge e : v.adj )
            {
                Vertex w = e.dest;
                double cvw = e.cost;

                if( --w.scratch == 0 )
                    q.add( w );

                if( v.dist == INFINITY )
                    continue;

                if( w.dist > v.dist + cvw )
                {
                    w.dist = v.dist + cvw;
                    w.prev = v;
                }
            }
        }

        if( iterations != vertexMap.size( ) )
            throw new GraphException( "Graph has a cycle!" );
    }

    /**
     * Process a request; return false if end of file.
     */
    public static boolean processRequest( Scanner in, SimulatorOne g )
    {
        try
        {
            System.out.print( "Enter start node:" );
            String startName = in.nextLine( );

            System.out.print( "Enter destination node:" );
            String destName = in.nextLine( );

            System.out.print( "Enter algorithm (u, d, n, a ): " );
            String alg = in.nextLine( );

            if( alg.equals( "u" ) )
                g.unweighted( startName );
            else if( alg.equals( "d" ) )
            {
                g.dijkstra( startName );
                g.printPath( destName );
            }
            else if( alg.equals( "n" ) )
                g.negative( startName );
            else if( alg.equals( "a" ) )
                g.acyclic( startName );

            g.printPath( destName );
        }
        catch( NoSuchElementException e )
        { return false; }
        catch( GraphException e )
        { System.err.println( e ); }
        return true;
    }

    /**
     * A main routine that:
     * 1. Reads a file containing edges (supplied as a command-line parameter);
     * 2. Forms the graph;
     * 3. Repeatedly prompts for two vertices and
     *    runs the shortest path algorithm.
     * The data file is a sequence of lines of the format
     *    source destination cost
     */
    public static void main( String [ ] args )
    {
        SimulatorOne g = new SimulatorOne( );
        try {
            //FileReader fin = new FileReader(args[0]);
            Scanner scanner = new Scanner(System.in);


            int num_nodes = Integer.parseInt(scanner.nextLine());//gets the number of nodes
            for (int i = 0; i < num_nodes; i++) {
                String[] values = (scanner.nextLine()).split(" ");
                //get the length of the nodes in an array
                int size = values.length;
                if (size == 1) {
                    g.addEdge(values[0], null, 0);
                }                    //has no connecting nodes
                else {

                    //get all the other connecting nodes
                    int counter = 0;
                    int bound = size - 2;
                    if (bound == 1) {
                        String source = values[0];
                        String dest = values[1];
                        int cost = Integer.parseInt(values[2]);
                        g.addEdge(source, dest, cost);
                        bound = bound - 2;
                    } else if (bound != 1) {
                        while ((bound) >= 1) {
                            String source = values[0];
                            String dest = values[2 * counter + 1];
                            int cost = Integer.parseInt(values[2 * counter + 2]);
                            g.addEdge(source, dest, cost);
                            bound = bound - 2;
                            counter++;
                        }
                    }
                }
            }
            //get the number of shops
            int num_shop = Integer.parseInt(scanner.nextLine());
            //shop node number

           // String[] shop_nodes = scanner.nextLine().replaceAll("\\s+", "").split("");
            String[] shop_nodes = scanner.nextLine().replaceAll(" ", "  ").split("  ");
            //get the number of clients
            int num_clients = Integer.parseInt(scanner.nextLine());
            //get the client node number
            //original String[] client_nodes = scanner.nextLine().replaceAll("\\s+", "").split(" ");
            String[] client_nodes = scanner.nextLine().replaceAll(" ", "  ").split("  ");
/**
 * New Version finding taxis
 */
            for (String client : client_nodes) {
                boolean are_there_taxis = false;
                System.out.println("client " + client);
                double shortestDistance = Double.MAX_VALUE;
                String shortestTaxi = null;
                ArrayList<Integer> taxis = new ArrayList<Integer>();
                boolean same_taxi = false;
                for (String shop : shop_nodes) {
                    g.dijkstra(shop);
                    Vertex dest = vertexMap.get(client);
                    if ((dest.dist < shortestDistance) ) {
                        shortestDistance = dest.dist;
                        shortestTaxi = shop;
                        are_there_taxis = true;



                    } else if ((dest.dist == shortestDistance) && shortestTaxi!=null) {
                        //there are two or more closest shops
                        taxis.add(Integer.parseInt(shortestTaxi));
                        taxis.add(Integer.parseInt(shop));
                        same_taxi = true;
                        are_there_taxis = true;
                    }
                }
                /**
                 * Correct Finding taxis
                 */
                /**
                 * Finding shops
                 */

                double Shop_Distance = Double.MAX_VALUE; //maximum distance from the client to a node
                String shortest_shop = null; //closet shop from the client
                boolean are_there_shops = false;
                boolean more_shops = false;
                boolean other = false;
                //String all_more_shops = null;//list that will store all the equal shops
                ArrayList<Integer> shops = new ArrayList<Integer>();
                boolean other_shop= false;
                for(String shop: shop_nodes) {
                    g.dijkstra(client);
                    Vertex dest = vertexMap.get(shop);
                    if (dest.num_paths !=0) {
                        more_shops = true; // There are multiple equal shortest paths to this shop
                        if (dest.dist < Shop_Distance) {
                            Shop_Distance = dest.dist;
                            shortest_shop = shop;
                            are_there_shops = true;
                            shops.add(Integer.parseInt(shop));
                        } else if (dest.dist == Shop_Distance && shortest_shop != null) {
                            // If there are multiple equal shortest paths, but they have the same distance as the shortest_shop
                            shops.add(Integer.parseInt(shop));
                            other_shop = true;
                            are_there_shops = true;
                        }
                    } else {
                        if (dest.dist < Shop_Distance) {
                            Shop_Distance = dest.dist;
                            shortest_shop = shop;
                            are_there_shops = true;
                            shops.add(Integer.parseInt(shop));
                        } else if (dest.dist == Shop_Distance && shortest_shop != null) {
                            shops.add(Integer.parseInt(shop));
                            other_shop = true;
                            are_there_shops = true;
                        }
                    }
                }
                /**
                 * End of finding shops
                 */
                /**
                 * Correct
                 */
                if(are_there_shops==false || are_there_taxis ==false){
                    System.out.println("cannot be helped");
                }
                else {

                    if (are_there_taxis == true) {
                        g.dijkstra(shortestTaxi);//start at the node
                        if (same_taxi == true) {
                            Collections.sort(taxis);
                            for (Integer taxi : taxis) {
                                System.out.println("taxi " + taxi);
                                g.dijkstra(String.valueOf(taxi));
                                Vertex dest = vertexMap.get(client);
                                g.printPath(dest);
                                System.out.println();
                            }
                        } else {
                            System.out.println("taxi " + shortestTaxi);
                            Vertex dest = vertexMap.get(client); //to the client
                            g.printPath(dest);
                            System.out.println();
                        }

                    }

                    //done
                    if (are_there_shops == true) {
                        g.dijkstra(client);
                        if (more_shops == true && other_shop == false) {
                            Vertex des = vertexMap.get(String.valueOf(shortest_shop));
                            System.out.println("shop " + des.name);
                            System.out.println("multiple solutions cost " + String.valueOf(Math.round(des.dist)));

                        }
                        else if(more_shops == false&& other_shop == true){
                            Collections.sort(shops);
                            for(Integer shop: shops){
                                System.out.println("shop " + shop);
                                Vertex dest = vertexMap.get(String.valueOf(shop));
                                g.printPath(dest);
                                System.out.println();
                            }
                        }
                        else {
                            System.out.println("shop " + shortest_shop);
                            Vertex dest = vertexMap.get(shortest_shop);
                            g.printPath(dest);
                            System.out.println();
                        }
                    //    System.out.println(more_shops);


                    }
                }



/**
 * End of new version
 */

            }
        }
        //exception
        catch( Exception e )
        { System.err.println( e ); }


    }
}
