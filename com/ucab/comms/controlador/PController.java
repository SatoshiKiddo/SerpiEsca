package controlador;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;

import com.fazecast.jSerialComm.*;

/**
 * 
 * @author kiddo
 * @version 1.1
 * 
 * PController refiere a la clase principal que se encarga del manejo
 * de los dos puertos que tendran en la funcion cada computadora.
 *
 */
public class PController implements Runnable, ProtocoloADP{
	
	private boolean servidor;
	//ADP completo y establecido
	private boolean ADPEstablished = false;
	private SerialPort[] puertos;
	private String identificador;
	private NetworkInterface mac_address;
	final private int puerto1 = 0;
	final private int puerto2 = 1;
	
	public PController(boolean servidor, String identificador) {
		this.servidor = servidor;
		this.identificador = identificador;
		try {
			this.mac_address= NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error al intentar obtener la MAC del dispositivo.");
		}
		while (this.puertos.length < 2) {
			//Busqueda de puertos disponibles y su asignacion.
			try {
				
				wait(1000);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error al intentar realizar intervalo de espera.");
			}
			this.puertos = SerialPort.getCommPorts();
			System.out.println("Intentando leer los puertos disponibles ...");
		}
		puertos[puerto1].setComPortParameters(2400, 8, 0, 1);
		puertos[puerto2].setComPortParameters(2400, 8, 0, 1);
		while(!puertos[puerto1].isOpen() || !puertos[puerto2].isOpen()) {
			//Abro los puertos para su posterior uso.
			try {
				
				wait(1000);
			}
			catch (Exception e) {
				System.out.println("Error al intentar realizar intervalo de espera.");
			}
			System.out.println("Abriendo puertos de uso del equipo...");
			puertos[puerto2].openPort();
			puertos[puerto1].openPort();
		}
		/* if (this.servidor)
			//se ejecuta el protocolo ADP como servidor
		else */
			new Thread(this).start();
	}
	
	public void envioADD(int puerto) {
		
	}
	
	public void envioTokenRing(int puerto) {
		//Envio del token ring.
	}

	@Override
	public void run() {
		//Metodo de escucha para recibir la trama de los dos puertos.
		
	}

	public void envioADK(int puerto) {
		System.out.println("Enviando trama ADK...");
	}
	
	
	

}
