package Controller;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Muhammad Syazili
 */
public class GenerateRandomValues {
    public double[][] doGenerateRandomValues(int numberOfRow, int numberOfColumn, double minValue, double maxValue) {
        double[][] tempRandomValue = new double[numberOfRow][numberOfColumn];
        for (int i = 0; i < numberOfRow; i++) {
            for (int j = 0; j < numberOfColumn; j++) {
                tempRandomValue[i][j] = ThreadLocalRandom.current().nextDouble(minValue, maxValue);
            }
        }
        return tempRandomValue;
    }
}