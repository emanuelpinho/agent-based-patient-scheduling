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


}
