package pt.ulisboa.tecnico.meic.cnv.dto;

import java.lang.reflect.Field;
import java.math.BigInteger;

public class Metric {

    private String instance;
    private String url;
    private BigInteger m_count;
    private BigInteger taken;
    private BigInteger not_taken;

    public Metric(String instance, String url, BigInteger m_count, BigInteger taken, BigInteger not_taken) {
        this.instance = instance;
        this.url = url;
        this.m_count = m_count;
        this.taken = taken;
        this.not_taken = not_taken;
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
