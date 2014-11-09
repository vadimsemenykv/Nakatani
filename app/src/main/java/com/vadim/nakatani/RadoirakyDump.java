package com.vadim.nakatani;

import java.util.Arrays;

/**
 * Created by vadim on 06.11.14.
 */
public class RadoirakyDump {
    private static int[] radiorakyAproks = { 89, 81, 69, 81, 94, 94,   76, 60, 76, 70, 60, 67 };

    private final int[] eks;
    private int[] radioraky;

    private int  middle;
    private int low;
    private int high;

    public RadoirakyDump(int[] eks) {
        this.eks = eks;
        radioraky = new int[24];
        init();
    }

    private void init() {
        for (int i = 0, j = 0; i < 24; i++, j++) {
            if (i == 6) j = 0;
            if (i == 18) j = 6;
            radioraky[i] = (int)(0.866 * 100 * Math.log(1 + ((double)eks[i] / radiorakyAproks[j])));
        }

        int m = 0;
        for ( int i : eks) {
            m += i;
        }
        m = m/24;
        double middleDouble = 0.866 * 100 * Math.log(1 + ((double)m / 76));
        middle = (int) Math.round(middleDouble);

        low = (int) Math.round(middleDouble - 14/2);
        high = (int) Math.round(middleDouble + 14/2);
    }

    public int[] getEks() {
        return eks;
    }

    public int[] getRadioraky() {
        return radioraky;
    }

    public int getMiddle() {
        return middle;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }
}
