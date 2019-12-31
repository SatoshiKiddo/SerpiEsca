package controlador;

public interface IProtocoloGDP extends IProtocoloT {
	
	public void envioGDK(int puerto);
	public void envioTokenRing(int puerto);

}
