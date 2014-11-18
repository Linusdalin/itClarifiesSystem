package analysis;

import java.math.BigInteger;

/**
 *          Simple style is typically the styles we are working with in the analysis and frontend
 */

public class SimpleStyle {


    public enum Type {HEADLINE, LIST, TOC, PAGEHEADER, PAGEFOOTER, TEXT}

    public Type type;
    public int level;
    public String name;
    public boolean restartNumbering = false;


    /*****************************************************************'
     *
     *      SimpleSytyle is based on the style name and the level.
     *
     * @param name
     */

    public SimpleStyle(String name){

        this(name, false, false);
    }

    public SimpleStyle(String name, boolean hasNumber, boolean restart){

        level = 0;
        this.name = name;
        type = Type.TEXT;  // This is default

        if(isTOC(name))
            type = Type.TOC;

        if(isHeader(name))
            type = Type.PAGEHEADER;
        if(isFooter(name))
            type = Type.PAGEFOOTER;
        if(isHeadline(name))
            type = Type.HEADLINE;
        if(isList(name))
            type = Type.LIST;

        if(!hasNumber && !restart)
            level = 0;

        restartNumbering = restart;

    }

    private boolean isTOC(String name){

        if(name == null)
            return false;
        return name.startsWith("TOC") || name.startsWith("Innehll");
    }

    private boolean isHeader(String name){

        if(name == null)
            return false;
        return name.startsWith("Tablehead") || name.startsWith("Tabellsidhuvud");
    }


    private boolean isFooter(String name){

        if(name == null)
            return false;
        return name.startsWith("Tablefooter") || name.startsWith("Tabellsidfot");
    }

    private boolean isHeadline(String name){


        if(name == null)
            return false;

        if(! (name.startsWith("Title") || name.startsWith("Rubrik") || name.startsWith("Heading")))
            return false;

        try{

            level = new Integer(name.substring(name.length() - 1));

        }catch(Exception e){

            level = 0;
        }

        return true;
    }



    public static boolean isList(String style) {

        if(style == null)
            return false;

        return style.startsWith("List");
    }
}
