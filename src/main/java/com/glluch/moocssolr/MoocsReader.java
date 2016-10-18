package com.glluch.moocssolr;

import com.glluch.findterms.*;
import com.glluch.utils.Filename;
import com.glluch.utils.Out;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class MoocsReader {
    public static String infoDirName = "resources/courses/en";
    
    
    public static  HashMap <String, HashMap <String,Double>>  readDir() throws IOException, ParseException{
    HashMap <String, HashMap <String,Double>>  moocs=new HashMap <> ();
    SuffixFileFilter jsonsff=new SuffixFileFilter(".json");
    termsAndRelated tr=new termsAndRelated();
   Iterator<File> cfiles= FileUtils.iterateFiles(new File(infoDirName), jsonsff,null);
   Out.p("Reading directory with MOOCs"); 
   while (cfiles.hasNext()){
        //Out.p("Cfiles");
        File cf=(File) cfiles.next();
        //Out.p("Reading "+cf.getName());
        String filename=cf.getCanonicalPath();
       String [] doc2=readFile(filename);
       if (doc2[1]!=null){
           
       HashMap <String,Double> terms=tr.find(doc2[1]);
       if (!terms.isEmpty()) moocs.put(doc2[0], terms);
                }
       else {
       Out.p("ERROR This file: "+cf.getName()+" doesn't have description!!");
       }
    }
   Out.p("MOOCs retrieved"); 
    return moocs;
    }
    
    /**
     * 
     * @param fileName the json file were the metadata is 
     * (it has to have the fields title and description)
     * @return an string[] with the title and the description
     * @throws IOException 
     * @throws ParseException
     * @throws NullPointerException
     */
    public static String[] readFile(String fileName) throws IOException, ParseException,NullPointerException {
         String[] res = new String[2];
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(fileName));
        JSONObject jsonObject = (JSONObject) obj;
        String title=(String) jsonObject.get("title");
        res[0] = title;
        if (StringUtils.isEmpty(res[0])) res[0]=Filename.nameWithoutExtension(fileName);
        //if (StringUtils.isEmpty(res[0])) res[0]=fileName;
        res[1] = (String) jsonObject.get("description");//description
        
        return res;
    }
      
      
}
