package utils;

import channels.Channel;
import data.CodeData;
import data.SyndromeTable;
import java.util.Random;
import java.util.HashMap;

/**
 * Šioje klasėje įgyvendinama didžioji dalis kodo logikos.
 * Klasė naudoja statinius metodus, nes funkcijų rezultatai priklauso tik nuo įvedamų duomenų.
 */
public class CodeMath {

    // Kadangi matrica neturi vienetinės dalies, dauginimas gali vykti taip, turint int m = 0 pridedam prie sumos m
    // pozicijos bitą, kitam rate m padidinam.

    /**
     * Sugeneruojama atsitiktinė matrica be vienetinės dalies.
     * @param rows      kodo dimensija (k).
     * @param columns   kodo žodžio ilgis minus dimensija (n-k)
     * @return          grąžina atsitiktinai sugeneruotą matricą iš bitų.
     */
    public static int[][] generateMatrix(int rows, int columns) {
        Random random = new Random();
        int[][] matrix = new int[rows][columns];
        for(int i = 0; i < rows; i++) {
            for(int r = 0; r < columns; r++) {
                matrix[i][r] = random.nextInt(2);
            }
        }
        return matrix;
    }

    /**
     * Grandininio ("step-by-step") kodo realizacija.
     * Pasinaudojus syndromų lentele, atsekant lyderių svorius dekoduojamas vektorius.
     * Siekama gauti nulinį sindromą, kurio lyderio svoris yra 0 (nes tai reiškia, kad vektorius priklauso kodui,
     * kadangi sudauginus kontrolinę matricą su transponuotu kodo žodžiu turi gautis nulinis sindromas).
     * @param syndromeTable     programos pradžioje sugeneruota sindromų lentelė.
     * @param codeData          programos pradžioje vartotojo įvesti ir programos papildyti kodo parametrai.
     * @param corruptedCode     vektorius, kurį siekiama dekoduoti.
     * @return                  jeigu iškraipytas vektorius turėjo tiek klaidų,
     * kiek algoritmas geba ištaisyti, grąžinamas kodo žodis.
     * Jei klaidų buvo padaryta daugiau, grąžinamas galimai neteisingas dekoduotas vektorius.
     */
    public static int[] decodeVector(SyndromeTable syndromeTable, CodeData codeData, int[] corruptedCode) {
        HashMap<String, Integer> syndromeMapping = syndromeTable.getSyndromeLeaderMapping();
        String codeSyndrome = TextUtils.intArrayToString(codeData.calculateSyndrome(corruptedCode));
        int oldWeight = syndromeMapping.get(codeSyndrome);
        if(oldWeight == 0) return corruptedCode;
        int m = 0; // Žingsninis pozicijos sekiklis (kur jis rodys, ten keisime bitus).
        while(true) {
            corruptedCode[m] = CodeMath.changeBit(corruptedCode[m]);
            codeSyndrome = TextUtils.intArrayToString(codeData.calculateSyndrome(corruptedCode));
            int weight = syndromeMapping.get(codeSyndrome);
            if(weight == 0) return corruptedCode;
            if(weight >= oldWeight) {
                corruptedCode[m] = CodeMath.changeBit(corruptedCode[m]);
            }
            else oldWeight = weight;
            m++;
        }
    }

    /**
     * Vektoriaus užkodavimas. Kadangi programa dirba tik su vienetinėmis matricomis įėjusi žinutė m užkoduojama
     * prie jos pridedant m sudauginta su nevienetinės generuojančios matricos dalies duomenimis (t.y. c = m + m x Glikęs).
     * @param message               žinutė, kurią norima užkoduoti.
     * @param generatingMatrix      generuojanti matrica, kuria bus užkoduojama žinutė.
     * @param k                     kodo dimensija.
     * @param n                     kodo žodžio ilgis.
     * @return                      vektorių, priklausantį kodo žodžių aibei.
     */
    public static int[] encodeVector(int[] message, int[][] generatingMatrix, int k, int n) {
        int[] code = new int[n];
        if (k >= 0) System.arraycopy(message, 0, code, 0, k);
        for(int i = 0; i < k; i++) {
            for(int r = k; r < n; r++) {
                int shift = r - k;
                code[r] += (message[i] * generatingMatrix[i][shift]);
                code[r] %= 2;
            }
        }
        return code;
    }

