package FirstComeFirstServed;


import java.util.Scanner;

public class FirstComeFirstServed {

    public static void main(String [] args)
    {
        Scanner scan = new Scanner(System.in);

        System.out.println("Qual a opção de testes que pretende correr? (1, 2 ou 3)");
        String option = scan.nextLine();
        Common c = new Common(option);

        c.init();
        c.run();
    }
}
