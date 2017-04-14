package pt.ulisboa.tecnico.meic.cnv;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class MSSClient {
    private AmazonDynamoDBClient dynamoClient;
    private DynamoDBMapper mapper = new DynamoDBMapper(dynamoClient);

    public MSSClient(){
        try {
            dynamoClient = new AmazonDynamoDBClient(new ProfileCredentialsProvider().getCredentials());
            dynamoClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
    }

    //http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.html
    public void storeMetric(Metric metric){
        mapper.save(metric);
    }
}
