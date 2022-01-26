
/*
 * PRESOTTO MATTEO
 * 5C_IA
 * PROGETTI TPSIT
*/

package Progetto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


// Accesso alla chat tra Client
public class ChatClient {

    /* Accesso alla Chat client  */
    static class ChatAccess extends Observable {
        private Socket s;
        private OutputStream oS;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        /* Crea socket che riceve i thread */
        public void InitSocket(String server, int port) throws IOException {
            s = new Socket(server, port);
            oS = s.getOutputStream();

            Thread riceviThread = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));
                        String line;
                        
                        while ((line = reader.readLine()) != null)
                        	notifyObservers(line);
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            riceviThread.start();
        }


        /* Invia una riga di testo */
        public void send(String text) {
            try {
                oS.write((text + "\r\n").getBytes());
                oS.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        /* Chiusura del socket */
        public void close() {
            try {
                s.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }

    /* Chat client GUI */
    static class ChatFrame extends JFrame implements Observer {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextArea TxtAreaMess;
        private JTextField TxtMess;
        private JButton BtnInvia;
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess) {
            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            initComponents();
        }

        /* Componenti dell'interfaccia grafica*/
        private void initComponents() {
        	TxtAreaMess = new JTextArea(20, 50);
        	TxtAreaMess.setEditable(false);
        	TxtAreaMess.setLineWrap(true);
            add(new JScrollPane(TxtAreaMess), BorderLayout.CENTER);

            Box box = Box.createHorizontalBox();
            add(box, BorderLayout.SOUTH);
            TxtMess = new JTextField();
            BtnInvia = new JButton("Invia");
            box.add(TxtMess);
            box.add(BtnInvia);

            /* Azione che controlla quando si preme invio oppure si clicca il bottone */
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = TxtMess.getText();
                    if (str != null && str.trim().length() > 0)
                        chatAccess.send(str);
                    TxtMess.selectAll();
                    TxtMess.requestFocus();
                    TxtMess.setText("");
                }
            };
            TxtMess.addActionListener(sendListener);
            BtnInvia.addActionListener(sendListener);

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chatAccess.close();
                }
            });
        }

        /* Aggiorna la GUI indipendentemente dall'Object */
        public void update(Observable o, Object arg) {
            final Object Args = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	TxtAreaMess.append(Args.toString());
                	TxtAreaMess.append("\n");
                }
            });
        }
    }

    public static void main(String[] args) {
        String server = args[0];
        int porta = 7979;
        ChatAccess access = new ChatAccess();

        JFrame frame = new ChatFrame(access);
        frame.setTitle("Presotto Chat - Connesso a " + server + ": " + porta);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        try {
            access.InitSocket(server,porta);
        } catch (IOException ex) {
            System.out.println("Impossibile connettersi a " + server + ": " + porta);
            ex.printStackTrace();
            System.exit(0);
        }
    }
}

