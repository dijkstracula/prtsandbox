package sample.sampleimpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;

public class Ring {
    private BigInteger val;

    private final Logger logger = LogManager.getLogger(this.getClass());

    public Ring() {
        val = BigInteger.valueOf(0);
    }

    public void Add(int i) {
        logger.info("ADD:" + i);
        val.add(BigInteger.valueOf(i));
    }

    public void Mul(int i) {
        logger.info("MUL:" + i);
        val.multiply(BigInteger.valueOf(i));
    }
}
