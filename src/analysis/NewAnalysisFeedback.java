package analysis;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-02-26
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class NewAnalysisFeedback {


    public final int classifications;
    public final int references;
    public final int annotations;
    public final int risks;

    public final boolean isModified;

    public NewAnalysisFeedback(int classifications, int references, int annotations, int risks) {

        this.classifications = classifications;
        this.references = references;
        this.annotations = annotations;
        this.risks = risks;

        this.isModified = (classifications + references + annotations + risks) > 0;
    }

}
