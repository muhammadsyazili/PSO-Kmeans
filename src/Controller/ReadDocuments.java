package Controller;

import Entity.mReadDocuments;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 * @author Muhammad Syazili
 */
public class ReadDocuments {
    mReadDocuments omRD = new mReadDocuments();
    
    public void doReadDocuments(String path) throws FileNotFoundException, IOException{
        File oF = new File(path);
        if(oF.listFiles().length != 0){
            HashMap<String, String> tempDocuments = new HashMap<>();
            
            //filter berkas
            File[] documents = oF.listFiles((File pathname) -> {
                return pathname.getName().endsWith(".txt")&&pathname.isFile();
            });
            
            if (documents.length == 0) {
                JOptionPane.showMessageDialog(null, "berkas format .txt kosong!", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
                //close program
                System.exit(0);
            } else{
                //membaca setiap berkas
                for(File document : documents){
                    String fillDocument = new String();
                    BufferedReader oBR = new BufferedReader(new FileReader(document));
                    String txt;
                    while ((txt = oBR.readLine()) != null){
                       fillDocument = fillDocument + txt + " ";
                    }
                    tempDocuments.put(document.getPath(), fillDocument);
                    oBR.close();
                }
                //menyimpan hasil pembacaan seluruh berkas ke entitas
                omRD.Documents = tempDocuments;
            }
        } else{
            JOptionPane.showMessageDialog(null, "berkas dalam direktori kosong!", "ERROR MESSAGE", JOptionPane.ERROR_MESSAGE);
            //close program
            System.exit(0);
        }
    }
    
    public HashMap<String, String> getResultReadDocuments(){
        return omRD.Documents;
    }
}