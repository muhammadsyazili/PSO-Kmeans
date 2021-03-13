package Controller;

import java.util.List;

/**
 * @author Muhammad Syazili
 */
public class DistanceMeasure {
    public double[][] euclideanDistance(double[][] centroids, List<double[]> data){
        double[][] result = new double[data.size()][centroids.length];
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < centroids.length; j++) {
                double temp = 0;
                for (int k = 0; k < centroids[j].length; k++) {
                    temp += Math.pow(Math.abs(data.get(i)[k] - centroids[j][k]), 2);
                }
                result[i][j] = Math.sqrt(temp);
            }
        }
        return result;
    }
}