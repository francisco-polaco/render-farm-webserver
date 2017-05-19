package pt.ulisboa.tecnico.meic.cnv.dto;

import java.lang.reflect.Field;
import java.math.BigInteger;

public class Metric {

    private BigInteger m_count;
    private BigInteger taken;
    private BigInteger not_taken;
    private String file;
    private String sc;
    private String sr;
    private String wc;
    private String wr;
    private String coff;
    private String roff;

    public Metric(BigInteger m_count, BigInteger taken, BigInteger not_taken, String file, String sceneColumns,
                  String sceneRows, String windowColumns, String windowRows, String columnOffset, String rowOffset) {
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
        for (int i = 0; i < fields.length; i++) {
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
