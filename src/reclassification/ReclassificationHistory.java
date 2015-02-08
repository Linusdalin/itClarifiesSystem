package reclassification;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-03
 * Time: 07:43
 * To change this template use File | Settings | File Templates.
 */
public class ReclassificationHistory {


    public static final MTDocument[] documents = {

            new MT_Test(),
            new MT_SwedishDemo(),
            new MT_EHM(),
    };

    public static MTDocument getDocumentByName(String name) {

        for (MTDocument document : documents) {

            if(document.getName().equals(name))
                return document;
        }

        return null;

    }
}
