package scenarios;

import channels.Channel;
import data.CodeData;
import data.SyndromeTable;
import utils.CodeMath;
import utils.TextUtils;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Antro scenarijaus vykdymas. Įgyvendinti visi reikalavimai.
 */
public class SecondScenario {

    private final CodeData codeData;
    private final SyndromeTable syndromeTable;
    private final Scanner input;
    private String binaryText = "";
    private int[][] vectors;
    private final Channel channel;
    private int additionalBitsToAdd;

    public SecondScenario(CodeData codeData, SyndromeTable syndromeTable, Scanner scanner) {
        this.codeData = codeData;
        this.syndromeTable = syndromeTable;
        input = scanner;
        channel = new Channel(codeData.getErrorChance());
    }

    /**
     * Visas antrasis scenarijus vykdomas čia.
     */
    public void start() {
        readText();
        vectors = CodeMath.splitToVectors(binaryText, codeData);
        additionalBitsToAdd = codeData.getK() - binaryText.length() % codeData.getK();
        if(additionalBitsToAdd == codeData.getK()) additionalBitsToAdd = 0;
        sendWithoutCoding();
        sendWithCoding();
    }

    /**
     * Prašoma vartotojo įvesti tekstą. Tekstas gali būti trumpas arba ilgas, vienos eilučių arba daugiau.
     * Kad būtų pagreintintas programos veikimas naudojame StringBuilder (nes "String" yra "immutable").
     * Kad būtų baigtas teksto nuskaitymas, tuščioje eilutėje reikia nuspausti "Enter".
     * Įvestas tekstas konvertuojamas į bitų seką.
     */
    private void readText() {
        System.out.println("Prašome įvesti tekstą. Tekstas gali būti sudarytas iš vienos daugiau eilučių." +
                " Kai norėsite baigti, spauskite „Enter“:");
        StringBuilder textData = new StringBuilder();
        String enteredText = input.nextLine();
        while(!enteredText.isBlank()) {
            textData.append(enteredText).append("\n");
            enteredText = input.nextLine();
        }
        if(textData.toString().isBlank()) {
            System.out.println("Neįvedėte teksto, programa baigia darbą.");
            System.exit(0);
        }
        binaryText = new BigInteger(textData.toString().getBytes()).toString(2);
    }

    /**
     * Nepatikimu kanalu siunčiamas neužkoduotas pranešimas. Kanalo gale pasirodęs pranešimas iš bitų sekos
     * verčiamas į eilutę ir išspausdinamas.
     */
    private void sendWithoutCoding() {
        System.out.println("Jūsų įvestą tekstą siunčiame neužkoduotą kanalu, " +
                "kuriame tikimybė padaryti klaidą lygi " + codeData.getErrorChance() + ".");
        int[][] corruptedVectors = new int[vectors.length][codeData.getK()];
        for(int i = 0; i < vectors.length; i++) {
            corruptedVectors[i] = channel.transmit(vectors[i]);
        }
        String corruptedText = TextUtils.getCorruptedText(corruptedVectors, false, codeData, additionalBitsToAdd);
        System.out.println("Kanalo gale pasirodė štai toks pranešimas: " + corruptedText);
    }

    /**
     * Nepatikimu kanalu siunčiamas pranešimas prieš tai yra užkoduojamas. Kanalo gale pasirodžiusi bitų seka
     * yra dekoduojama, tada verčiama į eilutę ir išspausdinama.
     */
    private void sendWithCoding() {
        System.out.println("Jūsų įvestą tekstą užkoduojame ir siunčiame tuo pačiu kanalu.");
        int[][] newVectors = CodeMath.encodeSendDecode(vectors, codeData, channel, syndromeTable);
        String corruptedText = TextUtils.getCorruptedText(newVectors, true, codeData, additionalBitsToAdd);
        System.out.println("Kanalo gale pasirodė štai toks pranešimas: " + corruptedText);
    }

}
