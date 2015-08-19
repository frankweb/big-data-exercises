package nearsoft.academy.bigdata.recommendation;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by francisco on 18/08/15.
 */
public class MovieRecommender {

    private DataModel dataModel;
    private final HashMap<String, Integer> usersMap, productsMap;
    private final String basePath;

    public MovieRecommender(String path) throws IOException {
        this.basePath = path;

        CSVCreator csv = new CSVCreator(path + "movies.txt", path + "movies.csv");
        csv.createCSV();

        this.dataModel = new FileDataModel(new File(path + "movies.csv"));
        this.productsMap = csv.getProductsMap();
        this.usersMap = csv.getUsersMap();
    }

    public List<String> getRecommendationsForUser(String userId) throws TasteException {
        int id = this.usersMap.get(userId);
        UserSimilarity similarity = new PearsonCorrelationSimilarity(this.dataModel);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, this.dataModel);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(this.dataModel, neighborhood, similarity);
        List<RecommendedItem> recommendation = recommender.recommend(id, 3);

        List<String> output = new ArrayList<>();

        for(RecommendedItem item : recommendation){
            for (Map.Entry<String, Integer> e : productsMap.entrySet()) {
                if(e.getValue() == item.getItemID()){
                    output.add(e.getKey());
                    break;
                }
            }
        }

        return output;
    }

    public long getTotalReviews() throws TasteException, IOException {
        return new IOAcademy().countLines(this.basePath + "movies.csv");
    }

    public int getTotalProducts() throws TasteException {
        return this.dataModel.getNumItems();
    }

    public int getTotalUsers() throws TasteException {
        return this.dataModel.getNumUsers();
    }

}
