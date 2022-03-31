package Fission;


import java.util.ArrayList;
import java.util.Timer;

public class Giocatore {
	
	private static final int MAX= Integer.MAX_VALUE;
	private static final int MIN= Integer.MIN_VALUE;

	private Scacchiera root,copiaRoot;	
	private int colore;
	private int coloreAvversario;
	private boolean tempoFinito;
	private Timer timer = new Timer();
	private ThreadTempoPerMossa th;
	
	/*
	 * -10000 Sconfitta
	 * 	10000 Vittoria
	 * 	 	0 Pareggio
	 * 	  n00 Guadagno di n pedine
	 *   -n00 Perdita  di n pedine						
	 *    n+1 Pedine perse per entrambi quindi pareggio 1 13 1(SIGNIFICA CHE NESSUNO DEI DUE PERDE PEDINE NEL PATH DELLE MOSSE)
	 */
	
	
	public Scacchiera getRoot() {
		return root;		
	}

			
	public Giocatore(Scacchiera scacchiera, int colore) {
		this.root = scacchiera;	
		this.colore = colore;
		this.coloreAvversario= 1 - colore;	
	}
	
	public void stopTimer() {
		timer.cancel();
	}
	
//--------------------------------------------------EURISTICHE-----------------------------------------------------------------------------
	@SuppressWarnings("static-access")
	private int calcolaEuristica(int col) {

		// differenza tra la scacchiera di partenza/intermedia del mio colore e la scacchiera dopo un certo numero di mosse.
		int io = copiaRoot.contaNumeroDiBita1(root.getScacchiere()[col])- copiaRoot.contaNumeroDiBita1(copiaRoot.getScacchiere()[col]);
		
		// differenza tra la scacchiera di partenza/intermedia dell'avversario e la scacchiera dopo un certo numero di mosse.
		int avversario = copiaRoot.contaNumeroDiBita1(root.getScacchiere()[1 - col])- copiaRoot.contaNumeroDiBita1(copiaRoot.getScacchiere()[1 - col]);

		// se questo if è valido significa che sia io che l'avversario abbiamo perso o sono rimaste lo stesso numero di pedine.
		if (io == avversario) {
			return (io+1);//1 a 13 ||| fare (13-io) permette di scegliere mosse che hanno il minor numero di perdite tra io e avversario
		}//1 INDICA CHE NESSUNO NEL PATH DELLE MOSSE PERDONO PEDINE

		// altrimenti ho un vantaggio o svantaggio in termini di pedine moltiplicati per 100
		return 100 * (avversario - io );//100 a 1200(impossibile) oppure -100 a -1200 (impossibile)
						
	}
	

	
	
//-------minMaxAlphabetapruning------------------------------------------minMaxAlphabetapruning----------------------------------minMaxAlphabetapruning
		private int minMaxAlphaBetaPruning(Scacchiera copiaRoot, int currentdepth, int col,int alfa,int beta) {
			
			if(!th.isAlive()) {
				tempoFinito = true ;
				return 0;
			}
					
			int funzioneUtilitaNodoFogliaPerThisColore= copiaRoot.funzioneUtilitaNodoFoglia(this.colore);////(10000VB 0PB -10000SB)
			if (funzioneUtilitaNodoFogliaPerThisColore != 100) {
				
				return funzioneUtilitaNodoFogliaPerThisColore*currentdepth;				
			}
			
			if(currentdepth == 1) {				
				
				return calcolaEuristica(this.colore);			
			}
								
			ArrayList<Mossa> listaMosseValide = copiaRoot.mosseValide(col);
							
			
			int v;
			if(this.colore == col) {
				v = MIN;
				for(Mossa mossa: listaMosseValide) {
					if(!th.isAlive()) {
						tempoFinito = true ;
						return 0;
					}
					copiaRoot.muoviPedinaDo(mossa.getX(), mossa.getY(), mossa.getDir(),col);
					
					currentdepth--;

					int tmp = minMaxAlphaBetaPruning(copiaRoot,currentdepth,1-col,alfa,beta);					
					currentdepth++;
					
					copiaRoot.undo();
					if(tmp > v) {						
						v=tmp;						
					}
					
					if(v > alfa) {
						alfa=v;	
					}
					if(beta <= alfa) break;
					
				}
			}
				
			else {
				v = MAX;
				for(Mossa mossa: listaMosseValide) {
					if(!th.isAlive()) {
						tempoFinito = true ;
						return 0;
					}
					copiaRoot.muoviPedinaDo(mossa.getX(), mossa.getY(), mossa.getDir(), col);
					
					currentdepth--;
					int tmp = minMaxAlphaBetaPruning(copiaRoot,currentdepth,1-col,alfa,beta);				
					currentdepth++;

					copiaRoot.undo();
					
					if(tmp < v) {
						v=tmp;
					}
					if(v < beta) {
						beta=v;	
					}
					if(beta <= alfa) break;
					
				}
								
			} 
			
			return v;
		}
//-------minMaxAlphabetapruning------------------------------------------minMaxAlphabetapruning----------------------------------minMaxAlphabetapruning
		
		
//-------iterativeDeepeningSearch----------------------------------------iterativeDeepeningSearch-------------------------------iterativeDeepeningSearch
	public int iterativeDeepeningSearch(Scacchiera copiaRoot, long tempoPerMossa) {	
		int score = -3000;
		int currentdepth = 3;
		tempoFinito = false;
		
		while(currentdepth<=20) {

			if(!th.isAlive()) {
				break;
			}				
											
			int scoreRicerca = minMaxAlphaBetaPruning(copiaRoot,currentdepth,this.coloreAvversario,MIN,MAX);	
			
			if(tempoFinito) { 
				return score;
			}
			
			
			score = scoreRicerca;	
			currentdepth++;			
		}			
		
		return score;
	}
//-------iterativeDeepeningSearch----------------------------------------iterativeDeepeningSearch-------------------------------iterativeDeepeningSearch---	
//-------elaboraProssimaMossaPotatura------------------------------------elaboraProssimaMossaPotatura-------------------------- elaboraProssimaMossaPotatura
	@SuppressWarnings("static-access")
	public Mossa elaboraProssimaMossaPotatura(){

		int score;
		int maxScore=MIN;
		int valoreMiglioreCasoDiParitaScore=MAX;
		copiaRoot = new Scacchiera(root);
		ArrayList<Mossa> listaMosseValide = copiaRoot.mosseValide(this.colore);	
		Mossa bestMove=null;
		Mossa mossaPerCasoDiParitaScore= null;
		int numeroDiMosse =	listaMosseValide.size();
		long tempoPerMossa = 920/numeroDiMosse;		
					
		
		for(Mossa mossa: listaMosseValide) {

			th=new ThreadTempoPerMossa();
			timer.schedule(th, tempoPerMossa);

			copiaRoot.muoviPedinaDo(mossa.getX(), mossa.getY(), mossa.getDir(),this.colore);
			score= iterativeDeepeningSearch(copiaRoot,tempoPerMossa);
		
			
			/*facciamo questo calcolo per scegliere in caso di score uguale tra le varie mosse,la mossa che ci permette di 
			 spostare nostra pedina in una posizione in cui nel suo intorno ci sono poche pedine del suo colore rispetto al 
			 colore dell'avversario
			 */
			if(score==1) {	

				long mask=copiaRoot.getMaskPerScegliMossa(); // maskera con  tutti 0 tranne 1 nel quadrato con centro in cui arriva
				int numeroPerdineDelMioColoreVicine=copiaRoot.contaNumeroDiBita1(copiaRoot.getScacchiere()[this.colore] & mask);  
				int numeroPerdineDelAvversarioColoreVicine=copiaRoot.contaNumeroDiBita1(copiaRoot.getScacchiere()[1-this.colore] & mask);  
	
				int differenza=numeroPerdineDelMioColoreVicine-numeroPerdineDelAvversarioColoreVicine;
				if(differenza < valoreMiglioreCasoDiParitaScore) {
					valoreMiglioreCasoDiParitaScore = differenza;
					mossaPerCasoDiParitaScore=mossa;
				}
			}
			

			if(score > maxScore) {
				maxScore = score;
				bestMove = mossa;
			}
			
			copiaRoot.undo();		 			 	
		}//FINE FOR SULLE MOSSE VALIDE

		if(maxScore==1) {
			bestMove= mossaPerCasoDiParitaScore;	
		}
		
		bestMove.convertIntToStr();
		return bestMove;
			
	}
//-------elaboraProssimaMossaPotatura------------------------------------elaboraProssimaMossaPotatura-------------------------- elaboraProssimaMossaPotatura	
	
	
	public void muovi(Mossa mossa, int colore) {		
		root.muoviPedina(mossa.getX(),mossa.getY(),mossa.getDir(),colore);	
	}

}
