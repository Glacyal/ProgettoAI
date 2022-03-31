package Fission;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Random;


public class Client {
	int into=0;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Giocatore giocatore;
	private int col;
	private LinkedList<Mossa> cacheMosseIniziali=new LinkedList<>();
			
	
	public Client(String serverIP, int serverPort) throws Exception {
		
		socket = new Socket(serverIP, serverPort);
		in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out= new PrintWriter(socket.getOutputStream(), true);
								
	}//Costruttore
	
	
	
	public void gioca() throws Exception {
		String colore;
		String risposta;
		Scacchiera scacchiera = new Scacchiera(); 
		
		
		try {
			
			risposta = in.readLine();
			if (risposta.startsWith("WELCOME")) {
				
				colore = risposta.substring(8);
				colore = colore.toUpperCase();
				
				
				
				if (colore.equals("WHITE")) {
					col=0;
					giocatore = new Giocatore(scacchiera, col);
					System.out.println("WELCOME WHITE");
					cacheMosseIniziali.add(new Mossa("C5","NE"));
					cacheMosseIniziali.add(new Mossa("D6","NE"));
					cacheMosseIniziali.add(new Mossa("E3","SW"));
					cacheMosseIniziali.add(new Mossa("F4","SW"));
					
					
					// per la prima mossa da effettuare
					while(!risposta.startsWith("YOUR_TURN"))
						risposta = in.readLine();
					
					
					if (risposta.startsWith("YOUR_TURN")) {
						Mossa m=cacheMosseIniziali.get(new Random().nextInt(cacheMosseIniziali.size()-1));
						out.println(m);
						giocatore.muovi(m,col);
					}
						
					
				} else {
					col=1;
					giocatore = new Giocatore(scacchiera,col);
					System.out.println("WELCOME BLACK");
					cacheMosseIniziali.add(new Mossa("C4","NW"));
					cacheMosseIniziali.add(new Mossa("D3","NW"));
					cacheMosseIniziali.add(new Mossa("E6","SE"));
					cacheMosseIniziali.add(new Mossa("F5","SE"));
					
					
					
					
					// per la prima mossa da effettuare
					while(!risposta.startsWith("OPPONENT_MOVE"))
						risposta = in.readLine();
					
					
					
					if (risposta.startsWith("OPPONENT_MOVE")) {
						
						System.out.println("Mossa dell'avversario: " + risposta.substring(14));//OPPONENT_MOVE B4,N
						String[] campiRisposta = risposta.substring(14).split(","); //OPPONENT_MOVE B4,N
						Mossa m = new Mossa(campiRisposta[0],campiRisposta[1]);
						//CASI SPECIALI
						
						
						if(campiRisposta[0].equals("B4") && campiRisposta[1].equals("W")) {
							cacheMosseIniziali.clear();
							cacheMosseIniziali.add(new Mossa("D3","NW"));
						}else if(campiRisposta[0].equals("G5") && campiRisposta[1].equals("E")) {
							cacheMosseIniziali.clear();
							cacheMosseIniziali.add(new Mossa("E6","SE"));
						}
						else if(campiRisposta[0].equals("E7") && campiRisposta[1].equals("S")) {
							cacheMosseIniziali.clear();
							cacheMosseIniziali.add(new Mossa("F5","SE"));
						}
						else if(campiRisposta[0].equals("D2") && campiRisposta[1].equals("N")) {
							cacheMosseIniziali.clear();
							cacheMosseIniziali.add(new Mossa("C4","NW"));
						}

						
						//CASI INIZIALI 
						else if(campiRisposta[0].equals("B4") || campiRisposta[0].equals("C5")) {
							cacheMosseIniziali.removeFirst();// "C4","NW"
						}else if(campiRisposta[0].equals("C3")) {
							cacheMosseIniziali.removeFirst();// "C4","NW"
							cacheMosseIniziali.removeFirst();// "D3","NW"
						}else if(campiRisposta[0].equals("D2") || campiRisposta[0].equals("E3")) {
							cacheMosseIniziali.remove(1); // "D3","NW"
						}else if(campiRisposta[0].equals("F4") || campiRisposta[0].equals("G5")) {
							cacheMosseIniziali.remove(3);//"F5","SE"
						}else if(campiRisposta[0].equals("F6")) {
							cacheMosseIniziali.remove(3);//"F5","SE"
							cacheMosseIniziali.remove(2);//"E6","SE"
						}else if(campiRisposta[0].equals("D6") || campiRisposta[0].equals("E7")) {
							cacheMosseIniziali.remove(2);//"E6","SE"
						}
						
						giocatore.muovi(m,1-col);
								
					}

					risposta = in.readLine();
					if (risposta.startsWith("YOUR_TURN")) {
						Mossa m=cacheMosseIniziali.get(new Random().nextInt(cacheMosseIniziali.size()-1));
						out.println(m);
						giocatore.muovi(m,col);
					}
					
					 
				}
			}
			
			

			cacheMosseIniziali.clear();
			
			while (true) {
		
				risposta = in.readLine();
				//-------------------------------------------------------------
				if (risposta.startsWith("YOUR_TURN")) {

					Mossa mossa = giocatore.elaboraProssimaMossaPotatura();
					
					out.println(mossa);												
					giocatore.muovi(mossa,col);
					
				} 
				
				//-------------------------------------------------------------
				else if (risposta.startsWith("VALID_MOVE"))
					System.out.println("Mossa valida, attendi...");
				
				//-------------------------------------------------------------
				else if (risposta.startsWith("ILLEGAL_MOVE")) {
					System.out.println("Hai effettuato una mossa non consentita");
					break;
				}
				
				//-------------------------------------------------------------
				else if (risposta.startsWith("OPPONENT_MOVE")) {
					System.out.println("Mossa dell'avversario: " + risposta.substring(14));//OPPONENT_MOVE B4,N
					String[] campiRisposta = risposta.substring(14).split(","); //OPPONENT_MOVE B4,N
				
					Mossa m = new Mossa(campiRisposta[0],campiRisposta[1]);
					
					giocatore.muovi(m,1-col);
					
					
				} 
				
				//-------------------------------------------------------------
				else if (risposta.startsWith("VICTORY")) {
					System.out.println("HAI VINTO");
					break;
				} else if (risposta.startsWith("TIMEOUT")) {
					System.out.println("TIMEOUT");
					break;
				} else if (risposta.startsWith("DEFEAT")) {
					System.out.println("HAI PERSO");
					break;
				} else if (risposta.startsWith("TIE")) {
					System.out.println("HAI PAREGGIATO");
					break;
				} else if (risposta.startsWith("MESSAGE"))
					System.out.println(risposta.substring(8));
			}

		} finally {
			socket.close();
			giocatore.stopTimer();
		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		String serverIP="localhost";
		//String serverIP="160.97.28.146";
		int serverPort=8901;
		
		if(args.length==2) {
			
			serverIP=args[0];
			serverPort=Integer.valueOf(args[1]);
			
		}
	
		Client p = new Client(serverIP, serverPort);		
		p.gioca();
		
	} 					

}
