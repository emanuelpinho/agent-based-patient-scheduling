package hospital;


public class Experience {

    private int i;

    private double exp;

    public Experience(){
        i=1;
        exp=(0.1*i + 1)/0.1*i;
    }

    public void incrementExp(){
        i++;
        exp = (0.1*i + 1)/0.1*i;
    }

    public double getExp(){
        return exp;
    }
}
