package search;

import log.PukkaLogger;

import java.util.Arrays;

/**
 *
 *          The keyword field handler will manage the encoding of
 *          tags in the keyword field
 *
 */


public class KeywordFieldHandler {

    private String keywordString;

    public KeywordFieldHandler(String encodedField){

        this.keywordString = encodedField;
    }

    public KeywordFieldHandler(){

        this.keywordString = "";
    }

    public void addTag(String tag, String pattern){

        keywordString += tag + "{" + pattern + "}";

    }

    public String encode() {
        return keywordString;
    }


    public String getPatternForTag(String tag){

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "In get Pattern: tag= \""+ tag +"\" looking at:" + keywordString);

        int textPos = keywordString.indexOf(tag.toLowerCase());

        if(textPos < 0)
            return tag.substring(1).toLowerCase();

        String rest = keywordString.substring( textPos );

        int patternStart = rest.indexOf("{") + 1;
        int patternEnd   = rest.indexOf("}");

        if(patternStart < 0 || patternEnd < 0){

            // The pattern indicators are missing. This is an implementation error
            PukkaLogger.log(PukkaLogger.Level.FATAL, " Could not find pattern for tag " + tag + " in " + keywordString);
        }

        String pattern = rest.substring(patternStart, patternEnd);
        return pattern;

    }


}
