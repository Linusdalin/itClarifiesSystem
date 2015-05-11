package classification;

import featureTypes.FeatureTypeInterface;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-05-07
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */

public class ClassificationPattern {


    private final FeatureTypeInterface classification;
    private final String tag;

    ClassificationPattern(FeatureTypeInterface classification, String tag){


        this.classification = classification;
        this.tag = tag;
    }

    public FeatureTypeInterface getClassification() {
        return classification;
    }

    public String getTag() {
        return tag;
    }
}
