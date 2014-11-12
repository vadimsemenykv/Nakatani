package com.vadim.nakatani;

/**
 * Created by vadim on 06.11.14.
 */
public class RiodorakyDump {
    private static int[] radiorakyAproks = { 89, 81, 69, 81, 94, 94,   76, 60, 76, 70, 60, 67 };

    private final int[] eks;
    private int[] radioraky;

    private int middleRiodoraky;
    private int lowRiodoraky;
    private int highRiodoraky;

    private int middleEKS;

    public RiodorakyDump(int[] eks) {
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
        middleEKS = m;
        double middleDouble = 0.866 * 100 * Math.log(1 + ((double)m / 76));
        middleRiodoraky = (int) Math.round(middleDouble);

        lowRiodoraky = (int) Math.round(middleDouble - 14/2);
        highRiodoraky = (int) Math.round(middleDouble + 14/2);
    }

    public int[] getEks() {
        return eks;
    }

    public int[] getRiodoraky() {
        return radioraky;
    }

    public int getMiddleRiodoraky() {
        return middleRiodoraky;
    }

    public int getMiddleEKS() {
        return middleEKS;
    }

    public int getLowRiodoraky() {
        return lowRiodoraky;
    }

    public int getHighRiodoraky() {
        return highRiodoraky;
    }
}
