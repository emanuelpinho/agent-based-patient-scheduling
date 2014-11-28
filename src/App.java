import hospital.CommonAgent;
import jade.Boot;

/**
 * Created by Emanuelpinho on 18/11/14.
 */
public class App {

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
 */


