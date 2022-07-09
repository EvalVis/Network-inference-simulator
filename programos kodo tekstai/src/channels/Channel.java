package channels;

import utils.CodeMath;
import java.util.Arrays;
import java.util.Random;

/**
 * "Channel" klasė sukuria nepatikimą kanalą be atminties, kuriuo judės pranešimas.
 */

public class Channel {

    private final Random random = new Random();
    private final double errorChance; // Kanalo nepatikimumo rodiklis.

    public Channel(double errorChance) {
        this.errorChance = errorChance;
    }

    /**
     * Atliekamas siuntimas nepatikimu kanalu.
     * @param vector    vektorius (užkoduotas ar ne), siunčiamas kanalu.
     * @return          grąžina iškraipytą (arba tokį, koks buvo paduotas kaip parametras) vektorių.
     */
    public int[] transmit(int[] vector) {
        int[] transmitted = Arrays.copyOf(vector, vector.length);
        for(int i = 0; i < transmitted.length; i++) {
            double value = random.nextDouble();
            if(value <= errorChance) {
                transmitted[i] = CodeMath.changeBit(transmitted[i]);
            }
        }
        return transmitted;
    }

}
