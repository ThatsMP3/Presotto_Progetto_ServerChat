
/*
 * PRESOTTO MATTEO
 * 5C_IA
 * PROGETTI TPSIT
*/

package Progetto;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


// Server Chat
public class ChatServer {

	// Massimo di Client che si possono connettere sono 10
	private static final int Clientmax = 10;
	private static final ClientThread[] threads = new ClientThread[Clientmax];

	public static void main(String args[]) {

	  
		// Numero porta
		int porta = 7979;
		// Welcoming Socket
		ServerSocket ws = null;
		// Client socket
		Socket s = null;
		if (args.length < 1) {
			System.out.println("Utilizzo della classe ChatServer \n" + "Porta utilizzata = " + porta);
		} else {
			porta = Integer.valueOf(args[0]).intValue();
		}

		/*
		 * Si apre il server con il numero porta 7979
		 */
		try {
			ws = new ServerSocket(porta);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Crea un client socket per ogni connessione richiesta e lo passa al ClientThread
		 */
		while (true) {
			try {
				s = ws.accept();
				int i = 0;
				for (i = 0; i < Clientmax; i++) {
					if (threads[i] == null) {
						(threads[i] = new ClientThread(s, threads)).start();
						break;
					}
				}
				if (i == Clientmax) {
					PrintStream ps = new PrintStream(s.getOutputStream());
					ps.println("Server pieno! Riprova più tardi...");
					ps.close();
					s.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}  
}

