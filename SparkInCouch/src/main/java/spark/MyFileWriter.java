package spark;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {

    private BufferedWriter out = null;

    public MyFileWriter(String fileName) throws IOException {
        this.out = new BufferedWriter(new FileWriter(fileName));
    }

    public void writeFile(String content) {
        try {
            this.out.write(content);
            this.out.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWrite() throws IOException {
        this.out.close();
    }
}
