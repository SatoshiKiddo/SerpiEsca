package controlador;

import java.util.ArrayList;

public interface IProtocoloGDP extends IProtocoloT {
	
	public void ProcesoGDP() throws Exception;
	public void envioGDK(int puerto);
	public void envioTokenRing(int puerto);
	public int desempaquetadoGDK(ArrayList<Byte> buffer);

}
