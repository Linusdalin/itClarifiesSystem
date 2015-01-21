package classification;

import featureTypes.FeatureTypeTree;

/*************************************************************************************
 *
 *              This is the default structure for presentation
 *
 *
 */


public class ClassificationOverviewTree {

    private static final ClassificationOverviewNode[] LEAF = new ClassificationOverviewNode[]{};


    public static final ClassificationOverviewNode root =

            new ClassificationOverviewNode(FeatureTypeTree.Structure,

                    new ClassificationOverviewNode[]{

                            new ClassificationOverviewNode(FeatureTypeTree.Date, LEAF),
                            new ClassificationOverviewNode(FeatureTypeTree.Percentage, LEAF),
                    }

            );
}
