package diff;


import log.PukkaLogger;

/**
 *          A comparator will compare two versions of a document and
 *          produce a DiffStructure
 *
 *          This is done by two main responsibilities:
 *
 *           - Compare two fragments to see if they are the same or not.
 *           - Create a diff structure that maps which fragments in the two documents are the same.
 */
public class FragmentComparator {


    // Below this threshold, the two strings are considered the same. The value is
    // empirical and has been deducted by the tests in the DiffTest unit test class.

    public static final int DISTANCE_THRESHOLD = 35;

    public FragmentComparator(){

    }


    /*********************************************************************************
     *
     *      A first and very simple implementation of the diff algorithm.
     *      This will have to be improved with better "longest run" algorithms
     *
     *      The algorithm here should detect added and removed fragments
     *
     * @param activeFragments  - text representation
     * @param referenceFragments - text representation
     * @return - the structure between the fragments
     */

    public DiffStructure diff(String[] activeFragments, String[] referenceFragments){

        int referencePointer = 0;
        DiffStructure diffStructure = new DiffStructure();

        for(int activePointer = 0; activePointer < activeFragments.length; activePointer++){


            // Find the next match in the referenceFragment array
            int lookAhead = referencePointer;

            while(lookAhead < referenceFragments.length && !isSame(activeFragments[activePointer], referenceFragments[lookAhead])){

                PukkaLogger.log(PukkaLogger.Level.INFO, "Got MISS between fragments " + activePointer + " and " + lookAhead);
                lookAhead++;
            }

            if(lookAhead == referenceFragments.length){

                // We reached the end of the reference fragments. Active Fragment is an Orphan.
                diffStructure.add(new Match(activePointer, Match.ORPHAN));

            }
            else{

                // It was a hit. We will now continue from this position. But first all elements we looped over in the lookup
                // Shall be orphans

                for(int i = referencePointer; i < lookAhead; i++){

                    diffStructure.add(new Match(Match.ORPHAN, i));

                }


                PukkaLogger.log(PukkaLogger.Level.INFO, "Got hit between fragments " + activePointer + " and " + lookAhead);

                diffStructure.add(new Match(activePointer, lookAhead)
                        .setDistance(getDistance(activeFragments[activePointer], referenceFragments[lookAhead])));
                referencePointer = lookAhead+1;
            }


        }

        return diffStructure;
    }

    /*************************************************************************************
     *
     *          Evaluator for the distance between two fragments
     *
     * @param activeFragment    - one
     * @param referenceFragment - the other
     * @return - true if we are below the threshold
     */


    public boolean isSame(String activeFragment, String referenceFragment) {


        int distance = getDistance(activeFragment, referenceFragment);
        int distancePerAvgChar = (100 * distance) / ((referenceFragment.length() + activeFragment.length()) / 2);
        PukkaLogger.log(PukkaLogger.Level.INFO, "Got distance " + distance + " between " + activeFragment + " and " + referenceFragment);

        PukkaLogger.log(PukkaLogger.Level.INFO, " - Distance per avg char =  " + distancePerAvgChar);

        return distancePerAvgChar < DISTANCE_THRESHOLD;
    }


    /******************************************************************
     *
     *          Distance Calculation between two different strings.
     *          This uses Levenshteins algorithm for distance.
     *
     *          This is used to estimate if two fragments actually are the same.
     *
     *
     * @param active - one fragment
     * @param original - the other fragment
     * @return  distance value according to the algorithm
     *
     *
     */

    public int getDistance (String active, String original) {

    	int len0 = active.length()+1;
    	int len1 = original.length()+1;

    	// the array of distances
    	int[] cost = new int[len0];
    	int[] newcost = new int[len0];

    	// initial cost of skipping prefix in String active
    	for(int i=0;i<len0;i++) cost[i]=i;

    	// dynamicaly computing the array of distances

    	// transformation cost for each letter in original
    	for(int j=1;j<len1;j++) {

    		// initial cost of skipping prefix in String original
    		newcost[0]=j-1;

    		// transformation cost for each letter in active
    		for(int i=1;i<len0;i++) {

    			// matching current letters in both strings
    			int match = (active.charAt(i-1)==original.charAt(j-1))?0:1;

    			// computing cost for each transformation
    			int cost_replace = cost[i-1]+match;
    			int cost_insert  = cost[i]+1;
    			int cost_delete  = newcost[i-1]+1;

    			// keep minimum cost
    			newcost[i] = Math.min(Math.min(cost_insert, cost_delete),cost_replace );
    		}

    		// swap cost/newcost arrays
    		int[] swap=cost; cost=newcost; newcost=swap;
    	}

    	// the distance is the cost for transforming all letters in both strings
    	return cost[len0-1];
    }

}
