package Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Muhammad Syazili
 */
public class Kmeans {
    public int finalNumberOfIteration;
    public double[][] finalCentroids;
    
    public HashMap<Integer, String[]> doKmeans(double[][] centroids, HashMap<String, double[]> data, int numberOfCluster){
        System.out.println("---------------------- K-MEANS ----------------------");
        System.out.println("\n");

        //konversi HashMap (keys) ke ArrayList
        List<String> dataPaths = data.keySet().stream().collect(Collectors.toList());
        //konversi HashMap (values) ke ArrayList
        List<double[]> dataValues = data.values().stream().collect(Collectors.toList());
        
        int numberOfIteration = 0;
        boolean convergentionCheck = false;
        double[][] oldCentroids = new double[numberOfCluster][dataValues.get(0).length];
        HashMap<Integer, String[]> resultGroupingObject = new HashMap();
        
        System.out.println("pusat klaster awal");
        for (double[] centroid : centroids) {
            System.out.println(Arrays.toString(centroid));
        }
        System.out.println("\n");
        
        do{
            System.out.format("++++++++++++++++++ iterasi ke-%d ++++++++++++++++++\n", numberOfIteration+1);
            System.out.println("\n");
            
            //1. menghitung jarak menggunakan teknik euclidien distance           
            double[][] resultCalcDistance = this.calcDistance(centroids, dataValues);
            
            //debugging
            System.out.println("1. hasil perhitungan jarak");
            for (int i = 0; i < resultCalcDistance.length; i++) {
                System.out.format("data ke-%d = %s\n", i+1, Arrays.toString(resultCalcDistance[i]));
            }
            System.out.println("\n");

            //2. mengelompokkan setiap data ke pusat klaster terdekat berdasarkan hasil perhitungan jarak 
            resultGroupingObject = this.grouping(resultCalcDistance, dataPaths, dataValues.size(), numberOfCluster);

            //debugging
            System.out.println("2. hasil pengelompokkan terhadap pusat klaster terdekat [path , cluster]");
            for (int i = 0; i < resultGroupingObject.size(); i++) {
                System.out.println(Arrays.toString(resultGroupingObject.get(i)));
            }
            System.out.println("\n");

            //simpan pusat klaster lama
            for (int i = 0; i < numberOfCluster; i++) {
                System.arraycopy(centroids[i], 0, oldCentroids[i], 0, dataValues.get(0).length);
            }
            
            //debugging
            System.out.println("3. pusat klaster lama");
            for (double[] tempCentroid : oldCentroids) {
                System.out.println(Arrays.toString(tempCentroid));
            }
            System.out.println("\n");
            
            //3. memperbarui pusat klaster           
            centroids = this.createNewCentroids(numberOfCluster, dataValues.get(0).length, resultGroupingObject, data, dataValues.size());

            //debugging
            System.out.println("4. pusat klaster baru");
            for (double[] tempCentroid : centroids) {
                System.out.println(Arrays.toString(tempCentroid));
            }
            System.out.println("\n");
            
            //4. cek konvergensi
            for (int i = 0; i < numberOfCluster; i++) {
                if (Arrays.equals(centroids[i], oldCentroids[i]) == false) {
                    convergentionCheck = true;
                    break;
                }
                convergentionCheck = false;
            }
            
            numberOfIteration++;
            System.out.println("\n");
        } while(convergentionCheck);
        this.finalNumberOfIteration = numberOfIteration;
        this.finalCentroids = centroids;
        
        System.out.println(resultGroupingObject);
        
        System.out.println("\n");
        return resultGroupingObject;
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
    
    private double[][] createNewCentroids(int numberOfRow, int numberOfColumn, HashMap<Integer, String[]> resultGrouping, HashMap<String, double[]> data, int numberOfDocument){
        double[][] newCentroids = new double[numberOfRow][numberOfColumn];
        for (int i = 0; i < numberOfRow; i++) {
            for (int j = 0; j < numberOfColumn; j++) {

                double sumOfSameCluster = 0;
                int numberOfOneCluster = 0;
                for (int k = 0; k < numberOfDocument; k++) {
                    if (Integer.parseInt(resultGrouping.get(k)[1]) == i) {
                        double[] temp1 = data.get(resultGrouping.get(k)[0]);
                        sumOfSameCluster += temp1[j];
                        numberOfOneCluster++;
                    }
                }
                
                newCentroids[i][j] = sumOfSameCluster/numberOfOneCluster;
                //mencegah agar tidak NaN & Infinity
                if (Double.isNaN(newCentroids[i][j]) || Double.isInfinite(newCentroids[i][j])) {
                    newCentroids[i][j] = (double)0;
                }
            }
        }
        return newCentroids;
    }
}