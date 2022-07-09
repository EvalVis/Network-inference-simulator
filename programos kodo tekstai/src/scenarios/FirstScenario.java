package scenarios;

import channels.Channel;
import data.CodeData;
import data.SyndromeTable;
import utils.CodeMath;
import utils.TextUtils;
import java.util.Scanner;

/**
 * Pirmojo scenarijaus vykdymas. Įgyvendinti visi nurodymai.
 */

public class FirstScenario {

    private final CodeData codeData;
    private int[] message;
    private int[] code;
    private final Scanner input;
    private int[] corruptedCode;
    private final SyndromeTable syndromeTable;
    private int[] decodedCode;

    public FirstScenario(CodeData codeData, SyndromeTable syndromeTable, Scanner scanner) {
        this.codeData = codeData;
        this.syndromeTable = syndromeTable;
        input = scanner;
    }

    /**
     * Viso pirmojo scenarijaus vykdymas vyksta čia.
     */
    public void start() {
        readVector();
        encodeMessage();
        sendViaChannel();
        reportErrors();
        changeBeforeDecode(); // Vartotojas gali modifikuoti iš kanalo gautą vektorių prieš algoritmui dekoduojant.
        decodedCode = CodeMath.decodeVector(syndromeTable, codeData, corruptedCode);
        printDecodedVector();
    }

    /**
     * Nuskaitomas žinutės vektorius ir patikrinama, ar jo ilgis teisingas. Jei ilgis neteisingas, prašoma įvesti
     * dar kartą.
     */
    private void readVector() {
        System.out.println("Prašome įvesti " + codeData.getK() + " ilgio vektorių:");
        while(message == null) {
            try {
                message = TextUtils.readVector(input.nextLine(), codeData.getK());
            } catch (Exception e) {
                System.out.println("Neteisingai įvestas vektorius. Patikrinkite, ar " +
                        "vektoriaus ilgis tikrai " + codeData.getK() + " ir ar vektorių užrašėte tik bitais.");
            }
        }
    }

    /**
     * Žinutės užkodavimas bei užkoduoto vektoriaus spausdinimas.
     */
    private void encodeMessage() {
        code = CodeMath.encodeVector(message, codeData.getMatrix(), codeData.getK(), codeData.getN());
        System.out.println("Štai užkoduotas vektorius:");
        TextUtils.printCodedVector(code);
    }

    /**
     * Kodo siuntimas nepatikimu kanalu. Iškraipyto vektoriaus gavimas bei išspausdinimas.
     */
    private void sendViaChannel() {
        Channel channel = new Channel(codeData.getErrorChance());
        corruptedCode = channel.transmit(code);
        System.out.println("Kodas buvo nusiųstas kanalu, " +
                "kurio iškraipymo tikimybė: " + codeData.getErrorChance() + ". " +
                "Štai koks vektorius buvo gautas kanalo gale:");
        TextUtils.printCodedVector(corruptedCode);
    }

    /**
     * Vartotojui suteikiama galimybė pakeisti iš kanalo išėjusį vektorių.
     * Šiuo atveju tiesiog leidžiama įvesti naują vektorių.
     */
    private void changeBeforeDecode() {
        System.out.println("Jei įvesite naują vektorių, algoritmas jį panaudos dekoduodamas, kitu atveju spauskite „Enter“:");
        while(true) {
            String data = input.nextLine();
            if(data.isBlank()) break;
        else {
            try {
                corruptedCode = TextUtils.readVector(data, codeData.getN());
                break;
            } catch (Exception e) {
                System.out.println("Klaida įvedant naują vektorių, prašome " +
                        "koduoti bitais. Jei nenorite įvesti naujo vektoriaus, prašome spausti „Enter“:");
            }
        }
        }
    }

    /**
     * Išspausdinamas klaidų kiekis ir jų pozicijos vektoriui išėjus iš kanalo.
     */
    private void reportErrors() {
        int errorCount = CodeMath.calculateErrorCount(code, corruptedCode);
        System.out.println("Iš viso buvo padaryta: " + errorCount + " klaidos (klaidų).");
        System.out.println("Klaidų pozicijos (pirma pozicija žymima " +
                "vienetu): " + TextUtils.findDifferentPositions(code, corruptedCode));
    }

    /**
     * Išspausdinamas atkoduotas vektorius (vienetinės matricos atveju tiesiog paimam vektoriaus pradžią ilgio k).
     */
    private void printDecodedVector() {
        System.out.println("Atkoduotas vektorius: " + TextUtils.codeToMessage(decodedCode, codeData.getK()));
    }

}
