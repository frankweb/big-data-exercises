package nearsoft.academy.bigdata.recommendation;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by francisco on 18/08/15.
 */
public class IOAcademy {

    public boolean fileExists(String path){
        return new File(path).exists();
    }

    public void read(String path, IIOAcademy IIOAcademy) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String line = "";
        while((line = br.readLine()) !=  null){
            IIOAcademy.onLine(line);
        }
        br.close();
    }

    public void write(String path, List<String> lines) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));

        for(String line : lines){
            bw.write(line);
            bw.newLine();
        }

        bw.close();
    }

    public long countLines(String path) throws IOException {
        return Files.lines(new File(path).toPath()).count();
    }

}
