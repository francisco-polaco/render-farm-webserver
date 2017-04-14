package pt.ulisboa.tecnico.meic.cnv;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.math.BigInteger;
import java.util.UUID;

@DynamoDBTable(tableName = "metrics")
public class Metric {
    UUID requestId;
    BigInteger iCount;
    BigInteger bCount;
    BigInteger mCount;
    String file;
    Integer sceneColumns;
    Integer sceneRows;
    Integer windowColumns;
    Integer windowRows;
    Integer columnOffset;
    Integer rowOffset;

    public Metric(UUID requestId, BigInteger iCount, BigInteger bCount, BigInteger mCount, String file, Integer sceneColumns, Integer sceneRows, Integer windowColumns, Integer windowRows, Integer columnOffset, Integer rowOffset) {
        this.requestId = requestId;
        this.iCount = iCount;
        this.bCount = bCount;
        this.mCount = mCount;
        this.file = file;
        this.sceneColumns = sceneColumns;
        this.sceneRows = sceneRows;
        this.windowColumns = windowColumns;
        this.windowRows = windowRows;
        this.columnOffset = columnOffset;
        this.rowOffset = rowOffset;
    }

    @DynamoDBHashKey
    public UUID getRequestId() { return requestId; }
    public void setRequestId(UUID requestId) { this.requestId = requestId; }

    @DynamoDBAttribute
    public BigInteger getiCount() { return this.iCount; }
    public void setiCount(BigInteger iCount) {this.iCount = iCount; }


    @DynamoDBAttribute
    public BigInteger getbCount() { return bCount; }
    public void setbCount(BigInteger bCount) { this.bCount = bCount; }

    @DynamoDBAttribute
    public BigInteger getmCount() { return mCount; }
    public void setmCount(BigInteger mCount) { this.mCount = mCount; }

    @DynamoDBAttribute
    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
    
    @DynamoDBAttribute
    public Integer getSceneColumns() { return sceneColumns; }
    public void setSceneColumns(Integer sceneColumns) { this.sceneColumns = sceneColumns ; }

    @DynamoDBAttribute
    public Integer getSceneRows() { return sceneRows ; }
    public void setSceneRows(Integer sceneRows) { this.sceneRows = sceneRows ; }

    @DynamoDBAttribute
    public Integer getWindowColumns() { return windowColumns ; }
    public void setWindowColumns(Integer windowColumns) { this.windowColumns = windowColumns ; }

    @DynamoDBAttribute
    public Integer getWindowRows() { return windowRows ; }
    public void setWindowRows(Integer windowRows) { this.windowRows = windowRows ; }

    @DynamoDBAttribute
    public Integer getColumnOffset() { return columnOffset ; }
    public void setColumnOffset(Integer columnOffset) { this.columnOffset = columnOffset ; }

    @DynamoDBAttribute
    public Integer getRowOffset() { return rowOffset ; }
    public void setRowOffset(Integer rowOffset) { this.rowOffset = rowOffset ; }
}
