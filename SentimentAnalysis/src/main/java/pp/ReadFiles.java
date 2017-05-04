package pp;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
public class ReadFiles {	
    FileInputStream fis = null;
    BufferedReader reader = null; 
	public ReadFiles(String fileName) throws FileNotFoundException{
        this.fis = new FileInputStream(fileName);
        this.reader = new BufferedReader(new InputStreamReader(fis));           
	}
	public String getName() throws IOException{
	   
		String line = this.reader.readLine();
		if(line != null){
			line = line.toLowerCase().split("\\s+")[0];
		}
		return line;		
	}
	public String getLine() throws IOException{
		   
		String line = this.reader.readLine();
		if(line != null){
			line = line.toLowerCase();
		}
		return line;		
	}
	/*public String getNameList() throws IOException{
		StringBuilder sb = new StringBuilder();
	    String nameList = "";
        
	    String line = this.reader.readLine().split("\\s+")[0];	
		while(line != null){
			sb.append(line+" ");
            line = this.reader.readLine();
			if(line != null){
				line = line.split("\\s+")[0];
			}
        }
		nameList = sb.toString().toLowerCase();
		return nameList;
	}*/
	public void closeRead() throws IOException{
		this.reader.close();
	    this.fis.close();
	}
}
