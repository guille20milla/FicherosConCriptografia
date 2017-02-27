package Criptografia;

import java.math.BigInteger;
import java.util.Random;

public class RSA {

    private BigInteger N;
    private BigInteger k;
    private BigInteger e;
    private BigInteger d;

    public RSA() {
        Random r = new Random();
        int bitlength = 1024;
        BigInteger p = BigInteger.probablePrime(bitlength, r);
        BigInteger q = BigInteger.probablePrime(bitlength, r);
        N = p.multiply(q);

        k = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitlength / 2, r);

        while (k.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(k) < 0) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(k);
    }

    public Clave clavePublica() {
        return new Clave(e, N);
    }

    public Clave clavePrivada() {
        return new Clave(d, N);
    }
}
