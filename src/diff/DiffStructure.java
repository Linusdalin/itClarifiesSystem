package diff;

import java.util.ArrayList;
import java.util.List;

/***************************************************************************************
 *
 *          A diffStructure is the outcome of a diff operation
 *
 *          It contains the comparison between two documents listing the order of the fragments
 *
 *          The document references are labeled "active" and "referenced"
 *
 */

public class DiffStructure {

    // List of matches between the fragments
    List<Match> matches = new ArrayList<Match>();

    public void add(Match match) {

        matches.add(match);
    }


    public List<Match> getMatches(){

        return matches;
    }

    /***********************************************
     *
     *      Display the match
     *
     * @return
     */

    public String toString(){

        String output = "Match:\n";
        for(Match match : matches){
            output += match.toString() + "\n";
        }
        return output;
    }

    // This is only for verification in the unit tests

    public int getNoOrphansActive() {


        int orphans = 0;
        for(Match match : matches){

           if(match.referenced == Match.ORPHAN){
               orphans++;
           }
        }

        return orphans;
    }

    // This is only for verification in the unit tests

    public int getNoOrphansReferenced() {


        int orphans = 0;
        for(Match match : matches){

           if(match.active == Match.ORPHAN){
               orphans++;
           }
        }

        return orphans;
    }

    /***********************************************************************
     *
     *      For lookup in the diff process, retrieve the corresponding fragment
     *      in the active list, given a fragment number in the reference doc
     *
     * @param fragmentNo - index
     * @return
     */

    public int getFragmentInActive(int fragmentNo) {

        for(Match match : matches){

           if(match.referenced == fragmentNo){
               return match.active;
           }
        }
        return Match.ORPHAN;        // TODO: This is really an error. Throw exception
    }
}


