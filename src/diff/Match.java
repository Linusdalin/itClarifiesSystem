package diff;

public class Match {

    public int active;
    public int referenced;
    public int distance;

    public static final int ORPHAN = -1;
    public static final int INFINITE = -1;

    public Match(int active, int referenced){

        this.active = active;
        this.referenced = referenced;
        this.distance = INFINITE;
    }

    public Match setDistance(int distance){

        this.distance = distance;
        return this;
    }

    public String toString(){

        return "<" + active + ", " + referenced +"> (dist: " + distance + ")";
    }
}
