package pt.ulisboa.tecnico.meic.cnv.dto;

import java.lang.reflect.Field;
import java.math.BigInteger;

/**
 * Created by diogo on 14-04-2017.
 */
public class Metric {

    private String instance;
    private BigInteger m_count;
    private BigInteger taken;
    private BigInteger not_taken;
    private String file;
    private Integer sc;
    private Integer sr;
    private Integer wc;
    private Integer wr;
    private Integer coff;
    private Integer roff;

    public Metric(String instance, BigInteger m_count, BigInteger taken, BigInteger not_taken, String file, Integer sceneColumns,
                  Integer sceneRows, Integer windowColumns, Integer windowRows, Integer columnOffset, Integer rowOffset) {
        this.instance = instance;
        this.m_count = m_count;
        this.taken = taken;
        this.not_taken = not_taken;
        this.file = file;
        this.sc = sceneColumns;
        this.sr = sceneRows;
        this.wc = windowColumns;
        this.wr = windowRows;
        this.coff = columnOffset;
        this.roff = rowOffset;
    }

    @Override
    public String toString() {
        String output = "[";
        Field[] fields = this.getClass().getDeclaredFields();
        for(int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                String field_info = fields[i].getName() + ": " + fields[i].get(this).toString();
                output += i == fields.length - 1 ? field_info : field_info + ", ";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        output += "]";
        return output;
    }
}
