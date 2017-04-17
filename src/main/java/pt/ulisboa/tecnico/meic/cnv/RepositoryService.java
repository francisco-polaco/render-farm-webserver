package pt.ulisboa.tecnico.meic.cnv;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import pt.ulisboa.tecnico.meic.cnv.dto.Metric;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class RepositoryService {

    private final String TABLE_NAME = "Metrics";
    private final long READ_CAPACITY = 1L;
    private final long WRITE_CAPACITY = 1L;
    private final String PRIMARY_KEY_NAME = "id";
    private AmazonDynamoDB repository;

    public RepositoryService() {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        repository = AmazonDynamoDBClientBuilder.standard().withRegion("eu-west-1").withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        createTable();
    }

    private void createTable() {
        CreateTableRequest createTableRequest =
                new CreateTableRequest().withTableName(TABLE_NAME)
                        .withKeySchema(new KeySchemaElement().withAttributeName(PRIMARY_KEY_NAME).withKeyType(KeyType.HASH))
                        .withAttributeDefinitions(new AttributeDefinition().withAttributeName(PRIMARY_KEY_NAME).withAttributeType(ScalarAttributeType.S))
                        .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(READ_CAPACITY).withWriteCapacityUnits(WRITE_CAPACITY));

        // Create table if it does not exist yet
        TableUtils.createTableIfNotExists(repository, createTableRequest);
        // wait for the table to move into ACTIVE state
        try {
            TableUtils.waitUntilActive(repository, TABLE_NAME);
        } catch (InterruptedException e) {
            exit("Error creating the Metrics table in DynamoDB!");
        }
        System.out.println("Connected do DynamoDB!");
    }

    public void addMetric(String id, Metric metric) {
        Map<String, AttributeValue> item = newItem(id, metric);
        PutItemRequest putItemRequest = new PutItemRequest(TABLE_NAME, item);
        PutItemResult putItemResult = repository.putItem(putItemRequest);
        if (putItemResult.toString().equals("{}"))
            System.out.println("Successfully added item with id = " + id + ", and Metric = " + metric);
        else
            System.out.println("Result: " + putItemResult);
    }

    private Map<String, AttributeValue> newItem(String id, Metric metric) {
        Map<String, AttributeValue> items = new LinkedHashMap<>();
        items.put(PRIMARY_KEY_NAME, new AttributeValue(id));
        for (Field field : metric.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                items.put(field.getName(), new AttributeValue(field.get(metric).toString()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    /*
    * Only for construction purposes
    */
    private void deleteTable() {
        DeleteTableRequest deleteTableRequest = new DeleteTableRequest(TABLE_NAME);
        TableUtils.deleteTableIfExists(repository, deleteTableRequest);
    }

    private void exit(String msg) {
        System.err.println(msg);
        System.exit(0);
    }

    /*
    Shuts down this client object, releasing any resources that might be held open. This is an optional method,
    and callers are not expected to call it, but can if they want to explicitly release any open resources.
    Once a client has been shutdown, it should not be used to make any more requests.
    */
    public void shutdownConnection() {
        repository.shutdown();
    }

    public AmazonDynamoDB getRepository() {
        return repository;
    }

    public void setRepository(AmazonDynamoDB repository) {
        this.repository = repository;
    }
}
