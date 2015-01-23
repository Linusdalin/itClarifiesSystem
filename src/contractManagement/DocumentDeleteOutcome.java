package contractManagement;

/**
 *          This is the count of the recursive delete
 *          It is used to pass the result back to the user or calling process
 *
 */

public class DocumentDeleteOutcome {


    public int documents = 0;
    public int versions = 0;
    public int clauses = 0;
    public int fragments = 0;
    public int annotations = 0;
    public int classifications = 0;
    public int riskFlags = 0;
    public int references = 0;
    public int keywords = 0;
    public int indices = 0;

    DocumentDeleteOutcome(int documents, int versions, int clauses, int fragments, int annotations, int classifications, int riskFlags, int references, int keywords, int indices){

        this.documents = documents;
        this.versions = versions;
        this.clauses = clauses;
        this.fragments = fragments;
        this.annotations = annotations;
        this.classifications = classifications;
        this.riskFlags = riskFlags;
        this.references = references;
        this.keywords = keywords;
        this.indices = indices;
    }

    public DocumentDeleteOutcome() {

    }

    public void add(DocumentDeleteOutcome more) {

        documents       += more.documents;
        versions        += more.versions;
        clauses         += more.clauses;
        fragments       += more.fragments;
        annotations     += more.annotations;
        classifications += more.classifications;
        riskFlags       += more.riskFlags;
        references      += more.references;
        keywords        += more.keywords;
        indices         += more.indices;
    }
}
