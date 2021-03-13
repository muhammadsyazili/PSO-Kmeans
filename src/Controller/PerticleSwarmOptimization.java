package Controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Muhammad Syazili
 */
public class PerticleSwarmOptimization {
    MatrixOperation oMO = new MatrixOperation();
    
    public double[][] doPerticleSwarmOptimization(HashMap<String, double[]> data, int numberOfMaxIteration, int numberOfCluster, int numberOfParticle, double learningRate1, double learningRate2){
        System.out.println("---------------------- PSO ----------------------");
        System.out.println("\n");
        
        //konversi HashMap (keys) ke ArrayList
        List<String> dataPaths = data.keySet().stream().collect(Collectors.toList());
        //konversi HashMap (values) ke ArrayList
        List<double[]> dataValues = data.values().stream().collect(Collectors.toList());

        //1. inisialisasi
        GenerateRandomValues oGRV = new GenerateRandomValues();
        
        int numberOfTerm = dataValues.get(0).length;
        double minValueOfData = oMO.getMinValueInMatrix2DForList(dataValues);
        double maxValueOfData = oMO.getMaxValueInMatrix2DForList(dataValues);
        
        //1.1 inisialisasi partikel
        double[][][] particle = new double[numberOfParticle][numberOfCluster][dataValues.get(0).length];
        for (int i = 0; i < numberOfParticle; i++) {
            particle[i] = oGRV.doGenerateRandomValues(numberOfCluster, numberOfTerm, minValueOfData, maxValueOfData);
        }
        
        //1.2 inisialisasi penyimpan data partikel setiap iterasi 
        double[][][] particleSaved = new double[numberOfParticle][numberOfCluster][dataValues.get(0).length];
        
        //1.3 inisialisasi penyimpan nilai fitness partikel setiap iterasi
        double[] fitnessValueSaved = new double[numberOfParticle];
        
        //1.4 inisialisasi kecepatan awal partikel
        double[] velocity = new double[numberOfParticle];
        for (int i = 0; i < numberOfParticle; i++) {
            velocity[i] = 0;
        }
        
        //1.5 inisialisasi posisi awal partikel
        double[] position = new double[numberOfParticle];
        for (int i = 0; i < numberOfParticle; i++) {
            position[i] = 0;
        }
        
        //1.6 inisialisasi bobot inersia
        double inertia = 1;

        //1.7 menghitung besaran bobot inersia setiap iterasi
        double inertiaEachIteration = ((double)1 / (double)numberOfMaxIteration);
        
        double[] fitnessValue = new double[numberOfParticle];
        for (int i = 0; i < numberOfMaxIteration; i++) {
            System.out.format("-------------------------------- iterasi ke %d --------------------------------\n", i+1);
            
            for (int j = 0; j < numberOfParticle; j++) {
                boolean convergentionCheck = false;
                double[][] oldCentroids = new double[numberOfCluster][dataValues.get(0).length];
                do{
                    //2.1 menghitung jarak menggunakan teknik euclidien distance
                    double[][] resultCalcDistance = this.calcDistance(particle[j], dataValues);

                    //2.2 mengelompokkan setiap data ke pusat klaster terdekat berdasarkan hasil perhitungan jarak
                    HashMap<Integer, String[]> resultGrouping = this.grouping(resultCalcDistance, dataPaths, dataValues.size(), numberOfCluster);

                    //simpan pusat klaster lama
                    for (int k = 0; k < numberOfCluster; k++) {
                        System.arraycopy(particle[j][k], 0, oldCentroids[k], 0, dataValues.get(0).length);
                    }

                    //2.3 memperbarui pusat klaster            
                    particle[j] = this.createNewCentroids(numberOfCluster, dataValues.get(0).length, resultGrouping, data, dataValues.size());

                    //2.4 cek konvergensi
                    for (int k = 0; k < numberOfCluster; k++) {
                        if (Arrays.equals(particle[j][k], oldCentroids[k]) == false) {
                            convergentionCheck = true;
                            break;
                        }
                        convergentionCheck = false;
                    }
                } while(convergentionCheck);
            }
            
            //2.5 menghitung nilai fitness setiap partikel
            for (int j = 0; j < numberOfParticle; j++) {
                double fitness = this.calcFitnessValue(particle[j], dataPaths, dataValues, numberOfCluster, dataValues.size());
                fitnessValue[j] = fitness;
                System.out.format("nilai fitness P-%d I-%d = %f\n", j+1, i+1, fitness);
            }
            System.out.println("\n");
            
            //2.6 menghitung nilai global best
            double GBestValue = 99999;
            for (int j = 0; j < numberOfParticle; j++) {
                if (GBestValue > fitnessValue[j]) {
                    GBestValue = fitnessValue[j];
                }
            }
            System.out.format("nilai G Best I-%d = %f\n", i+1, GBestValue);
            System.out.println("\n");
            
            //2.7 menghitung nilai personal best
            double[] LBestValue = new double[numberOfParticle];
            for (int j = 0; j < numberOfParticle; j++) {
                if (i == 0)
                {
                    //iterasi ke-1, penampung nilai fitness & posisi terbaik setiap partikel masih kosong
                    
                    //simpan nilai personal best
                    LBestValue[j] = fitnessValue[j];
                    
                    //simpan nilai fitness
                    fitnessValueSaved[j] = fitnessValue[j];
                    
                    //simpan posisi terbaik
                    particleSaved[j] = particle[j].clone();
                }
                else
                {
                    if (fitnessValue[j] < fitnessValueSaved[j])
                    {
                        //nilai fitness iterasi sekarang lebih baik dari nilai fitness iterasi sebelumnya
                        
                        //simpan nilai personal best
                        LBestValue[j] = fitnessValue[j];
                        
                        //simpan nilai fitness
                        fitnessValueSaved[j] = fitnessValue[j];

                        //simpan posisi terbaik
                        particleSaved[j] = particle[j].clone();
                    }
                    else
                    {
                        //nilai fitness iterasi sebelumnya lebih baik dari nilai fitness iterasi sekarang
                        
                        //simpan nilai personal best
                        LBestValue[j] = fitnessValueSaved[j];
                        
                        //simpan nilai fitness
                        fitnessValueSaved[j] = fitnessValueSaved[j];

                        //simpan posisi terbaik
                        particleSaved[j] = particleSaved[j];
                    }
                }
            }
            
            for (int j = 0; j < numberOfParticle; j++) {
                System.out.format("nilai P Best P-%d I-%d = %f\n", j+1, i+1, LBestValue[j]);
            }
            System.out.println("\n");

            //2.8 memperbarui kecepatan & posisi
            for (int j = 0; j < numberOfParticle; j++) {
                
                double c1 = ThreadLocalRandom.current().nextDouble(0, 1);
                double c2 = ThreadLocalRandom.current().nextDouble(0, 1);
                
                //2.8.1 memperbarui kecepatan
                velocity[j] = inertia * (velocity[j] + (learningRate1 * c1 * (LBestValue[i] - position[j]) + learningRate2 * c2 * (GBestValue - position[j])));
                System.out.format("kecepatan P-%d I-%d = %f\n", j+1, i+1, velocity[j]);
                
                //2.8.2 memperbarui posisi
                position[j] += velocity[j];
                System.out.format("posisi P-%d I-%d = %f\n", j+1, i+1, position[j]);
                
                //2.8.3 memasukkan hasil memperbarui posisi ke setiap partikel
                for (int k = 0; k < numberOfCluster; k++) {
                    for (int l = 0; l < dataValues.get(0).length; l++) {
                        particle[j][k][l] += position[j];
                    }
                }
                
                //2.8.4 normalisasi hasil pembaruan posisi agar tidak keluar dari batas minimal & maksimal data asli
                for (int k = 0; k < numberOfCluster; k++) {
                    System.out.format("c-%d\n", k+1);
                    for (int l = 0; l < dataValues.get(0).length; l++) {
                        particle[j][k][l] = this.normalization(particle[j], dataValues, particle[j][k][l]);
                        System.out.format("%f |", particle[j][k][l]);
                    }
                    System.out.print("\n");
                }
                System.out.println("\n");
            }
            
            //2.9 increment bobot inersia
            inertia -= inertiaEachIteration;
        }
        
        // menghitung rata-rata nilai fitness semua partikel
        double sumFitness = 0;
        for (int j = 0; j < numberOfParticle; j++) {
            sumFitness += fitnessValue[j];
        }
        System.out.format("rata-rata nilai fitness = %f\n", sumFitness/numberOfParticle);
        
        //mencari indeks partikel terbaik
        int bastParticle = 0;
        double GBestValue = 99999;
        for (int j = 0; j < numberOfParticle; j++) {
            if (GBestValue > fitnessValueSaved[j]) {
                bastParticle = j;
            }
        }
        System.out.format("partikel terbaik -> %d\n", bastParticle+1);
        return particleSaved[bastParticle];  
    }
      
