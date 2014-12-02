package hospital;

import sun.management.Agent;

import java.util.Map;

/**
 * Created by Emanuelpinho on 01/12/14.
 */
public class Doctor extends Agent {

    public static String TYPE = "Doctor";

    /**
     * Experience that the Doctor has in a specific treatment
     */

    private Map<String, Experience> exp;

    /**
     * Patient may be busy if performing some treatment.
     * When busy, a patient won't bid for any treatment auction.
     */
    private boolean busy = false;

    /**
     * Returns patient\'s busy state
     * @return busy
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Set patient\'s busy state
     * @param b Busy state
     */
    public void setBusy(boolean b) {
        this.busy = b;
    }

}
