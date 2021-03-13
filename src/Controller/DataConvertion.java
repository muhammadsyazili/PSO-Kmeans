package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;
import Entity.mDataConvertion;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Muhammad Syazili
 */
public class DataConvertion {
    mDataConvertion omDC = new mDataConvertion();
    public int numberOfTerm;
    public int numberOfDocument;
    
    public HashMap<String, double[]> doDataConvertion(HashMap<String, String> documents) throws IOException{
        //konversi Map (value) ke ArrayList
        ArrayList<String> documentValues = (ArrayList<String>) documents.values().stream().collect(Collectors.toList());
        //konversi Map (key) ke ArrayList
        ArrayList<String> documentPaths = (ArrayList<String>) documents.keySet().stream().collect(Collectors.toList());
        
        //debugging raw data
        System.out.println("raw data ===========================================");
        documentValues.forEach((d) -> {
            System.out.println(d);
        });
        
        //1. proses case folding & remove punctuation
        documentValues.forEach((d) -> {
            documentValues.set(documentValues.indexOf(d), this.removePunctuation(this.caseFolding((String) d)));
        });
        
        //debugging hasil case folding & remove punctuation
        System.out.println("\n hasil case folding & remove punctuation ===========================================");
        documentValues.forEach((d) -> {
            System.out.println(d);
        });
        
        //2. proses tokenizing
        ArrayList<ArrayList> resultTokenizing = new ArrayList();
        documentValues.forEach((d) -> {
            resultTokenizing.add(this.tokenizing((String) d));
        });
        
        //debugging hasil tokenizing
        System.out.println("\n hasil tokenizing ===========================================");
        for(int i=0; i<resultTokenizing.size();i++){
            System.out.println(resultTokenizing.get(i));
        }
        
        //3. proses stemming
        ArrayList<ArrayList> resultStemming = new ArrayList();
        ArrayList tempStemming = new ArrayList();
        for (int i = 0; i < resultTokenizing.size(); i++) {
            for (int j = 0; j < resultTokenizing.get(i).size(); j++) {
                tempStemming.add(this.stemming((String) resultTokenizing.get(i).get(j)));
            }
            resultStemming.add((ArrayList) tempStemming.clone());
            tempStemming.clear();
        }
        
        //debugging hasil stemming
        System.out.println("\n hasil stemming ===========================================");
        for (int i = 0; i < resultStemming.size(); i++){
            System.out.println(resultStemming.get(i));
        }
        
        //4. proses stop words
        ArrayList<ArrayList> resultStopWords = new ArrayList();
        ArrayList tempStopWords = new ArrayList();
        for(int i=0; i<resultStemming.size(); i++){
            for(int j=0; j<resultStemming.get(i).size(); j++){
                boolean checkStopWord = this.stopWordsRemoval((String) resultStemming.get(i).get(j));
                if(checkStopWord == false){
                    tempStopWords.add(resultStemming.get(i).get(j));
                }
            }
            resultStopWords.add((ArrayList) tempStopWords.clone());
            tempStopWords.clear();
        }
        
        //debugging hasil stop words
        System.out.println("\n hasil stop words ===========================================");
        for (int i = 0; i < resultStopWords.size(); i++){
            System.out.println(resultStopWords.get(i));
        }

        //5. proses membuat terms
        ArrayList resultCreateTerms = this.createTerms(resultStopWords);
        
        //debugging hasil membuat terms
        System.out.println("\n hasil create terms ===========================================");
        for (int i = 0; i < resultCreateTerms.size(); i++) {
            System.out.format("Term ke-%d = %s\n", i, resultCreateTerms.get(i));
        }

        //6. proses perhitungan TF-IDF
        double[][] resultCalcTFIDF = this.calcTFIDF(resultCreateTerms, resultStopWords, documentValues.size());
        
        //debugging hasil perhitungan TF-IDF
        System.out.println("\n hasil TF-IDF ===========================================");
        for (double[] resultCalcTFIDF1 : resultCalcTFIDF) {
            for (int j = 0; j < resultCalcTFIDF1.length; j++) {
                System.out.format("%f |", resultCalcTFIDF1[j]);
            }
            System.out.print("\n");
        }
        
        //meyimpan hasil konversi data ke entitas
        omDC.resultDataConvertion = resultCalcTFIDF;
        
        //set numberOfDocument
        this.numberOfDocument = resultCalcTFIDF.length;
        //set numberOfTerm
        this.numberOfTerm = resultCalcTFIDF[0].length;
        
        //konversi ArrayList ke HashMap
        HashMap<String, double[]> resultDataConvertion = new HashMap<>();
        for (int i = 0; i < documentPaths.size(); i++) {
            resultDataConvertion.put(documentPaths.get(i), omDC.resultDataConvertion[i]);
        }
        return resultDataConvertion;
    }
    
