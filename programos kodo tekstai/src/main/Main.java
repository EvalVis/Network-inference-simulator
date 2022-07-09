package main;

import data.CodeData;
import data.SyndromeTable;
import scenarios.FirstScenario;
import scenarios.SecondScenario;
import scenarios.ThirdScenario;

import java.util.Scanner;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private CodeData codeData;
    private SyndromeTable syndromeTable;

    public Main() {
        inputData();
        chooseScenario();
        scanner.close();
    }

    /**
     * Gaunami pradiniai duomenys ir pagal juos sugeneruojami papildomi, reikalingi koduoti ir atkoduoti pranešimus.
     */
    private void inputData() {
        Input input = new Input(scanner);
        codeData = input.readCodeData();
        syndromeTable = new SyndromeTable(codeData);
    }

    /**
     * Prašome pasirinkti vieną iš egzistuojančių scenarijų.
     */
    private void chooseScenario() {
        System.out.println("Pasirinkite scenarijų (įveskite atitinkamą skaičių):");
        System.out.println("1 - programa užkoduoja, siunčia nepatikimu kanalu ir dekoduoja vartotojo įvestą vektorių.");
        System.out.println("2 - programa siunčia nepatikimu kanalu " +
                "vartotojo įvestą tekstą užkodavus jį ir neužkodavus jo ir parodo skirtumą.");
        System.out.println("3 - programa siunčia nepatikimu kanalu vartotojo įvesto paveiksliuko spalvas" +
                " užkodavus jas ir neužkodavus jų ir parodo skirtumą tarp paveiksliukų.");
        int scenarioNumber = readNumber();
        if(scenarioNumber == 1) {
            new FirstScenario(codeData, syndromeTable, scanner).start();
        }
        else if(scenarioNumber == 2) {
            new SecondScenario(codeData, syndromeTable, scanner).start();
        }
        else if(scenarioNumber == 3) {
            new ThirdScenario(codeData, syndromeTable, scanner).start();
        }

    }

    /**
     * Nuskaitomas scenarijaus pasirinkimas.
     * @return      grąžina skaičių, kuris atitinka egzistuojantį scenarijų.
     */
    private int readNumber() {
        int number = 0;
        while(number != 1 && number != 2 && number != 3) {
            String numberValue = scanner.nextLine();
            try {
                number = Integer.parseInt(numberValue);
                if(number != 1 && number != 2 && number != 3) numberIsWrong();
            } catch(Exception e) {
                numberIsWrong();
            }
        }
        return number;
    }

    /**
     * Jei įvestas skaičius, neatitinka jokio scenarijaus, kviečiama ši funkcija klaidos pranešimui spausdinti.
     */
    private void numberIsWrong() {
        System.out.println("Tokio scenarijaus nėra. Prašome įvesti vieną iš nurodytų skaičių:");
    }

    public static void main(String[] args) {
        new Main();
    }

}
