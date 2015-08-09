package analysis;

import dataRepresentation.DataObjectInterface;
import log.PukkaLogger;
import risk.ContractRisk;
import risk.ContractRiskTable;

/**********************************************************************''
 *
 *          A risk classification is an external word comment representing a risk
 *
 *          The format is:
 *
 *          "#Risk <severity> <description text>
 *
 */

public class ImportedRiskClassification {


    public boolean isRiskClassification = false;  // Before parsing it correctly, it is false
    private static final String RISK_TAG = "#Risk";
    private ContractRisk risk = ContractRisk.getUnknown();
    private String description;

    /**************************************************************************
     *
     *          Parse and create a risk classification from a comment
     *
     *
     * @param comment       - the text comment
     */

    public ImportedRiskClassification(String comment){

        String[] tokens = comment.split(" ");

        if(tokens.length < 2){
            PukkaLogger.log(PukkaLogger.Level.WARNING, "Expecting risk tag and severity parsing risk. Got" + comment);
            return;

        }

        if(!tokens[0].equals(RISK_TAG)){

            PukkaLogger.log(PukkaLogger.Level.WARNING, "Expecting risk tag. Got" + comment);
            return;

        }

        getRiskLevel(tokens[1]);

        this.description = comment.substring(comment.indexOf(tokens[1]) + tokens[1].length() + 1);
        isRiskClassification = true;

    }

    private void getRiskLevel(String token) {

        for (DataObjectInterface value : ContractRiskTable.Values) {

            if(((ContractRisk)value).getName().equals(token)){
                this.risk = (ContractRisk)value;
                return;
            }

        }

        PukkaLogger.log(PukkaLogger.Level.WARNING, "Unknown risk severity " + token + " Setting severity to unknown");

    }

    public String getDescription() {
        return description;
    }

    public ContractRisk getRisk() {
        return risk;
    }


    /******************************************************************''
     *
     *          static test of a string
     *
     * @param comment       - the comment
     * @return              - true if it is a risk
     */

    public static boolean isRisk(String comment) {

        return comment.trim().startsWith(RISK_TAG);
    }

    public String toString(){

        return "#Risk " + this.risk.getName() + "(" +  this.description +")";
    }

}
