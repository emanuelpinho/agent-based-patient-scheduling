package main;

import jade.Boot;


public class App {

    public static void main(String [] args)
    {
        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "Common:hospital.CommonAgent;Tests:tests.TestAgent";
        Boot.main(param);
    }
}