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

        String[] tagsWithPatterns = keywordString.split("[#{}]");

        PukkaLogger.log(PukkaLogger.Level.DEBUG, "In get Pattern: tag= \""+ tag +"\" tagsWithPatterns:" + Arrays.asList(tagsWithPatterns).toString());

        int tagPos = find(tag, tagsWithPatterns);
        if(tagPos >= 0){

            return tagsWithPatterns[tagPos + 1];

        }
        else{

            // If we cant find the tag, we add it as a potential pattern anyway

            return tag.substring(1).toLowerCase();
        }


    }

    private int find(String word, String[] tagsWithPatterns) {

        for(int i = 0; i < tagsWithPatterns.length; i++){

            if(tagsWithPatterns[i].equalsIgnoreCase(word.substring(1)))
                return i;

        }
        return -1;
    }


}
