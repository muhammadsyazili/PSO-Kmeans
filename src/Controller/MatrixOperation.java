package Controller;

import java.util.List;

/**
 * @author Muhammad Syazili
 */
public class MatrixOperation {
    
    public double[][] MultiplyMatrix2DDouble(double[][] data1, double[][] data2){
        int numberOfRowData1 = data1.length;
        int numberOfColumnData1 = data1[0].length;
        int numberOfRowData2 = data2.length;
        int numberOfColumnData2 = data2[0].length;
        double[][] tempMultiply = new double[numberOfRowData1][numberOfColumnData2];
        for(int i = 0; i < numberOfRowData1; i++){
            for(int j = 0; j < numberOfColumnData2; j++){
                double total = 0;
                for(int k = 0; k < numberOfColumnData1; k++){
                    total += (data1[i][k] * data2[k][j]);
                }
                tempMultiply[i][j] = total;
            }
        }
        return tempMultiply;
    }
    
    public int[][] matrixTranspose2DInt(int[][] data){
        int[][] transpose = new int[data[0].length][data.length];
        for(int i=0; i<data.length; i++){
            for(int j=0; j<data[0].length; j++){
                transpose[j][i] = (int) data[i][j];
            }
        }
        return transpose;
    }
    
    public double[][] MatrixTranspose2DDouble(double[][] data){
        double[][] transpose = new double[data[0].length][data.length];
        for(int i=0; i<data.length; i++){
            for(int j=0; j<data[0].length; j++){
                transpose[j][i] = (double) data[i][j];
            }
        }
        return transpose;
    }
    
    public double getMinValueInMatrix2DForList(List<double[]> data){
        double min = 99999;
        for (double[] data1 : data) {
            for (int j = 0; j < data.get(0).length; j++) {
                if (min > data1[j]) {
                    min = (double)data1[j];
                }
            }
        }
        return min;
    }
    
    public double getMaxValueInMatrix2DForList(List<double[]> data){
        double max = 0;
        for (double[] data1 : data) {
            for (int j = 0; j < data.get(0).length; j++) {
                if (max < data1[j]) {
                    max = (double)data1[j];
                }
            }
        }
        return max;
    }
    
    public double getMinValueInMatrix2DForArray(double[][] data){
        double min = 99999;
        for (double[] data1 : data) {
            for (int j = 0; j < data1.length; j++) {
                if (min > data1[j]) {
                    min = data1[j];
                }
            }
        }
        return min;
    }
    
    public double getMaxValueInMatrix2DForArray(double[][] data){
        double max = 0;
        for (double[] data1 : data) {
            for (int j = 0; j < data1.length; j++) {
                if (max < data1[j]) {
                    max = data1[j];
                }
            }
        }
        return max;
    }
}
