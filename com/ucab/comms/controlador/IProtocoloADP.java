package controlador;

public interface IProtocoloADP extends IProtocoloT {
	
	public void inicioServidorADD() throws Exception;
	public void inicioServidorADK() throws Exception;
	public void inicioClienteADDK() throws Exception;
	public void envioADD(int puerto) throws Exception;
	public void envioADK(int puerto, String tablero) throws Exception;
	public int desempaquetadoADK(byte[] buffer);
	public int desempaquetadoADD(byte[] buffer);

}
