package controlador;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public final class ByteConv {
	
	public static void pausas_De_prueba() {
		Scanner scanner = new Scanner(System.in);
		String entrada  ="";
		 do{
		    entrada  = scanner.nextLine();
		    System.out.println(entrada);
		 }
		 while(!entrada.equals("")); 
		 System.out.println("SE PRESIONÓ LA TECLA ENTER");
	}
	
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
		try {
			return new String(bs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
	
	public static String printBytes(byte[] buffer) {
		String impresion= "";
		for(int i = 0; i<buffer.length; i++) {
			impresion = impresion + byteToString(buffer[i]);
		}
		return impresion;
	}
	
	public static String printBytes(ArrayList<Byte> buffer) {
		String impresion= "";
		for(int i = 0; i<buffer.size(); i++) {
			impresion = impresion + byteToString(buffer.get(i));
		}
		return impresion;
	}

}
