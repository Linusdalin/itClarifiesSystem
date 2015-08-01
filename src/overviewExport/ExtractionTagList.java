package overviewExport;

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
     * @param classTag    - classification tag found for a fragment
     * @return            - true if the classTag in the extraction is applicable
     */


    public boolean isApplicableFor(String classTag) {

        if(mainTag.equals(classTag))
            return true;

        for (String child : children) {

            if(child.equals(classTag))
                return true;
        }

        return false;

    }
}
