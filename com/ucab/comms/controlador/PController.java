package controlador;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;

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
public class PController implements IProtocoloADP, IProtocoloGDP{
	
	private boolean servidor;
	//ADP completo y establecido
	private boolean ADPEstablished = false;
	private boolean ADKEstablished = false;
	private SerialPort[] puertos;
	private String identificador;
	private NetworkInterface mac_address;
	private int puertoSalida;
	private int puertoEntrada;
	final private int puerto1 = 0;
	final private int puerto2 = 1;
	final private int limitante = 10;
	
	public PController(boolean servidor) {
		this.servidor = servidor;
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
			
	}
	
	public void envioADD(int puerto) throws Exception {
		System.out.println("Enviando trama ADD...");
		byte[] identificador_add= new byte[7];
		TramaADD trama_envio;
		for(int i=0; i<6; i++) {
			identificador_add[i] = this.mac_address.getHardwareAddress()[i];
		}
		identificador_add[6] = this.identificador.getBytes()[0];
		trama_envio = new TramaADD(identificador_add, "FFFFFFFFFFFF".getBytes(), this.mac_address.getHardwareAddress());
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puerto1].writeBytes(data, data.length);
	}

	public void envioADK(int puerto, String tablero) throws Exception {
		System.out.println("Enviando trama ADK...");
		byte[] identificador_add= new byte[7];
		TramaADK trama_envio;
		for(int i=0; i<6; i++) {
			identificador_add[i] = this.mac_address.getHardwareAddress()[i];
		}
		identificador_add[6] = this.identificador.getBytes()[0];
		trama_envio = new TramaADK(identificador_add, "FFFFFFFFFFFF".getBytes(), this.mac_address.getHardwareAddress(), tablero);
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puerto1].writeBytes(data, data.length);
		
	}
	
	public void envioGDK(int puerto) {
		System.out.println("Enviando trama GDK...");
		
	}
	
	public void envioTokenRing(int puerto) {
		System.out.println("Enviando trama ADK...");
		
	}

	@Override
	public void inicioServidorADD() throws Exception {
		byte[] buffer;
		int i;
		int repeticion_paquetes = 0;
		// Se genera el inicio del protocolo ADP cumpliendo el papel del servidor.
		//Se puede agregar como condicion dentro del while un raise event para poder salir y seleccionar cliente.
		while (!this.ADPEstablished) {
			//Se coloca aca el codigo para seleccionar el identificador del equipo.
			//Se coloca aca el codigo para seleccionar el puerto aleatorio de envio.
			//Colocar un cronometro de espera en este punto.
			//Espera de llegada del paquete.
			//Se coloca cual es el puerto de entrada  y el puerto de salida
			this.puertoEntrada= this.puerto2;
			this.puertoSalida= this.puerto1;
			i=0;
			while(this.puertos[this.puerto2].bytesAvailable() < 21) {
				try {
					this.envioADD(this.puerto1);
					wait(1000);
					System.out.println("Tiempo transcurrido de espera " + Integer.toString(i) + " segundos...");
					if(i == 10) {
						i = -1;
						break;
					}
					i++;
				}
				catch(Exception e) {
					System.out.println("Error al intentar esperar la llegada del paquete");
					e.printStackTrace();
				}
			}
			if(i < 0) {
				throw new Exception("Protocolo ADP no completado");
			}
			buffer= new byte[this.puertos[this.puerto2].bytesAvailable()];
			this.puertos[this.puerto2].readBytes(buffer, buffer.length);
			switch(this.desempaquetadoADD(buffer)) {
				case -1:
					if(repeticion_paquetes < limitante)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP");
					}
			}
			this.ADPEstablished= true;
		}
	}

	@Override
	public void inicioServidorADK() throws Exception {
		byte[] buffer;
		int i;
		int repeticion_paquetes = 0;
		while(!this.ADKEstablished) {
			//Se coloca aca el codigo para llenar la info necesaria sobre los jugadores.
			String tablero="0000";
			//Se coloca aca el codigo para seleccionar el valor del tablero.
			i=0;
			//Espera de llegada del paquete final
			System.out.println("Esperando Aknowledgemente de todos los equipos involucrados...");
			while(this.puertos[this.puerto2].bytesAvailable() < 21) {
				try {
					this.envioADK(this.puerto2, tablero);
					wait(1000);
					System.out.println("Tiempo transcurrido de espera " + Integer.toString(i) + " segundos...");
					if(i == 10) {
						i = -1;
						break;
					}
					i++;
				}
				catch(Exception e) {
					System.out.println("Error al intentar esperar la llegada del paquete");
					e.printStackTrace();
				}
			}
			if(i < 0) {
				throw new Exception("Protocolo ADP no completado");
			}
			buffer= new byte[this.puertos[this.puerto2].bytesAvailable()];
			this.puertos[this.puerto2].readBytes(buffer, buffer.length);
			switch(this.desempaquetadoADK(buffer)) {
				case -1:
					if(repeticion_paquetes < limitante)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP");
					}
			}
			this.ADKEstablished = true;
		}
		
	}

	@Override
	public void inicioClienteADDK() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int desempaquetadoADK(byte[] buffer) {
		if(desempaquetadoTrama(buffer) == 1 ) {
			
		}
		return -1;
	}

	@Override
	public int desempaquetadoADD(byte[] buffer) {
		if(desempaquetadoTrama(buffer) == 2 ) {
			if(this.servidor) {
				//Se formatea la informacion de los jugadores para la facilidad de la aplicacion.
				ArrayList<DataJugador> datos_jugadores = new ArrayList<DataJugador>();
				DataJugador.llenadoData(buffer, datos_jugadores);
				//Codigo de llenado de informacion con los datos de los demas jugadores en conjunto con la determinacion del tablero.
			}
			return 0;
		}
		return -1;
	}
	
	// 5: token ring
	// 1-4: procedimientos de los protocolos ADP y GDP.
	@Override
	public int desempaquetadoTrama(byte[] buffer) {
		String direccion_final = ByteConv.getMacAddress(buffer, 1);
		String control_s_protocolo = ByteConv.byteToString(buffer[13]);
		try {
			if (direccion_final != ByteConv.getMacAddress(this.mac_address.getHardwareAddress(), 0)) {
				if (direccion_final != "FFFFFFFFFFFF") {
					//La direccion final no coincide con el equipo que la recibe, por lo tanto se debe reenviar al siguiente nodo.
					this.puertos[this.puertoSalida].writeBytes(buffer, buffer.length);
					return -1;
				}
			}
			if (control_s_protocolo.substring(1, 4) == "0001") {
				return 5;
			}
			switch(control_s_protocolo.substring(5, 8)) {
				case "0000":
					return 1;
				case "0001":
					return 2;
				case "0010":
					return 3;
				case "0011":
					return 4;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//Protocolo desconocido
		return -1;
	}

}
