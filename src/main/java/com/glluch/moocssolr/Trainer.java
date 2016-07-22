
package com.glluch.moocssolr;

import com.glluch.utils.Out;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.Classifier;

/**
 * 
 * @author Guillem LLuch Moll guillem72@gmail.com
 */
public class Trainer {
    public static String infoDirName = "resources/ml/";
    
    public void evaluateCerts() throws IOException, Exception{
  
    SuffixFileFilter sff=new SuffixFileFilter(".csv");
   Iterator<File> cfiles= FileUtils.iterateFiles(new File(infoDirName), sff,null);
    while (cfiles.hasNext()){
       
        Classifiers cls=new Classifiers();
        cls.init(cfiles.next());
       // Out.p(cfiles.next().getName());
        cls.evaluateNeigh();
      
        //cls.evaluateLinear();
        
        //E9, D1 0'644
    }
     }
    
    
    public Models buildModels() throws IOException, Exception,NoSuchElementException {
   Models ms=new Models();
    SuffixFileFilter sff=new SuffixFileFilter(".csv");
   Iterator<File> cfiles= FileUtils.iterateFiles(new File(infoDirName), sff,null);//infoDirName = "resources/ml/"
    while (cfiles.hasNext()){
       File f=cfiles.next();
        Classifiers cls=new Classifiers();
        cls.init(f);
        String id=StringUtils.substring(f.getName(), 0, 4);
        if (StringUtils.endsWith(id, ".")) id=StringUtils.substring(id, 0, 3);;
        double evaluateNeigh = cls.evaluateNeigh();
        Out.p(id+", cc="+evaluateNeigh);
       
       
        Classifier c=cls.Neigh();
        
          //Classifier c=cls.gauss();
        
      // cls.debugData();
        ms.put(id, c);
        
    }
    return ms;
     }
    
    
}
