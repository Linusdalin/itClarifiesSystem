package analysis;

import log.PukkaLogger;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2014-06-15
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class AutoNumberer {

    // Default start values. All levels start at 0 so when we get a new number we get 1.

    int current[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int currentIndentation = 1;

    public static final boolean CONTINUE = false;
    public static final boolean RESTART = true;


    /******************************************************
     *
     *      Set a starting main level
     *
     * @param i
     * @return
     */

    public AutoNumberer startAt(int i) {


        current[0] = i - 1;
        return this;
    }

    /****************************************************
     *
     *      Get a new number
     *
     * @param indentation - the desired indentation level
     * @return String with the chapter number
     *
     *          Note: This method has the sideeffect of updating the current chapter level
     *
     */


    public String getNewNumber(int indentation, boolean restart) {

        if(indentation == 0){
            PukkaLogger.log(PukkaLogger.Level.INFO, "indentation = 0, no headline number");
            return "";
        }

        if(restart){

            for(int i = 0; i < current.length; i++){

                    current[i] = 0;
            }


        }

        // First update the numbers.

        current[indentation-1]++;     // Increase one chapter

        // Implicitly update all chapters when we are increasing more than one level

        for(int i = 0; i < indentation-1; i++){

            if(current[i] == 0)
                current[i] = 1; // Implicitly update
        }




        for(int i = indentation; i < current.length; i++){

            current[i] = 0; // Reset all of them
        }

        // Secondly create a new value

        StringBuffer buffer = new StringBuffer();

        for(int i = 0; i < indentation-1; i++){
            buffer.append(current[i]);
            buffer.append(".");
        }

        buffer.append(current[indentation-1]);


        PukkaLogger.log(PukkaLogger.Level.INFO, "Got headline indentation number " + buffer.toString());
        return buffer.toString();

    }
}
