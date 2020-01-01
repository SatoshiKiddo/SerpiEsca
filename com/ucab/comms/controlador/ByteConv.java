package controlador;

import java.util.ArrayList;

public final class ByteConv {
	
	public static String bitString(Integer variable, int length) {
		byte posicion_r = variable.byteValue();
		String cadena = Integer.toBinaryString(posicion_r & 0xFF);
		while(cadena.length() < length) cadena = "0" + cadena;
		return cadena;
	}
	
	public static String byteToString(byte b) {
		String retorno = Integer.toBinaryString(b & 0xFF);
		while(retorno.length()<8) retorno = "0" + retorno;
		return retorno;
	}
	
	public static String getMacAddress(byte[] bs, int inicial) {
		String retorno = "";
		for(int i = inicial; i < inicial + 6; i++) {
			retorno = retorno + byteToString(bs[i]);
		}
		return retorno;
	}
	
	public static String getMacAddress(ArrayList<Byte> bs, int inicial) {
		String retorno = "";
		for(int i = inicial; i < inicial + 6; i++) {
			retorno = retorno + byteToString(bs.get(i));
		}
		return retorno;
	}
	
	public static byte[] arrayListByteToArrayByte(ArrayList<Byte> buffer) {
		byte[] buffer_s= new byte[buffer.size()];
		for(int i=0; i<buffer.size(); i++) {
			buffer_s[i]=buffer.get(i).byteValue();
		}
		return buffer_s;
	}

}
