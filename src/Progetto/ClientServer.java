
/*
 * PRESOTTO MATTEO
 * 5C_IA
 * PROGETTI TPSIT
*/

package Progetto;


import javax.swing.*;

// Questa classe server per capire chi fa l'accesso : Client o Server
public class ClientServer {

	
	public static void main(String [] args){
		
		Object[] scelta = { "Server","Client"};
		String sceltainiziale = "Server";
		
		Object select = JOptionPane.showInputDialog(null, "Entra come : ", "Presotto Chat", JOptionPane.QUESTION_MESSAGE, null, scelta, sceltainiziale);
		// Se entro col Server si accede alla chat Server
		if(select.equals("Server")){
            String[] arg = new String[] {};
			new ChatServer().main(arg);
		}
		// Se entro col client, richiede l'IP (localhost) e accede alla chat Client
		else if(select.equals("Client")){
			String IPServer = JOptionPane.showInputDialog("Entra l'IP del server");
            String[] arg = new String[] {IPServer};
			new ChatClient().main(arg);
		}
	}

}


