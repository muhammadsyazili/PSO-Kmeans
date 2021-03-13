package Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @author Muhammad Syazili
 */
public class DaviesBouldinIndex {
    public double doDaviesBouldinIndex(double[][] finalCentroids, HashMap<String, double[]> data){
        System.out.println("---------------------- DAVIES BOULDIN INDEX ----------------------");
        System.out.println("\n");
        
        //konversi HashMap (keys) ke ArrayList
        List<String> dataPaths = data.keySet().stream().collect(Collectors.toList());
        //konversi HashMap (values) ke ArrayList
        List<double[]> dataValues = data.values().stream().collect(Collectors.toList());
        
        //1. menghitung jarak menggunakan teknik euclidien distance
        double[][] calcDistance = this.calcDistance(finalCentroids, dataValues);
        
        //2. mengelompokkan setiap data ke pusat klaster terdekat berdasarkan hasil perhitungan jarak
        HashMap<Integer, String[]> groupingObject = this.grouping(calcDistance, dataPaths, dataValues.size(), finalCentroids.length);

        //3. menghitung Sum of Square Within-cluster
        double[] SSW = this.SSW(groupingObject, calcDistance, finalCentroids.length);
        
        System.out.println("1. hasil SSW");
        for (int i = 0; i < SSW.length; i++) {
            System.out.format("SSW klaster-%d = %f\n", i+1, SSW[i]);
        }
        System.out.println("\n");
        
        //4. menghitung Sum of Square Between-cluster
        ArrayList<double[]> SSB = this.SSB(finalCentroids, finalCentroids.length);
        
        System.out.println("2. hasil SSB");
        for (int i = 0; i < SSB.size(); i++) {
            System.out.format("SSB (c-%d,c-%d) = %f\n", (int)SSB.get(i)[0]+1, (int)SSB.get(i)[1]+1, SSB.get(i)[2]);
        }
        System.out.println("\n");
        
        //5. menghitung Ratio
        double[] R = this.R(SSW, SSB);
        
        System.out.println("3. hasil Rasio");
        for (int i = 0; i < R.length; i++) {
            System.out.format("Rasio (c-%d,c-%d) = %f\n", (int)SSB.get(i)[0]+1, (int)SSB.get(i)[1]+1, R[i]);
        }
        System.out.println("\n");
        
        //6. menghitung DBI
        double resultDBI = this.DBI(R, finalCentroids.length);

        System.out.println("4. nilai DBI");
        System.out.println(String.valueOf(resultDBI));
        
        return resultDBI;
    }
    
    private double[][] calcDistance(double[][] centroids, List<double[]> data){
        DistanceMeasure oDM = new DistanceMeasure();            
        return oDM.euclideanDistance(centroids, data);
    }
    
    private HashMap<Integer, String[]> grouping(double[][] data, List<String> paths, int numberOfDocument, int numberOfCluster){
        HashMap<Integer, String[]> group = new HashMap<>();
        for (int i = 0; i < numberOfDocument; i++) {
            double distance = data[i][0];
            int cluster = 0;
            for (int j = 0; j < numberOfCluster; j++) {
                if (data[i][j] <= distance) {
                    distance = data[i][j];
                    cluster = j;
                }
            }
            group.put(i, new String[]{paths.get(i), String.valueOf(cluster)});
        }
        return group;
    }
    
    private double[] SSW(HashMap<Integer, String[]> finalClustering, double[][] resultCalcDistance, int numberOfCluster){
        //SSW -> Sum of Square Within-cluster
        double[] SSW = new double[numberOfCluster];
        for (int i = 0; i < numberOfCluster; i++) {
            double tempSumOfSameCluster = 0;
            int numberOfOneCluster = 0;
            for (int j = 0; j < finalClustering.size(); j++) {
                if (Integer.parseInt(finalClustering.get(j)[1]) == i) {
                    tempSumOfSameCluster += resultCalcDistance[j][i];
                    numberOfOneCluster++;
                }
            }
            
            SSW[i] = tempSumOfSameCluster/numberOfOneCluster;
            //mencegah agar tidak NaN & Infinity
            if (Double.isNaN(SSW[i]) || Double.isInfinite(SSW[i])) {
                SSW[i] = (double)0;
            }
        }
        return SSW;
    }
    
    private ArrayList<double[]> SSB(double[][] finalCentroids, int numberOfCluster){
        //SSB -> Sum of Square Between-cluster
        ArrayList<double[]> SSB = new ArrayList();
        for (int i = 0; i < numberOfCluster; i++) {
            for (int j = 0; j < numberOfCluster-(i+1); j++) {
                double temp = 0;
                for (int k = 0; k < finalCentroids[i].length; k++) {
                    temp += Math.pow(finalCentroids[i][k] - finalCentroids[i+j+1][k], 2);
                }
                SSB.add(new double[]{i, i+j+1, Math.abs(temp)});
            }
        }
        return SSB;
    }
    
    private double[] R(double[] SSW, ArrayList<double[]> SSB){
        double[] R = new double[SSB.size()];
        for (int i = 0; i < SSB.size(); i++) {
            
            R[i] = (SSW[(int)SSB.get(i)[0]] + SSW[(int)SSB.get(i)[1]]) / SSB.get(i)[2];
            //mencegah agar tidak NaN & Infinity
            if (Double.isNaN(R[i]) || Double.isInfinite(R[i])) {
                R[i] = (double)0;
            }
        }
        return R;
    }
    
    private double DBI(double[] R, int numberOfCluster){
        double D = 0;
        
        if (numberOfCluster == 2) {
            D = DoubleStream.of(R).sum();
        } else{
            for (int i = 0; i < R.length; i++) {
                for (int j = 0; j < R.length-(i+1); j++) {
                    D += Math.max(R[i], R[i+j+1]);
                    System.out.format("D (R-%d,R-%d) = %f\n", i+1, i+j+1+1, Math.max(R[i], R[i+j+1]));
                }
            }
        }
        
        double DBI = D/numberOfCluster;
        //mencegah agar tidak NaN & Infinity
        if (Double.isNaN(DBI) || Double.isInfinite(DBI)) {
            DBI = (double)0;
        }
        return DBI;
    }
}