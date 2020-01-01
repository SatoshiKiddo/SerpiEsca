package controlador;

import java.util.ArrayList;

public interface IProtocoloT {
	
	public int desempaquetadoTrama(ArrayList<Byte>  buffer);
	public ArrayList<Byte> lecturaTrama();

}
