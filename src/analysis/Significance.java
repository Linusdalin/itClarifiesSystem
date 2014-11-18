package analysis;

/**
 *
 *              Significance
 *
 *              Significance is used with classifications. A low significance means that the classification is
 *              uncertain and not displayed to the user. It can however still be used when determining if
 *              a clause is completely missing.
 *
 *
 */
public class Significance {


    // Required significance for a match in the search
    public static final long MATCH_SIGNIFICANCE = 60;

    // Required significance to display to the user
    public static final int DISPLAY_SIGNIFICANCE = 60;
}
