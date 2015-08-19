package nearsoft.academy.bigdata.recommendation;

import com.google.common.base.Joiner;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by francisco on 18/08/15.
 */
public class CSVCreator {

    private final String inputPath, outputPath;
    private HashMap<String, Integer> usersMap;
    private HashMap productsMap;
    private int counterUsers = 1, counterProducts = 1;
    private final IOAcademy ioAcademy;

    public CSVCreator(String inputPath, String outputPath) throws IOException {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        usersMap = new HashMap<>();
        productsMap = new HashMap<>();
        ioAcademy = new IOAcademy();
    }

    public HashMap<String, Integer> getProductsMap() {
        return productsMap;
    }

    public HashMap<String, Integer> getUsersMap() {
        return usersMap;
    }

    private HashMap<String, Integer> loadHashMap(String path) throws IOException {
        final HashMap<String, Integer> map = new HashMap<>();
        this.ioAcademy.read(path, line -> map.put(line.split(":")[0], Integer.parseInt(line.split(":")[1])));
        return map;
    }

    private void loadMaps() throws IOException {
        String[] arrayPath = outputPath.split("/");
        arrayPath[arrayPath.length - 1] = "users.txt";
        this.usersMap = loadHashMap(Joiner.on("/").join(arrayPath));
        arrayPath[arrayPath.length - 1] = "products.txt";
        this.productsMap = loadHashMap(Joiner.on("/").join(arrayPath));
    }

    private void writeMap(String path, HashMap map) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
        Set<String> keys = map.keySet();

        for(String key : keys){
            bw.write(key + ":" + map.get(key));
            bw.newLine();
        }

        bw.close();
    }

    public void createCSV() throws IOException {
        if(this.ioAcademy.fileExists(outputPath)){
            loadMaps();
            return;
        }

        final String[] userId = {""};
        final String[] productId = {""};
        final String[] value = new String[1];
        final ArrayList<String> output = new ArrayList<String>();

        this.ioAcademy.read(inputPath, line -> {
            if (line.contains("product/productId")) {
                String id = line.split(":")[1].trim();
                if (!productsMap.containsKey(id)) {
                    productsMap.put(id, ++counterProducts);
                    productId[0] = "" + counterProducts;
                } else {
                    productId[0] = String.valueOf(productsMap.get(id));
                }
            } else if (line.contains("review/userId")) {
                String id = line.split(":")[1].trim();

                if (!usersMap.containsKey(id)) {
                    usersMap.put(id, ++counterUsers);
                    userId[0] = "" + counterUsers;
                } else {
                    userId[0] = String.valueOf(usersMap.get(id));
                }
            } else if (line.contains("review/score")) {
                value[0] = line.split(":")[1].trim();
                output.add(String.format("%s,%s,%s", userId[0], productId[0], value[0]));
            }
        });

        this.ioAcademy.write(outputPath, output);

        String[] arrayPath = outputPath.split("/");
        arrayPath[arrayPath.length - 1] = "users.txt";
        writeMap(Joiner.on("/").join(arrayPath), usersMap);

        arrayPath[arrayPath.length - 1] = "products.txt";
        writeMap(Joiner.on("/").join(arrayPath), productsMap);
    }

}
