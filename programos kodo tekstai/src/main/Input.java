package main;

import data.CodeData;
import utils.CodeMath;
import utils.TextUtils;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Ši klasė apdoroja vartotojo pradinę įvestį (pradinius kodo parametrus).
 */

public class Input {

    private final Scanner input;

    public Input(Scanner scanner) {
        input = scanner;
    }

    public CodeData readCodeData() {
        int k = readK();
        int n = readN(k);
        int[][] matrix = readMatrix(k, n-k);
        double errorChance = readErrorChance();
        return new CodeData(k, n, matrix, errorChance);
    }

    /**
     * Nuskaitomas kodo žodžių ilgis.
     * @param k     reikia paduoti kodo dimensiją, kad patikrintume, ar kodo žodžių ilgis nebus mažesnis už dimensiją.
     * @return      grąžina kodo žodžių ilgį, kuris atitinka reikalavimus.
     */
    private int readN(int k) {
        System.out.println("Prašome įvesti vektoriaus ilgį n, kuriuo bus užkoduojamas pranešimas (t.y. kodo ilgį):");
        int n = 0;
        while(n < k) {
            String nValue = input.nextLine();
            try {
                n = Integer.parseInt(nValue);
                if(n < k) nIsWrong(k);
            } catch(Exception e) {
                nIsWrong(k);
            }
        }
        return n;
    }

    /**
     * Nuskaitoma dimensija.
     * @return      grąžina reikalavimus atitinkančią kodo dimensiją.
     */
    private int readK() {
        System.out.println("Prašome įvesti dimensiją k (į tokio ilgio vektorius bus skaidomas pranešimas jį užkoduojant):");
        int k = 0;
        while(k <= 0) {
            String kValue = input.nextLine();
            try {
                k = Integer.parseInt(kValue);
                if(k <= 0) kIsWrong();
            } catch(Exception e) {
                kIsWrong();
            }
        }
        return k;
    }

    /**
     * Vartotojui leidžiama pasirinkti įvesti matricą arba leisti programai ją sugeneruoti. Priklausomai nuo vartotojo
     * įvesties duodamas nurodymas sugeneruoti arba
     * nuskaityti matricą be vienetinės dalies (programa taupo atmintį ir vienetinės dalies nesaugo).
     * @param rows      kodo dimensija.
     * @param columns   kodo žodžio ilgio ir kodo dimensijos skirtumas (kadangi nėra vienetinės dalies, matrica gaunasi mažesnė).
     * @return          grąžina reikalavimus atitinkančią matricą be vienetinės dalies.
     */
    private int[][] readMatrix(int rows, int columns) {
        System.out.println("Rašykite „taip“, jei norite pats įvesti generuojančią matricą, kitu atveju, ji bus sugeneruota automatiškai.");
        String answer = input.nextLine();
        answer = answer.toLowerCase();
        if(answer.startsWith("taip")) {
            printMatrixEnterRules(rows, columns);
            return readUserMatrixInput(rows, columns);
        }
        else {
            System.out.println("Programa sugeneravo matricą (vienetinė dalis nerodoma): ");
            int[][] matrix = CodeMath.generateMatrix(rows, columns);
            for (int[] ints : matrix) {
                System.out.println(Arrays.toString(ints));
            }
            return matrix;
        }
    }

    /**
     * Nuskaitoma vartotojo įvesta matrica be vienetinės dalies.
     * @param rows      kodo dimensija.
     * @param columns   kodo žodžio ilgio ir kodo dimensijos skirtumas.
     * @return          reikalavimus atitinkančią vartotojo įvestą matricą be vienetinės dalies.
     */
    private int[][] readUserMatrixInput(int rows, int columns) {
        int m = 0; // Matricai sekti.
        int[][] matrix = new int[rows][columns];
        for(int i = 0; i < rows; i++) {
            try {
                int[] vector = TextUtils.readVector(input.nextLine(), columns);
                matrix[m] = vector;
                m++;
            }
            catch(Exception e) {
                enteredVectorIsWrong(columns);
                i--;
            }
        }
        return matrix;
    }

    /**
     * Nuskaito kanalo nepatikimumo rodiklį: klaidos tikimybę.
     * @return      grąžina reikalavimus atitinkančią klaidos tikimybę.
     */
    private double readErrorChance() {
        System.out.println("Prašome įvesti klaidos padarymo kanale tikimybę: (tarp 0 ir 1):");
        double errorChance = -1;
        while(errorChance <= 0 || errorChance >= 1) {
            String errorChanceValue = input.nextLine();
            try {
                errorChance = Double.parseDouble(errorChanceValue);
                if(errorChance <= 0 || errorChance >= 1) errorChanceIsWrong();
            } catch(Exception e) {
                errorChanceIsWrong();
            }
        }
        return errorChance;
    }

    /**
     * Tolimesniuose metoduose spausdinami klaidų pranešimai ir bandoma paaiškinti, kodėl įvestis nebuvo priimta.
     */

    private void errorChanceIsWrong() {
        System.out.println("Neteisinga klaidos tikimybė (turi būti tarp 0 ir 1):");
    }

    private void enteredVectorIsWrong(int columns) {
        System.out.println("Neteisingai įvestas vektorius, vektoriaus ilgis turi būti " + columns + ":");
    }

    private void printMatrixEnterRules(int rows, int columns) {
        System.out.println("Įveskite standartinės vienetinės generuojančios" +
                " matricos dešiniąją dalį (vienetinės dalies " +
                "nereikia, reikia k eilučių ir n-k stulpelių matricos," +
                "Jūsų atveju k = " + rows + ", n-k = " + columns + "):");
    }

    private void nIsWrong(int k) {
        System.out.println("Vektoriaus ilgis n turi būti sveikasis skaičius, didesnis už 0 ir nemažesnis už dimensiją k (kuri lygi: " + k + "):");
    }

    private  void kIsWrong() {
        System.out.println("Dimensija k turi būti sveikasis skaičius, didesnis už 0:");
    }


}
