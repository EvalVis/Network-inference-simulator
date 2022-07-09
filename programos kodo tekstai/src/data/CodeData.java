package data;

import utils.CodeMath;

/**
 * Klasėje laikomi pradiniai vartotojo įvesti ir pagal juos apskaičiuoti kodavimui reikalingi duomenys.
 */

public class CodeData {

    private final int k; // dimensija.
    private final int n; // kodo ilgis.
    private final int[][] matrix; // generuojanti matrica be vienetinės matricos dalies (joje tik k eilučių ir n-k stulpelių).
    private final int[][] parityMatrix; //kontrolinė matrica be vienetinės dalies (n-k eilučių, k stulpelių).
    // Kitaip tariant, tai generuojančios matricos be vienetinės dalies transponuota matrica.
    private final double errorChance;

    public CodeData(int k, int n, int[][] matrix, double errorChance) {
        this.k = k;
        this.n = n;
        this.matrix = matrix;
        this.errorChance = errorChance;
        parityMatrix = CodeMath.transpose(matrix, k, n-k); // Kadangi nėra vienetinės dalies, užtenka transponuoti.
    }

    public int getN() {
        return n;
    }

    public int getK() {
        return k;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public double getErrorChance() {
        return errorChance;
    }

    /**
     * Klasėje laikoma kontrolinė matrica privati, todėl sindromas apskaičiuojamas klasės viduje šiame metode.
     * @param vector    vektorius, kurio sindromą norime apskaičiuoti.
     * @return          grąžinamas paduoto vektoriaus sindromas.
     */
    public int[] calculateSyndrome(int[] vector) {
        int[] syndrome = new int[n-k];
        for(int i = 0; i < (n-k); i++) {
            for(int r = 0; r < k; r++) {
                syndrome[i] += (parityMatrix[i][r] * vector[r]); // Transpoziciją atlieku tiesiog čia ir dabar.
            }
            syndrome[i] += vector[k + i]; // vienetinės dalies pasirodymas.
            syndrome[i] %= 2; // Mūsų kūnas yra dvejetainis.
        }
        return syndrome;
    }

}
