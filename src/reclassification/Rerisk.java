package reclassification;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import project.*;
import dataRepresentation.*;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;

/********************************************************
 *
 *    Rerisk - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class Rerisk extends DataObject implements DataObjectInterface{



    private static final DataTableInterface TABLE = (DataTableInterface) new ReriskTable();

    public Rerisk(){

        super();

        if(table == null)
            table = TABLE;
    }

    public Rerisk(String risklevel, String date, String project, String document, long fragmentno, String fragment, String pattern, long patternpos, String user, boolean closed){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new StringData(risklevel);
           data[1] = new DateData(date);
           data[2] = new StringData(project);
           data[3] = new StringData(document);
           data[4] = new IntData(fragmentno);
           data[5] = new BlobData(fragment);
           data[6] = new TextData(pattern);
           data[7] = new IntData(patternpos);
           data[8] = new StringData(user);
           data[9] = new BoolData(closed);

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    /*********************************************************************''
     *
     *          Load from database
     *
     * @param condition - the SQL condition for selecting ONE UNIQUE object
     */

    public Rerisk(ConditionInterface condition){

        this();

        try{
            exists = load(condition);

        }catch(BackOfficeException e){

            System.out.println("Error loading object from database" + e.narration);
            e.printStackTrace();
        }

    }

    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        Rerisk o = new Rerisk();
        o.data = data;
        o.exists = true;
        return o;
    }

    public String getRiskLevel(){

        StringData data = (StringData) this.data[0];
        return data.getStringValue();
    }

    public void setRiskLevel(String risklevel){

        StringData data = (StringData) this.data[0];
        data.setStringValue(risklevel);
    }



    public DBTimeStamp getDate()throws BackOfficeException{

        DateData data = (DateData) this.data[1];
        return new DBTimeStamp(DBTimeStamp.ISO_DATE, data.value);
    }

    public void setDate(DBTimeStamp date){

        DateData data = (DateData) this.data[1];
        data.value = date.getISODate().toString();
    }



    public String getProject(){

        StringData data = (StringData) this.data[2];
        return data.getStringValue();
    }

    public void setProject(String project){

        StringData data = (StringData) this.data[2];
        data.setStringValue(project);
    }



    public String getDocument(){

        StringData data = (StringData) this.data[3];
        return data.getStringValue();
    }

    public void setDocument(String document){

        StringData data = (StringData) this.data[3];
        data.setStringValue(document);
    }



    public long getFragmentNo(){

        IntData data = (IntData) this.data[4];
        return data.value;
    }

    public void setFragmentNo(long fragmentno){

        IntData data = (IntData) this.data[4];
        data.value = fragmentno;
    }



    public String getFragment(){

        BlobData data = (BlobData) this.data[5];
        return data.getStringValue();
    }

    public void setFragment(String fragment){

        BlobData data = (BlobData) this.data[5];
        data.setStringValue(fragment);
    }



    public String getPattern(){

        TextData data = (TextData) this.data[6];
        return data.getStringValue();
    }

    public void setPattern(String pattern){

        TextData data = (TextData) this.data[6];
        data.setStringValue(pattern);
    }



    public long getPatternPos(){

        IntData data = (IntData) this.data[7];
        return data.value;
    }

    public void setPatternPos(long patternpos){

        IntData data = (IntData) this.data[7];
        data.value = patternpos;
    }



    public String getUser(){

        StringData data = (StringData) this.data[8];
        return data.getStringValue();
    }

    public void setUser(String user){

        StringData data = (StringData) this.data[8];
        data.setStringValue(user);
    }



    public boolean getClosed(){

        BoolData data = (BoolData) this.data[9];
        return data.value;
    }

    public void setClosed(boolean closed){

        BoolData data = (BoolData) this.data[9];
        data.value = closed;
    }




    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/


    public String getPostRequest() throws java.io.UnsupportedEncodingException {

        String fragmentText = getFragment();
        //fragmentText = fragmentText.replaceAll("&", "").replaceAll("/", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\}", "").replaceAll("\\{", "").replaceAll("\\]", "").replaceAll("\\[", "");

        return "&type=risk"+
                "&magicKey=" + SessionManagement.MagicKey +
                "&risk=" + getRiskLevel() +
                "&project=" + getProject() +
                "&document=" + getDocument() +
                "&ordinal=" + getFragmentNo() +
                "&fragment=" + java.net.URLEncoder.encode(fragmentText, "UTF-8") +
                "&pattern=" + getPattern() +
                "&pos=" + getPatternPos() +
                "&user=" + getUser();
    }

    /******************************************************************************
     *
     *          Generate risk will add a new risk in the risk table AND change the risk in the fragment
     *
     *
     * @param project
     * @param version
     * @param user
     * @param fragment
     * @return
     * @throws BackOfficeException
     */

    public analysis.ParseFeedbackItem generate(Project project, ContractVersionInstance version, PortalUser user, ContractFragment fragment) throws BackOfficeException {

        System.out.println("Body: " + getFragment());
        System.out.println("Located fragment: " + fragment.getOrdinal() + "(" + fragment.getName() + ")");

        ContractRisk risk = getContractRiskByName(getRiskLevel());

        if(!risk.exists())
            return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                    "No contractrisk called " + getRiskLevel() + " exists Aborting!", 0);


        RiskClassificationTable existingClassifications = new RiskClassificationTable(new LookupList()
                .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Fragment.name(), fragment.getKey()))
                .addFilter(new ReferenceFilter(RiskClassificationTable.Columns.Risk.name(), risk.getKey())));




        if(existingClassifications.getCount() > 0){

            System.out.println("Found existing classification(s) of type " + risk.getName() + " in fragment " + fragment.getName() + " Aborting!");
            return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.WARNING,
                    "Found existing classification(s) of type " + risk.getName() + " in fragment " + fragment.getName() + " Aborting!", 0);
        }


        //    public RiskClassification(DBKeyInterface fragment, DataObjectInterface risk, String comment, String keywords,
        //                              DBKeyInterface creator, DBKeyInterface version, DBKeyInterface project, String pattern, long patternpos, String time){



        RiskClassification classification = new RiskClassification(
                fragment.getKey(),
                (DataObjectInterface)risk,
                "regenerated",
                "",
                user.getKey(),
                version.getKey(),
                project.getKey(),
                getPattern(),
                getPatternPos(),
                getDate().getISODate(),
                FragmentClassification.GENERATED);

        classification.store();

        fragment.setRisk(risk);
        fragment.update();



        return new analysis.ParseFeedbackItem(analysis.ParseFeedbackItem.Severity.INFO, "Generated risk classification of type " + risk.getName() + " in fragment " + fragment.getName() + "!", 0);
    }

    private ContractRisk getContractRiskByName(String riskLevel) throws BackOfficeException{

        PukkaLogger.log(PukkaLogger.Level.INFO, " Trying to locate risk " + riskLevel);

        ContractRiskTable t = new ContractRiskTable();
        for (ContractRisk contractRisk : t.getAll()) {

            if(contractRisk.getName().equals(riskLevel))
                return contractRisk;
        }

        PukkaLogger.log(PukkaLogger.Level.INFO, " Not found risk " + riskLevel + " not good!");
        return new ContractRisk();

    }


}
