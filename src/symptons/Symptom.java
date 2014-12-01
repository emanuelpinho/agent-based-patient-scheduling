package symptons;

/**
 * Created by Emanuelpinho on 19/11/14.
 */
public class Symptom {

    private String name;

    private double decreaseRate;

    private double health;

    private String exam;

    public Symptom(String name){
        this.name = name;
        if (name.compareTo("fever") == 0) {
            this.decreaseRate = 0.6;
            this.health =  0.25;
            exam = "analysis";
        }
        else if (name.compareTo("mulligrubs") == 0){
            this.decreaseRate = 0.3;
            this.health =  0.1;
            exam = "endoscopy";
        }
        else if (name.compareTo("back pain") == 0){
            this.decreaseRate = 0.3;
            this.health =  0.15;
            exam = "resonance";
        }
        else if (name.compareTo("heart palpitations") == 0){
            this.decreaseRate = 0.6;
            this.health =  0.35;
            exam = "electrocardiogram";
        }
        else if (name.compareTo("muscles aches") == 0){
            this.decreaseRate = 0.2;
            this.health =  0.05;
            exam = "sonography";
        }
        else if (name.compareTo("intestinal pain") == 0){
            this.decreaseRate = 0.4;
            this.health =  0.2;
            exam = "colonoscopy";
        }
    }

    public String getExam() {
        return exam;
    }

    public double getDecreaseRate() {
        return decreaseRate;
    }

    public double getHealth() {
        return health;
    }
}
