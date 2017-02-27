package Criptografia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dividir {	
	public static List<byte[]> dividirArray(byte[] bts, int tamaño) {
	    List<byte[]> array = new ArrayList<byte[]>();
	    int inicio = 0;
	    
	    while (inicio < bts.length) {
	        int fin = Math.min(bts.length, inicio + tamaño);
	        array.add(Arrays.copyOfRange(bts, inicio, fin));
	        inicio += tamaño;
	    }
	    return array;
	}
}
