package pp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFiles {
	BufferedWriter out = null;
	public WriteFiles(String fileName) throws IOException{
		this.out = new BufferedWriter(new FileWriter(fileName));
	}
	public void writeFile(String content,String content1){
	    try{
            this.out.write(content+"      "+content1);
	        this.out.newLine();  //注意\n不一定在各种计算机上都能产生换行的效果
	    }catch (IOException e){                   
	        e.printStackTrace();
	    }
	}
	public void closeWrite() throws IOException{
		this.out.close();
	}
}
