/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

/**
 *  POJO class for matching up stuff in the JSON
 * @author Rodrigo
 */
public class KeyFinder implements ContentHandler{
  private Object value;
  private boolean found = false;
  private boolean end = false;
  private String key;
  private String matchKey;
        

    public void setMatchKey(String matchKey){
    this.matchKey = matchKey;
  }
        
    public Object getValue(){
    return value;
  }
        
    public boolean isEnd(){
    return end;
  }
        
    public void setFound(boolean found){
    this.found = found;
  }
        
    public boolean isFound(){
    return found;
  }
        
    /**
     * Start dealing with the JSON throws exception if there is IO error / format error.
     * @throws ParseException
     * @throws IOException
     */
    public void startJSON() throws ParseException, IOException {
    found = false;
    end = false;
  }

    /**
     *
     * @throws ParseException
     * @throws IOException
     */
    public void endJSON() throws ParseException, IOException {
    end = true;
  }

    /**
     * The comparison method (check if the object has the secret key we want) 
     * 
     * @param value the comparison object
     * @return whether the key was found
     * @throws ParseException
     * @throws IOException
     */
    public boolean primitive(Object value) throws ParseException, IOException {
    if(key != null){
      if(key.equals(matchKey)){
        found = true;
        this.value = value;
        key = null;
        return false;
      }
    }
    return true;
  }
/***
 * All the methods below are pretty straight forward POJO stuff.
 */
    
    public boolean startArray() throws ParseException, IOException {
    return true;
  }

    public boolean startObject() throws ParseException, IOException {
    return true;
  }

    public boolean startObjectEntry(String key) throws ParseException, IOException {
    this.key = key;
    return true;
  }
        
    public boolean endArray() throws ParseException, IOException {
    return false;
  }

    public boolean endObject() throws ParseException, IOException {
    return true;
  }

    public boolean endObjectEntry() throws ParseException, IOException {
    return true;
  }

}