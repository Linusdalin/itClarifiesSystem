package overviewExport;

import classification.FragmentClassification;

import java.util.ArrayList;
import java.util.List;

/****************************************************************************************
 *
 *              List of tags for an extraction. This includes the tag
 *              itself together with all the children in the module.
 *
 *              This is used to match th content for an extraction tag to all
 *              fragments matching the tag and the child tags.
 *
 */
public class ExtractionTagList {

    private List<String> children = new ArrayList<String>();
    private final String mainTag;

    public int activeHeadlineLevel = -1;
    public int activeHeadlineStart = 0;


    /*********************************************************
     *
     *          Create the object for a specific tag.
     *
     * @param mainTag
     */


    public ExtractionTagList(String mainTag){

        this.mainTag = mainTag;
    }

    public void setChildren( List<String> childTags){

        children = childTags;
    }


    public String getMainTag() {
        return mainTag;
    }

    /*************************************************************
     *
     *          Match
     *
     *          NOTE: This is filtering all blocking tags. We may want to display them some other way in the future
     *
     * @param classification    - classification found for a fragment
     * @return                  - true if the classTag in the extraction is applicable
     */


    public boolean isApplicableFor(FragmentClassification classification) {

        if(classification.getblockingState() == FragmentClassification.BLOCKED ||
           classification.getblockingState() == FragmentClassification.BLOCKING ||
           classification.getblockingState() == FragmentClassification.OVERRIDDEN)
            return false;

        if(mainTag.equals(classification.getClassTag()))
            return true;

        for (String child : children) {

            if(child.equals(classification.getClassTag()))
                return true;
        }

        return false;

    }
}
