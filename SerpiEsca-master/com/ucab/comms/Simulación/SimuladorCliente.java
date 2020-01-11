package Simulación;

import controlador.PController;

public class SimuladorCliente implements Runnable{
	
	@Override
	public void run() {
		PController controlador = new PController(false, "COM2", "COM4");
		try {
			controlador.set_identificador("00000010");
			controlador.inicioClienteADDK();
		}
		catch (Exception e){
			System.out.println("Error de inicio de programa");
			e.printStackTrace();
		}
		
	}

}
