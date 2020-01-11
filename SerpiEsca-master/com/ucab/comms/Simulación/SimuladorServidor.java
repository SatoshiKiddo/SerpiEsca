package Simulación;

import controlador.PController;

public class SimuladorServidor implements Runnable {

	@Override
	public void run() {
		PController controlador = new PController(true, "COM1", "COM3");
		try {
			controlador.set_identificador("00000001");
			controlador.inicioServidorADD();
		}
		catch (Exception e){
			System.out.println("Error de inicio de programa");
			e.printStackTrace();
		}
		
	}

}