    private  String caseFolding(String document){  
        return document.toLowerCase();    
    }
    
    private String removePunctuation(String document){
        //load removePunctuationDictionary
        String removePunctuationDictionary = omDC.getRemovePunctuationDictionary();
        return document.replaceAll(removePunctuationDictionary, " ");
    }
    
    private ArrayList tokenizing(String document){
        ArrayList<String> resultTokenizing = new ArrayList();
        resultTokenizing.addAll(Arrays.asList(document.split("\\s+")));
        return resultTokenizing;
    }
    
    private boolean stopWordsRemoval(String word) throws IOException{
        //membuang kata yang ada pada kamus stop words
        boolean check = false;
        String[] stopWordsDictionary = omDC.getStopWordsDictionary();
        for(String sw : stopWordsDictionary){
            if(word.equals(sw))
            {
                check = true;
                break;
            }
        }
        return check;
    }
    
    private String stemming(String word) throws IOException{
        // Mulai setup JSastrawi, cukup dijalankan 1 kali

        // JSastrawi lemmatizer membutuhkan kamus kata dasar
        // dalam bentuk Set<String>
        Set<String> dictionary = new HashSet<>();

        // Memuat file kata dasar dari distribusi JSastrawi
        // Jika perlu, anda dapat mengganti file ini dengan kamus anda sendiri
        InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
        BufferedReader oBR = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = oBR.readLine()) != null) {
            dictionary.add(line);
        }

        Lemmatizer oL = new DefaultLemmatizer(dictionary);
        // Selesai setup JSastrawi
        // lemmatizer bisa digunakan berkali-kali
        return oL.lemmatize(word);
    }
    
    private ArrayList createTerms(ArrayList<ArrayList> documents){
        Set<String> term = new HashSet();
        
        //menghilangkan kata yang duplikat dari seluruh dokumen
        documents.forEach((document) -> {
            document.forEach((word) -> {
                term.add((String) documents.get(documents.indexOf(document)).get(document.indexOf(word)));
            });
        });
        //konversi HashSet ke ArrayList
        ArrayList resultCreateTerms = new ArrayList(term);
        return resultCreateTerms;
    }
    
    private double[][] calcTFIDF(ArrayList terms, ArrayList<ArrayList> documents, int numberOfDocument){
        //proses hitung TF
        int[][] TF = new int[documents.size()][terms.size()];
        for (int i = 0; i < documents.size(); i++) {
            for (int j = 0; j < terms.size(); j++) {
                TF[i][j] = Collections.frequency(documents.get(i), terms.get(j));
            }
        }
        
        //proses tranpose TF
        MatrixOperation oMO = new MatrixOperation();
        int[][] TFtranspose = oMO.matrixTranspose2DInt(TF);
        
        //proses hitung IDF
        double[] IDF = new double[TFtranspose.length];
        for(int i=0; i<TFtranspose.length; i++){
            int DF = IntStream.of(TFtranspose[i]).sum();
            IDF[i] = Math.log10(numberOfDocument/DF);
        }
        
        //proses hitung TF * IDF
        double[][] rTFIDF = new double[documents.size()][terms.size()];
        for (int i = 0; i < TF.length; i++) {
            for (int j = 0; j < IDF.length; j++) {
                rTFIDF[i][j] = TF[i][j] * IDF[j];
                //mencegah agar tidak NaN & Infinity
                if (Double.isNaN(rTFIDF[i][j]) || Double.isInfinite(rTFIDF[i][j])) {
                    rTFIDF[i][j] = (double)0;
                }
            }
        }
        return rTFIDF;
    }
}