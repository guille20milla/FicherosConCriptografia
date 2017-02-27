/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Criptografia;

import java.math.BigInteger;

/**
 *
 * @author Alumno
 */
public class Clave {
    public BigInteger x, n;
	
	public Clave(BigInteger x, BigInteger N){
		this.x = x;
		this.n = N;
	}
	
	public byte[] encriptar(byte[] message) {      
		return (new BigInteger(message)).modPow(x, n).toByteArray(); 
	}
	
	public byte[] desencriptar(byte[] message) {      
		return (new BigInteger(message)).modPow(x, n).toByteArray();
	}
}
