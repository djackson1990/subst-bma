package scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 29/03/2011
 * Time: 2:02:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddGeography {
    public static void main(String[] args){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\cwu080\\Documents\\AustralianLanguage\\pny8.csv"));

            String line ="";
            ArrayList<String> langList = new ArrayList<String>();
            ArrayList<Double> latList = new ArrayList<Double>();
            ArrayList<Double> longList = new ArrayList<Double>();

            while((line = reader.readLine()) != null){

                String[] els = line.split(",");
                langList.add(els[1].toLowerCase());
                latList.add(Double.parseDouble(els[2]));
                longList.add(Double.parseDouble(els[3]));
                //System.out.println(els[1]);

            }
            reader = new BufferedReader(new FileReader("C:\\Users\\cwu080\\Documents\\AustralianLanguage\\PNY8alt194_bin_est_yule_relaxdollo7.xml"));
            PrintWriter writer = new PrintWriter(new PrintWriter("C:\\Users\\cwu080\\Documents\\AustralianLanguage\\PNY8alt194_bin_est_yule_relaxdollo7_bm.xml"));

            while((line = reader.readLine()) != null){
                writer.println(line);
                if(line.indexOf("<taxon id=") > -1){
                    
                    int firstQuoteInd = line.indexOf("\"");
                    int secondQuoteInd = line.lastIndexOf("\"");
                    String taxonName = line.substring(firstQuoteInd+1,secondQuoteInd);
                    int taxonIndex = langList.indexOf(taxonName.toLowerCase());
                    //System.out.println(taxonName+" "+taxonIndex);
                    String tabs = line.substring(0,line.indexOf("<"));
                    String loc = tabs+"\t<attr name=\"location\">"+latList.get(taxonIndex)+" "+longList.get(taxonIndex)+"</attr>";
                    writer.println(loc);


                }

            }
            writer.close();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
