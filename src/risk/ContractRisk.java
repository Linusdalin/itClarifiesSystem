package risk;

import risk.*;
import contractManagement.*;
import classification.*;
import userManagement.*;
import versioning.*;
import actions.*;
import overviewExport.*;
import search.*;
import crossReference.*;
import reclassification.*;
import dataRepresentation.*;
import databaseLayer.DBKeyInterface;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import log.PukkaLogger;
import pukkaBO.exceptions.BackOfficeException;
import pukkaBO.condition.*;
import pukkaBO.database.*;

import pukkaBO.acs.*;

/********************************************************
 *
 *    ContractRisk - Data Object
 *    Automatically generated file by GenerateTable.java
 *
 *
 */

public class ContractRisk extends DataObject implements DataObjectInterface{

    private static ContractRisk Black = null;  
    private static ContractRisk Red = null;  
    private static ContractRisk Amber = null;  
    private static ContractRisk Unknown = null;  
    private static ContractRisk None = null;  
    private static ContractRisk NotSet = null;  
    private static ContractRisk Advantage = null;  


    private static final DataTableInterface TABLE = (DataTableInterface) new ContractRiskTable();

    public ContractRisk(){

        super();

        if(table == null)
            table = TABLE;
    }

    public ContractRisk(long id, String name, String severity, String description){

        this();
        try{
           ColumnStructureInterface[] columns = getColumnFromTable();


           data = new ColumnDataInterface[columns.length];

           data[0] = new IntData(id);
           data[1] = new StringData(name);
           data[2] = new StringData(severity);
           data[3] = new TextData(description);

           exists = true;
        }catch(BackOfficeException e){
            PukkaLogger.log(PukkaLogger.Level.FATAL, "Could not create object.");
            exists = false;
        }


    }
    public DataObjectInterface createNew(ColumnDataInterface[] data ) throws BackOfficeException {

        ContractRisk o = new ContractRisk();
        o.data = data;
        o.exists = true;
        return o;
    }

    public long getId(){

        IntData data = (IntData) this.data[0];
        return data.value;
    }

    public void setId(long id){

        IntData data = (IntData) this.data[0];
        data.value = id;
    }



    public String getName(){

        StringData data = (StringData) this.data[1];
        return data.getStringValue();
    }

    public void setName(String name){

        StringData data = (StringData) this.data[1];
        data.setStringValue(name);
    }



    public String getSeverity(){

        StringData data = (StringData) this.data[2];
        return data.getStringValue();
    }

    public void setSeverity(String severity){

        StringData data = (StringData) this.data[2];
        data.setStringValue(severity);
    }



    public String getDescription(){

        TextData data = (TextData) this.data[3];
        return data.getStringValue();
    }

    public void setDescription(String description){

        TextData data = (TextData) this.data[3];
        data.setStringValue(description);
    }



    public static ContractRisk getBlack( )  {

       return (ContractRisk)ContractRiskTable.Values.get(0);
    }

    public static ContractRisk getRed( )  {

       return (ContractRisk)ContractRiskTable.Values.get(1);
    }

    public static ContractRisk getAmber( )  {

       return (ContractRisk)ContractRiskTable.Values.get(2);
    }

    public static ContractRisk getUnknown( )  {

       return (ContractRisk)ContractRiskTable.Values.get(3);
    }

    public static ContractRisk getNone( )  {

       return (ContractRisk)ContractRiskTable.Values.get(4);
    }

    public static ContractRisk getNotSet( )  {

       return (ContractRisk)ContractRiskTable.Values.get(5);
    }

    public static ContractRisk getAdvantage( )  {

       return (ContractRisk)ContractRiskTable.Values.get(6);
    }


    public static void clearConstantCache(){

        //  Clear all cache when the application is uploaded.

    }

    /* Code below this point will not be replaced when regenerating the file*/

    /*__endAutoGenerated*/



}
