package pt.ulisboa.tecnico.meic.cnv.dto;

import java.math.BigInteger;

/**
 * Created by diogo on 14-04-2017.
 */
public class Metric {

    private BigInteger m_count;
    private BigInteger taken;
    private BigInteger not_taken;

    public Metric(BigInteger m_count, BigInteger taken, BigInteger not_taken) {
        this.m_count = m_count;
        this.taken = taken;
        this.not_taken = not_taken;
    }

    public BigInteger getM_count() {
        return m_count;
    }

    public void setM_count(BigInteger m_count) {
        this.m_count = m_count;
    }

    public BigInteger getTaken() {
        return taken;
    }

    public void setTaken(BigInteger taken) {
        this.taken = taken;
    }

    public BigInteger getNot_taken() {
        return not_taken;
    }

    public void setNot_taken(BigInteger not_taken) {
        this.not_taken = not_taken;
    }

    @Override
    public String toString() {
        return "[m_count: " + m_count + ", taken: " + taken + ", not_taken: " + not_taken + "]";
    }

}