    private double calcFitnessValue(double[][] finalCentroid, List<String> dataKeys, List<double[]> dataValues, int numberOfCluster, int numberOfDocument){
        double[][] resultCalcDistance = this.calcDistance(finalCentroid, dataValues);
        HashMap<Integer, String[]> resultGrouping = this.grouping(resultCalcDistance, dataKeys, numberOfDocument, numberOfCluster);
        
        double resultSimilarityOneCluster = 0;
        for (int i = 0; i < numberOfCluster; i++) {
            double sumOfSameCluster = 0;
            int numberOfDocumentInOneCluster = 0;
            for (int j = 0; j < resultGrouping.size(); j++) {
                if (Integer.parseInt(resultGrouping.get(j)[1]) == i) {
                    sumOfSameCluster += resultCalcDistance[j][i];
                    numberOfDocumentInOneCluster++;
                }
            }
            
            double temp = sumOfSameCluster / numberOfDocumentInOneCluster;
            //mencegah agar tidak NaN & Infinity
            if (Double.isNaN(temp) || Double.isInfinite(temp)) {
                temp = (double)0;
            }
            resultSimilarityOneCluster += temp;
        }
        return resultSimilarityOneCluster / numberOfCluster;
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
    
    private double normalization(double[][] centroids, List<double[]> dataValues, double value){
        double minRange; if (oMO.getMinValueInMatrix2DForList(dataValues) == 0){minRange = 0;} else{minRange = oMO.getMinValueInMatrix2DForList(dataValues);}
        double maxRange; if (oMO.getMaxValueInMatrix2DForList(dataValues) == 0){maxRange = 0;} else{maxRange = oMO.getMaxValueInMatrix2DForList(dataValues);}
        double minValue; if (oMO.getMinValueInMatrix2DForArray(centroids) == 0){minValue = 0;} else{minValue = oMO.getMinValueInMatrix2DForArray(centroids);}
        double maxValue; if (oMO.getMaxValueInMatrix2DForArray(centroids) == 0){maxValue = 0;} else{maxValue = oMO.getMaxValueInMatrix2DForArray(centroids);}

        return (((value - minValue) * (maxRange - minRange)) / (maxValue - minValue)) + minRange;
    }
}