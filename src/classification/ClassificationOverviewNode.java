package classification;

import featureTypes.FeatureTypeInterface;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-01-21
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */

public class ClassificationOverviewNode {

    public FeatureTypeInterface type;
    public ClassificationOverviewNode[] children;

    ClassificationOverviewNode(FeatureTypeInterface type, ClassificationOverviewNode[] children){

        this.type = type;
        this.children = children;
    }


}
