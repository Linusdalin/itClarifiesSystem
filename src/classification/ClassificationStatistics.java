package classification;

import net.sf.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-01-21
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class ClassificationStatistics {

    public int direct;
    private int indirect;

    public ClassificationStatistics(){

        direct = 0;
        indirect = 0;
    }

    public void updateHit() {

        direct++;
    }


    public void addIndirectHits(int fromChildren) {
        this.indirect += fromChildren;
    }

    public JSONObject toJSON() {

        JSONObject json = new JSONObject()
                .put("direct", direct)
                .put("indirect", indirect);
        return json;
    }

    public boolean isEmpty() {

        return direct + indirect == 0;
    }
}
