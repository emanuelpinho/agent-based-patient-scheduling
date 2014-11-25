package tests;

import hospital.CommonAgent;
import org.junit.Assert;
import org.junit.Test;
import patient.PatientAgent;

import java.util.ArrayList;

public class MainTests {

    @Test
    public void firstTest() {

        ArrayList<String> symptons = new ArrayList<String>();

        symptons.add("fever");

        CommonAgent c = new CommonAgent();

        PatientAgent p = new PatientAgent(symptons, "teste");

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(true);
    }
}
