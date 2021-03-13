package Controller;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.SingularValueDecomposition;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Muhammad Syazili
 */
public class DimentionalityReduction {
    
    public HashMap<String, double[]> doDimentionReduction(HashMap<String, double[]> data, double thresholdEnergy){
        System.out.println("---------------------- DIMENTIONALITY REDUCTION ----------------------");
        System.out.println("\n");
        
        //konversi HashMap (keys) ke ArrayList
        List<String> dataPaths = data.keySet().stream().collect(Collectors.toList());
        //konversi HashMap (values) ke ArrayList
        List<double[]> dataValues = data.values().stream().collect(Collectors.toList());
        
        //konversi List ke Array
        double[][] dataArray = new double[dataValues.size()][dataValues.get(0).length];
        for (int i = 0; i < dataValues.size(); i++) {
            dataArray[i] = dataValues.get(i);
        }
        
        MatrixOperation oMO = new MatrixOperation();
        
        //cek ordo matriks (sebab SVD hanya bisa memproses jika baris >= kolom)
        double[][] newData;
        boolean transpose;
        if (dataArray.length < dataArray[0].length) { //baris < kolom
            //transpos matriks
            newData = oMO.MatrixTranspose2DDouble(dataArray).clone();
            transpose = true;
        } else{ //baris >= kolom
            newData = dataArray.clone();
            transpose = false;
        }
        
        System.out.println("data sebelum dilakukan reduksi dimensi ===============================================================");
        for (double[] newData1 : newData) {
            for (int j = 0; j < newData1.length; j++) {
                System.out.print(newData1[j] + "|");
            }
            System.out.print("\n");
        }
        
        //menghitung total energi matriks singular
        DoubleMatrix2D oDDM2D = new DenseDoubleMatrix2D(newData);
        SingularValueDecomposition oSVD = new SingularValueDecomposition(oDDM2D);
        
        double[] singularValues = oSVD.getSingularValues();
        
        double resultTotalEnergy = this.calcTotalEnergySingularMatrix(singularValues);
        
        int numberOfReduseMatrix = this.calcNumberOfReduseSingularMatrix(oSVD.getSingularValues(), resultTotalEnergy, thresholdEnergy);
        
        System.out.format("jumlah dimensi matriks S yang direduksi = %d\n", numberOfReduseMatrix);
        System.out.println("\n");
        
        //reduksi matriks S
        double[][] tempS = new double[oSVD.getS().rows()-numberOfReduseMatrix][oSVD.getS().columns()- numberOfReduseMatrix];
        for (int i = 0; i < oSVD.getS().rows()- numberOfReduseMatrix; i++) {
            for (int j = 0; j < oSVD.getS().columns()- numberOfReduseMatrix; j++) {
                tempS[i][j] = oSVD.getS().get(i, j);
            }
        }

        //reduksi matriks U
        double[][] tempU = new double[oSVD.getU().rows()][oSVD.getU().columns()- numberOfReduseMatrix];
        for (int i = 0; i < oSVD.getU().rows(); i++) {
            for (int j = 0; j < oSVD.getU().columns()- numberOfReduseMatrix; j++) {
                tempU[i][j] = oSVD.getU().get(i, j);
            }
        }

        //reduksi matriks V transpose
        double[][] tempVT = new double[oSVD.getV().viewDice().rows()- numberOfReduseMatrix][oSVD.getV().viewDice().columns()];
        for (int i = 0; i < oSVD.getV().viewDice().rows()- numberOfReduseMatrix; i++) {
            for (int j = 0; j < oSVD.getV().viewDice().columns(); j++) {
                tempVT[i][j] = oSVD.getV().viewDice().get(i, j);
            }
        }
        
        //komposisi kembali matriks U, S & VT
        double[][] resultReduse = oMO.MultiplyMatrix2DDouble(oMO.MultiplyMatrix2DDouble(tempU, tempS), tempVT);
        
        //cek apakah matriks data dilakukan transpos atau tidak sebelumnya
        double[][] resultReduseTranspose;
        if (transpose == true) {
            resultReduseTranspose = oMO.MatrixTranspose2DDouble(resultReduse);
        } else{
            resultReduseTranspose = resultReduse;
        }
        
        System.out.println("data sesudah dilakukan reduksi dimensi ===============================================================");
        for (double[] resultReduseTranspose1 : resultReduseTranspose) {
            for (int j = 0; j < resultReduseTranspose1.length; j++) {
                System.out.format("%f |", resultReduseTranspose1[j]);
            }
            System.out.print("\n");
        }
        System.out.println("\n");
        
        //konversi Array ke HashMap
        HashMap<String, double[]> resultDimentionalityReduction = new HashMap();
        for (int i = 0; i < resultReduseTranspose.length; i++) {
            resultDimentionalityReduction.put(dataPaths.get(i), resultReduseTranspose[i]);
        }
        
        System.out.println("\n");
        return resultDimentionalityReduction;
    }
    
    private double calcTotalEnergySingularMatrix(double[] singularMatrix)
    {
        double earlyEnergy = 0;
        for (int i = 0; i < singularMatrix.length; i++) {
            earlyEnergy += (Math.pow(singularMatrix[i], 2));
        }
        return earlyEnergy;
    }
    
    private int calcNumberOfReduseSingularMatrix(double[] singularMatrix, double totalEnergySingularMatrix, double thresholdEnergy)
    {
        int reduse = 1;
        double energyNow;
        do{
            double energy = 0;
            for (int i = 0; i < singularMatrix.length-reduse; i++) {
                energy += (Math.pow(singularMatrix[i], 2));
            }
            energyNow = energy;
            reduse++;
        }while(energyNow/totalEnergySingularMatrix >= thresholdEnergy);
        return reduse-1;
    }
}