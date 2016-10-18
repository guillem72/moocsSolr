/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glluch.mooclodecf.moocssolr;

import com.glluch.findterms.termsAndRelated;
import com.glluch.utils.Filename;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    while (cfiles.hasNext()){
        //Out.p("Cfiles");
        File cf=(File) cfiles.next();
        String filename=cf.getCanonicalPath();
       String [] doc2=readFile(filename);
       HashMap <String,Double> terms=tr.find(doc2[1]);
       if (!terms.isEmpty()) moocs.put(doc2[0], terms);
        
    }
    return moocs;
    }
    
      public static String[] readFile(String fileName) throws IOException, ParseException {
         String[] res = new String[2];
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(fileName));
        JSONObject jsonObject = (JSONObject) obj;
        res[0] = (String) jsonObject.get("title");
        if (StringUtils.isEmpty(res[0])) res[0]=Filename.nameWithoutExtension(fileName);
        //if (StringUtils.isEmpty(res[0])) res[0]=fileName;
        res[1] = (String) jsonObject.get("description");//description
        
        return res;
    }
      
      
}