    /**
     * Invertuojamas duotasis bitas.
     * @param bit       bitas, kurį norima invertuoti.
     * @return          grąžina invertuotą bitą.
     */
    public static int changeBit(int bit) {
        return (bit + 1) % 2;
    }

    /**
     * Transponuoja matricą.
     * @param matrix                matrica, kurią norima transponuoti.
     * @param parentMatrixRows      matricos, kurią norima transponuoti eilučių skaičius.
     * @param parentMatrixColumns   matricos, kurią norima transponuoti stulpelių skaičius.
     * @return                      grąžina pradinės matricos transponuotą matricą.
     */
    public static int[][] transpose(int[][] matrix, int parentMatrixRows, int parentMatrixColumns) {
        int[][] matrixT = new int[parentMatrixColumns][parentMatrixRows];
        for(int i = 0; i < parentMatrixRows; i++) {
            for(int r = 0; r < parentMatrixColumns; r++) {
                matrixT[r][i] = matrix[i][r];
            }
        }
        return matrixT;
    }

    /**
     * Apskaičiuoja vektoriaus svorį (t.y. vektoriuje esančių bitų, kurie yra nelygūs nuliui, skaičių).
     * @param vector            vektorius, kurio svorį norime apskaičiuoti.
     * @return                  grąžinamas duotojo vektoriaus svoris.
     */
    public static int calculateVectorWeight(int[] vector) {
        int sum = 0;
        for (int j : vector) {
            if (j != 0) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Apskaičiuojamas iš kanalo išėjusio vektoriaus klaidų skaičius.
     * @param encoded       užkoduotas vektorius prieš jį išsiunčiant į kanalą.
     * @param corrupted     kitame kanalo gale pasirodęs galimai iškraipytas vektorius.
     * @return              galimai iškraipyto vektoriaus klaidų skaičių.
     */
    public static int calculateErrorCount(int[] encoded, int[] corrupted) {
        int difference = 0;
        for(int i = 0; i < encoded.length; i++) {
            if(encoded[i] != corrupted[i]) difference++;
        }
        return difference;
    }

    /**
     * Norimos žinutės užkodavimas, siuntimas kanalu, klaidų ištaisymas..
     * @param vectors           pradinė žinutė.
     * @param codeData          kodo parametrai.
     * @param channel           nepatikimas kanalas.
     * @param syndromeTable     syndromų lentelė.
     * @return                  grąžinamas iš kanalo gautas ir galimai ištaisytas vektorius.
     */
    public static int[][] encodeSendDecode(int[][] vectors, CodeData codeData, Channel channel, SyndromeTable syndromeTable) {
        int[][] newVectors = new int[vectors.length][codeData.getN()];
        // Šiame cikle vyksta užkodavimas, siuntimas, klaidų taisymas.
        for(int i = 0; i < newVectors.length; i++) {
            newVectors[i] = CodeMath.encodeVector(vectors[i], codeData.getMatrix(), codeData.getK(), codeData.getN());
            newVectors[i] = channel.transmit(newVectors[i]);
            newVectors[i] = CodeMath.decodeVector(syndromeTable, codeData, newVectors[i]);
        }
        return newVectors;
    }

    /**
     * Bitų seka išskaidoma į reikiamo ilgio vektorius.
     * @param binaryText        bitų seka.
     * @param codeData          kodo parametrai.
     * @return                  grąžina vektorių masyvą (galima sakyti, matricą), kuriame yra žinutė.
     */
    public static int[][] splitToVectors(String binaryText, CodeData codeData) {
        int vectorCount = binaryText.length() / codeData.getK();
        int additionalBitsToAdd = codeData.getK() - binaryText.length() % codeData.getK();
        if(additionalBitsToAdd == codeData.getK()) additionalBitsToAdd = 0;
        if(additionalBitsToAdd != 0) vectorCount+=1;
        int[][] vectors = new int[vectorCount][codeData.getK()];
        int m = 0;
        int position = 0;
        for(int i = 0; i < binaryText.length(); i++) {
            vectors[position][m] = TextUtils.charBitToInt(binaryText.charAt(i));
            m++;
            if(m == codeData.getK()) {
                m = 0;
                position++;
            }
        }
        return vectors;
    }

}
