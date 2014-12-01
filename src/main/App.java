package main;

import jade.Boot;


public class App {

    public static String Common = "Common";

    public static void main(String [] args)
    {
        String[] param = new String[2];
        param[0] = "-gui";
        param[1] = "Common:hospital.CommonAgent";
        Boot.main(param);
    }

}


/*

help:
            ADD PATIENT



        patient = new PatientAgent();

        String name = "patient";

        AgentContainer c = getContainerController();

        try{
            AgentController a = c.acceptNewAgent("paciente", patient);
            a.start();
        }
        catch( Exception e ){
            System.out.println(e.getCause());
        }



        SEND MESSAGE FOR ALL

        public void action() {

            System.out.println("Init Send behavior action");

            AMSAgentDescription[] agents = null;
            try {
                SearchConstraints c = new SearchConstraints();
                c.setMaxResults (new Long(-1));
                agents = AMSService.search(myAgent, new AMSAgentDescription(), c);
            }
            catch (Exception e) {
                System.out.println( "Problem searching AMS: " + e );
                e.printStackTrace();
            }
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setContent("I need a traige consultation");

            for(int i=0; i<agents.length;i++) {
                if (agents[i].getName().compareTo("Common") == 0)
                    msg.addReceiver(agents[i].getName());
            }

            send(msg);

            done = true;
        }




    public void triageConsultation(){

        addBehaviour(new Triage(this));

        // Send message to Common Agent

        AMSAgentDescription[] agents;

        try {
            agents = AMSService.search(this, new AMSAgentDescription());
        }
        catch (Exception e) {
            System.out.println( "Problem searching AMS: " + e);
            e.printStackTrace();
            return;
        }

        ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
        msg.setContent("I need a traige consultation");

        for(AMSAgentDescription agent : agents) {
            if (agent.getName().toString().compareTo("Common") == 0)
                msg.addReceiver(agent.getName());
        }
        send(msg);

    }
 */


