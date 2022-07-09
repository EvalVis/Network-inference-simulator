package utils;

import data.CodeData;
import java.math.BigInteger;

/**
 * Šios klasės tikslas palengvinti vartotojo įvesties nuskaitymą, bei programos duomenų išvedimą vartotojui.
 */
public class TextUtils {

    /**
     * Nuskaitoma viena vartotojo įvedamos matricos eilutė.
     * @param line          eilutė, kurioje turėtų būti tik bitai ir tarpai.
     * @param columns       (n-k), kadangi vienetinė matricos dalis neįvedama.
     * @return              grąžina vektorių, kuris gaunamas sutvarkius vartotojo įvestą eilutę.
     * @throws Exception    jeigu į eilutę pateko kur kas daugiau,
     * nei tik bitai ir tarpai, funkcija yra naudojama netinkamai.
     */
    public static int[] readVector(String line, int columns) throws Exception {
        int[] vector = new int[columns];
            String vectorLine = line.trim().replace(" ", "");
            if (vectorLine.length() != columns) {
                throw new Exception();
            }
            for (int r = 0; r < columns; r++) {
                int bit = Integer.parseInt(String.valueOf(vectorLine.charAt(r)));
                if (bit != 0 && bit != 1) {
                    throw new Exception();
                }
                vector[r] = bit;
            }
            return vector;
    }

    /**
     * Išspausdinamas užkoduotas vektorius.
     * @param code      vektorius - bitų masyvas.
     */
    public static void printCodedVector(int[] code) {
        for (int j : code) {
            System.out.print(j);
        }
        System.out.println();
    }

    /**
     * Grąžinama vektoriaus eilutė pakeičiant bitų masyvą į "String".
     * @param code      bitų masyvas.
     * @return          "String" vektoriaus eilutė.
     */
    public static String intArrayToString(int[] code) {
        StringBuilder text = new StringBuilder();
        for (int j : code) {
            text.append(j);
        }
        return text.toString();
    }

    /**
     * Ieškoma pozicijų, kuriose buvo padarytos klaidos.
     * @param encoded       užkoduotas vektorius, prieš jį siunčiant į kanalą.
     * @param corrupted     iš kanalo gautas, galimai iškraipytas vektorius.
     * @return              grąžinamas tekstas, kuriame nurodoma, kur buvo padarytas klaidos.
     */
    public static String findDifferentPositions(int[] encoded, int[] corrupted) {
        StringBuilder text = new StringBuilder();
        for(int i = 0; i < encoded.length; i++) {
            if(encoded[i] != corrupted[i]) {
                text.append(i + 1).append(" ");
            }
        }
        return text.toString();
    }

    /**
     * Kodas yra pakeičiamas į žinutę. Kadangi naudojama vienetinė matrica, pakanka išspausdinti
     * tik pirmuosius k simbolių.
     * @param code          užkoduota žinutė.
     * @param k             kodo dimensija.
     * @return              atkoduota žinutė.
     */
    public static String codeToMessage(int[] code, int k) {
        StringBuilder text = new StringBuilder();
        for(int i = 0; i < k; i++) {
            text.append(code[i]);
        }
        return text.toString();
    }

    /**
     * Kadangi daugumoje vietoje naudojama bitų eilutė, tenka keisti bitų simbolius į skaičius.
     * @param bit           bito simbolis.
     * @return              bitas skaičiaus pavidalu.
     */
    public static int charBitToInt(char bit) {
        return bit == '0' ? 0 : 1;
    }

    /**
     * Iš galimai iškraipytas bitų sekos, pasinaudojant "BigInteger" klasės metodu "toByteArray()",
     * surandamas teksto atitikmuo.
     * @param corruptedVectors          galimai iškraipytų vektorių rinkinys.
     * @param withCode                  klausiama, ar vektorių rinkinys užkoduotas.
     * @param codeData                  kodo parametrai.
     * @param additionalBitsToAdd       nurodoma, kiek papildomų bitų reikėjo pridėti, kad visi vektoriai
     *                                  būtų reikiamo ilgio.
     * @return                          grąžinamas galimai iškraipytas tekstas.
     */
    public static String getCorruptedText(int[][] corruptedVectors, boolean withCode, CodeData codeData, int additionalBitsToAdd) {
        String corruptedBinary = getCorruptedBinary(corruptedVectors, withCode, codeData, additionalBitsToAdd);
        return new String(new BigInteger(corruptedBinary, 2).toByteArray());
    }

    /**
     * Iš kanalu gauto galimai iškrapytų vektorių rinkinio gaunama bitų seka.
     * Programos efektyvumui pagerinti naudojamas "StringBuilder".
     * @param corruptedVectors      galimai iškraipytų vektorių rinkinys.
     * @param withCode              klausiama, ar vektorių rinkinys buvo užkoduotas, jei taip, reikės
     *                              atkoduoti numetant kodo pabaigą.
     * @param codeData              kodo parametrai.
     * @param additionalBitsToAdd   nurodoma, kiek bitų buvo pridėti siekiant, kad visi siunčiami vektoriai
     *                              būtų vienodo ilgio.
     * @return                      grąžinama galimai iškraipyta bitų seka.
     */
    public static String getCorruptedBinary(int[][] corruptedVectors, boolean withCode, CodeData codeData, int additionalBitsToAdd) {
        StringBuilder corruptedBinary = new StringBuilder();
        for (int[] corruptedVector : corruptedVectors) {
            if (withCode) corruptedBinary.append(TextUtils.codeToMessage(corruptedVector, codeData.getK()));
            else corruptedBinary.append(TextUtils.intArrayToString(corruptedVector));
        }
        corruptedBinary = new StringBuilder(corruptedBinary.substring(0, corruptedBinary.length() - additionalBitsToAdd)); // Numetam prirašytus bitus.
        return corruptedBinary.toString();
    }

    /**
     * Kadangi spalvos turi būti vieno baito, prie gauto dvejetainio
     * skaičiaus pradžios prirašoma tiek nulių, kad eilutė turėtų 8 simbolius.
     * @param binary            dvejetainis skaičius-eilutė.
     * @return                  dvejetainis skaičius-eilutė, turinti 8 simbolius.
     */
    public static String addLeadingZerosToByte(String binary) {
        int amountToAdd = 8 - binary.length();
        return "0".repeat(Math.max(0, amountToAdd)) + binary;
    }

}
