package data;

import utils.CodeMath;
import utils.TextUtils;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Iš vartotojo įvestų ir pagal juos apskaičiuotų duomenų sukuriame sindromų lentelė.
 * Pagal ją taisysime iš kanalo išėjusias klaidas.
 */
public class SyndromeTable {

    private final CodeData codeData;
    private final HashMap<String, Integer> syndromeLeaderMapping = new HashMap<>(); // Sindromas ir svorio sąryšis.
    //private final HashMap<String, String> forTesting = new HashMap<>(); // for testing.
    private final int[] ohCode;

    public SyndromeTable(CodeData codeData) {
        this.codeData = codeData;
        ohCode = new int[codeData.getN()];
        generateMap();
    }

    /**
     * Pradinis nurodymas sugeneruoti sindromų lentelę ir nulinio vektoriaus įdėjimas (kas rodys, kad vektorius yra
     * kodo žodis). Tolesnis generavimas vykdomas toliau iškviečiamoje funkcijoje rekursyviai.
     */
    private void generateMap() {
        syndromeLeaderMapping.put(TextUtils.intArrayToString(codeData.calculateSyndrome(ohCode)), 0);
        //forTesting.put(TextUtils.intArrayToString(codeData.calculateSyndrome(ohCode)), TextUtils.intArrayToString(ohCode));
        continueMapping(new int[codeData.getN()], 0);
        /*for (String name: forTesting.keySet()) {
            String key = name.toString();
            String value = forTesting.get(name).toString();
            System.out.println(value + " " + key);
        }*/
    }

    /**
     * "Search in depth" algoritmas. Kadangi ieškant gylyn, pirmiau aptinkami sunkesni vektoriai, negalima,
     * apskaičiavus sindromą ir radus tokį patį lentelėje, naująjį išmesti. Reikia patikrinti jų svorius ir,
     * jeigu naujasis yra mažesnio svorio, pakeisti senąjį.
     * @param starting  pradinis vektorius, kurį keisime iteruodami.
     * @param position  nurodo, kurioje pozicijoje esantį bitą keisime.
     */
    private void continueMapping(int[] starting, int position) {
        for(int i = position; i < codeData.getN(); i++) {
            int[] next = Arrays.copyOf(starting, codeData.getN());
            next[i] = 1;
            int weight = CodeMath.calculateVectorWeight(next);
            String syndrome = TextUtils.intArrayToString(codeData.calculateSyndrome(next));
            if(!syndromeLeaderMapping.containsKey(syndrome) || syndromeLeaderMapping.get(syndrome) > weight) {
                syndromeLeaderMapping.put(syndrome, weight);
                //forTesting.put(syndrome, TextUtils.intArrayToString(next));
            }
            continueMapping(next, i+1);
        }
    }

    public HashMap<String, Integer> getSyndromeLeaderMapping() {
        return syndromeLeaderMapping;
    }

}
