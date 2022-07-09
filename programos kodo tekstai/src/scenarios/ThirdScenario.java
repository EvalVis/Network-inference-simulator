package scenarios;

import channels.Channel;
import data.CodeData;
import data.SyndromeTable;
import utils.CodeMath;
import utils.TextUtils;
import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.Scanner;

/**
 * Trečiojo scenarijaus vykdymas. Visi reikalavimai įgyvendinti.
 */
public class ThirdScenario {

    private final CodeData codeData;
    private final SyndromeTable syndromeTable;
    private final Scanner input;
    private BufferedImage image;
    private byte[] pixels;
    private int[][] vectors;
    private int additionalBitsToAdd;
    private String rgbBinary = "";
    private final Channel channel;
    private Desktop desktop;

    public ThirdScenario(CodeData codeData, SyndromeTable syndromeTable, Scanner scanner) {
        this.codeData = codeData;
        this.syndromeTable = syndromeTable;
        input = scanner;
        channel = new Channel(codeData.getErrorChance());
    }

    /**
     * Visas trečiasis scenarijus vykdomas čia.
     */
    public void start() {
        openFile();
        convertByteArrayToBinary();
        vectors = CodeMath.splitToVectors(rgbBinary, codeData);
        additionalBitsToAdd = codeData.getK() - rgbBinary.length() % codeData.getK();
        if(additionalBitsToAdd == codeData.getK()) additionalBitsToAdd = 0;
        sendWithoutCode();
        sendWithCode();
    }

    /**
     * Vartotojo prašoma įvesti kelią iki paveiksliuko. Jeigu paveiksliukas egzistuoja, jis atidaromas
     * parodomas. Bandoma jo spalvas konvertuoti į baitų masyvą. Nepasisekus, programa baigia darbą.
     */
    private void openFile() {
        System.out.println("Prašome įvesti kelią iki paveiksliuko:");
        String path = input.nextLine();
        try {
            File file = new File(path);
            if (!Desktop.isDesktopSupported()) {
                System.out.println("Neįmanoma prieiti prie darbalaukio ir atidaryti failo. Programa baigia darbą.");
                System.exit(0);
            }
            desktop = Desktop.getDesktop();
            if(file.exists()) {
                System.out.println("Atidaromas failas-šaltinis.");
                desktop.open(file);
                image = ImageIO.read(file);
                pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            }
            else {
                System.out.println("Nurodytas failas neegzistuoja. Programa baigia darbą");
                System.exit(0);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Bandant atidaryti ar nuskaityti failą įvyko klaida. Programa baigia darbą.");
            System.exit(0);
        }
    }

    /**
     * Baitų masyvas perkeliamas į eilutę, kurioje saugoma bitų seka.
     * Iš kiekvieno baito nuskaitomas rgb formatas. Pirmiau mėlyna, tada žalia, tada raudona.
     * Į eilutę spalvos tokia tvarka ir surašomos viena prie kitos. Tarp skirtingų rgb spalvų nėra
     * jokio žymeklio. Jos bus atskiriamos žinant, kad viena spalva - vienas baitas. Vadinasi, viena rgb spalva
     * yra 3 baitų ilgio.
     * Naudojamas StringBuilder siekiant
     * pagreitinti programos veikimą.
     */
    private void convertByteArrayToBinary() {
        StringBuilder rgbBinaryBuilder = new StringBuilder();
        for(int i = 0; i < pixels.length; i+=3) {
            int blue = ((int) pixels[i] & 0xff);
            int green = (((int) pixels[i + 1] & 0xff));
            int red =  (((int) pixels[i + 2] & 0xff));
            String r = TextUtils.addLeadingZerosToByte(Integer.toBinaryString(red));
            String g = TextUtils.addLeadingZerosToByte(Integer.toBinaryString(green));
            String b = TextUtils.addLeadingZerosToByte(Integer.toBinaryString(blue));
            rgbBinaryBuilder.append(b).append(g).append(r);
        }
        rgbBinary = rgbBinaryBuilder.toString();
    }

    /**
     * Bitų seka, kurioje nurodytos spalvos, siunčiama neužkoduota nepatikimu kanalu.
     * Kanalo gale gauta bitų seka verčiama į spalvas. Sukuriamas ir atidaromas paveiksliukas.
     */
    private void sendWithoutCode() {
        System.out.println("Failo spalvas siunčiame neužkoduotas kanalu, " +
                "kurio klaidos tikimybė: " + codeData.getErrorChance() + ".");
        int[][] corruptedColors = new int[vectors.length][codeData.getK()];
        for(int i = 0; i < vectors.length; i++) {
            corruptedColors[i] = channel.transmit(vectors[i]);
        }
        String corruptedBinary = TextUtils.getCorruptedBinary(corruptedColors, false, codeData, additionalBitsToAdd);
        createNewImage(corruptedBinary, "bekodo.bmp");
    }

    /**
     * Bitų seka, kurioje nurodytos spalvos, siunčiama užkodavus nepatikimu kanalu.
     * Kanalo gale gauta bitų seka atkoduojama ir verčiama į spalvas. Sukuriamas ir atidaromas paveiksliukas.
     */
    private void sendWithCode() {
        System.out.println("Failo spalvas užkoduojame ir siunčiame tuo pačiu kanalu.");
        int[][] codedVectors = CodeMath.encodeSendDecode(vectors, codeData, channel, syndromeTable);
        String corruptedBinary = TextUtils.getCorruptedBinary(codedVectors, true, codeData, additionalBitsToAdd);
        createNewImage(corruptedBinary, "koduotas.bmp");
    }

    /**
     * Iš galimai iškraipytos bitų sekos nuskaitomos spalvos (pirma mėlyna, tada žalia, tada raudona).
     * Jos panaudojamos sukurti naujam, galimai pakeistam, paveiksliukui.
     * Paveiksliukas sukuriamas laikinų failų "Temp" aplanke ir atidaromas.
     * Jeigu nepavyksta sukurti ar atidaryti paveiksliuko, programa baigia darbą.
     * @param corruptedBinary           iškraipyta bitų seka.
     * @param tempFilePath              naujojo failo pavadinimas.
     */
    private void createNewImage(String corruptedBinary, String tempFilePath) {
        File tempFile = null;
        int m = 0;
        BufferedImage changedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < corruptedBinary.length(); i+= (8*3)) {
            String blueByte = corruptedBinary.substring(i, i + 8);
            String greenByte = corruptedBinary.substring(i + 8, i + 16);
            String redByte = corruptedBinary.substring(i + 16, i + 24);
            int blue = Integer.parseInt(blueByte, 2);
            int green = Integer.parseInt(greenByte, 2);
            int red = Integer.parseInt(redByte, 2);
            Color color = new Color(red, green, blue);
            int colorCode = color.getRGB();
            int x = m % image.getWidth();
            int y = m / image.getWidth();
            changedImage.setRGB(x, y, colorCode);
            m++;
        }
        try {
            tempFile = File.createTempFile("tempCodeTheoryFiles", tempFilePath);
            ImageIO.write(changedImage, "bmp", tempFile);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Nepavyko įrašyti iš kanalo grįžusio failo. Programa baigia darbą.");
            System.exit(0);
        }
        try {
            if(tempFile.exists()) {
                System.out.println("Atidaromas failas, kuris buvo sukurtas atkodavus iš kanalo gautas spalvas.");
                desktop.open(tempFile);
            }
        } catch(Exception e) {
            System.out.println("Nepavyko atidaryti naujai sukurto failo.");
        }
    }

}
