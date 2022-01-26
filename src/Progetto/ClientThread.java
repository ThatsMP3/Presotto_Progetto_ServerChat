
/*
 * PRESOTTO MATTEO
 * 5C_IA
 * PROGETTI TPSIT
*/

package Progetto;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


// Per la connessione del cliente si richiama questa classe
public class ClientThread extends Thread {
	
  private String NomeClient = "";
  private DataInputStream din = null;
  private PrintStream ps = null;
  private Socket s = null;
  private final ClientThread[] threads;
  private int Clientmax;
  String dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

  public ClientThread(Socket s, ClientThread[] threads) {
	  this.s = s;
	  this.threads = threads;
	  Clientmax = threads.length;
  }

  public void run() {
	  int Clientmax = this.Clientmax;
	  ClientThread[] threads = this.threads;

	  try {
		  /*
		   * Input e Output del Client
		   */
		  din = new DataInputStream(s.getInputStream());
		  ps = new PrintStream(s.getOutputStream());
		  String nome;
		  while (true) {
			  ps.println("Inserisci il nome");
			  nome = din.readLine().trim();
			  if (nome.indexOf('#') == -1) {
				  break;
			  } else {
				  ps.println("Il nome non deve contenere '#'");
			  }
		  }

		  /* Messaggio di Benvenuto al nuovo client */
		  ps.println("Benvenuto " + nome + " nella chat di Presotto!\nPer uscire dalla chat scrivi /quit.");
		  System.out.println(dtf + " <" + nome +"> si è connesso al server");
		  synchronized (this) {
			  for (int i = 0; i < Clientmax; i++) {
				  if (threads[i] != null && threads[i] == this) {
					  NomeClient = "#" + nome;
					  break;
				  }
			  }
			  for (int i = 0; i < Clientmax; i++) {
				  if (threads[i] != null && threads[i] != this) {
					  threads[i].ps.println("---- Un Nuovo Utente " + nome + " è entrato in chat! ----");
				  }
			  }
		  }
		  /* Invio dei messaggi tra gli utenti */
		  while (true) {
			  String line = din.readLine();
			  if (line.startsWith("/quit")) {
				  break;
			  }
			  /* Messaggi privati con il simbolo # */
			  if (line.startsWith("#")) {
				  String[] words = line.split("\\s", 2);
				  if (words.length > 1 && words[1] != null) {
					  words[1] = words[1].trim();
					  if (!words[1].isEmpty()) {
						  synchronized (this) {
							  for (int i = 0; i < Clientmax; i++) {
								  if (threads[i] != null && threads[i] != this && threads[i].NomeClient != null && threads[i].NomeClient.equals(words[0])) {
									  threads[i].ps.println("Privato da <" + nome + "> " + words[1]);
									  words[0] = words[0].replace("#", "");
									  this.ps.println("Privato a <" + words[0] + "> " + words[1]);
									  System.out.println(dtf + "Priv tra <" + nome + "> < " + words[0]+ "> " + words[1] );
									  break;
								  }
							  }
						  }
					  }
				  }
			  } else {
				  /* Messaggio Pubblico */
				  synchronized (this) {
					  for (int i = 0; i < Clientmax; i++) {
						  if (threads[i] != null && threads[i].NomeClient != null) {
							  threads[i].ps.println("<" + nome + "> " + line);
							  System.out.println(dtf + "<" + nome + "> " + line);
						  }
					  }
				  }
			  }
		  }
		  synchronized (this) {
			  for (int i = 0; i < Clientmax; i++) {
				  if (threads[i] != null && threads[i] != this && threads[i].NomeClient != null) {
					  threads[i].ps.println("---- L'utente " + nome + " è uscito dalla chat ----");
				  }
			  }
		  }
      ps.println("---- Arrivederci " + nome + " ----");
      System.out.println(dtf + " <" + nome +"> si è disconnesso dal server");
      /*
       * Bisogna togliere dalla Lista di thread il client appena uscito
       * così che un nuovo client può accedere al server
       */
      	synchronized (this) {
      		for (int i = 0; i < Clientmax; i++) {
      			if (threads[i] == this) {
      				threads[i] = null;
      			}
      		}
      	}
      /*
       * Chiusura dei vari servizi
       */
      din.close();
      ps.close();
      s.close();
    } catch (IOException e) {
    }
  }
}


