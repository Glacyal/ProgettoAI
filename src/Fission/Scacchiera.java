
package Fission;

import java.util.ArrayList;
import java.util.LinkedList;

public class Scacchiera {

	// 0 B - 1 N - 2 TOTALE
	private long[] scacchiere;
	//long salvati a 2 a 2 per maschera bianca e maschera nera
	private LinkedList<Long> vecchieConfigurazioni = new LinkedList<Long>();
	private long maskPerScegliMossa = 0L;

	public Scacchiera() {
		
		scacchiere = new long[3];
		scacchiere[0] = 0b0000000000010000001010000101010000101010000101000000100000000000L;
		scacchiere[1] = 0b0000000000001000000101000010101001010100001010000001000000000000L;
		scacchiere[2] = (scacchiere[0] | scacchiere[1]);
	}
	
	public Scacchiera(Scacchiera s) {

		this.scacchiere = new long[3];
		this.scacchiere[0] = s.scacchiere[0];
		this.scacchiere[1] = s.scacchiere[1];
		this.scacchiere[2] = s.scacchiere[2];
	}
	

	public static String stampaBitMap(long n) {
		String lettera [] = { "A|", "B|", "C|", "D|", "E|", "F|", "G|","H|"," " };
		long mask = 1L << 63;
		int count = 0;
		int i=0;
		String ret="";
		
		ret=ret+"--12345678\n";
		ret=ret+"----------\n";
		ret=ret+lettera[i++];
		while(mask != 0) {
	
			if((mask&n) == 0)
				ret=ret+"*";
			else
				ret=ret+"1";
			
			count++;
			if(count%8 == 0) {
				ret=ret+"\n";
				ret=ret+lettera[i++];
			}
			
			
			mask = mask >>> 1;
		}
		
		ret+="\n";
		return ret;
		
	}

	
	public void stampaScacchiereNew() {
		String lettera [] = { "A|", "B|", "C|", "D|", "E|", "F|", "G|","H|", " "};
		long mask0 = 1L << 63;
		long mask1 = 1L << 63;
		long mask2 = 1L << 63;
		int count = 0;
		int i=0;
		System.out.println("V|12345678         B|12345678         N|12345678");
		System.out.println("----------         ----------         ----------");
		System.out.print(lettera[i]);
		while(mask1 != 0) {
			
			if(count<=7) {
			if((mask2&scacchiere[2]) == 0)
				System.out.print("*");
			else
				System.out.print("1");
				mask2 = mask2 >>> 1;
			}
			if(count>=8&&count<=15) {
				if((mask0&scacchiere[0]) == 0)
					System.out.print("*");
				else
					System.out.print("1");
				mask0 = mask0 >>> 1;	
			}
			if(count>=16) {
				if((mask1&scacchiere[1]) == 0)
					System.out.print("*");
				else
					System.out.print("1");
			mask1 = mask1 >>> 1;	
			}
			count++;
			if(count%8 == 0 && count%24 != 0) {System.out.print("         "+lettera[i]);}	
			else if(count%24 == 0) {
				count=0;
				System.out.println();
				System.out.print(lettera[++i]);
			}
			
			
		}
		
		System.out.println();
	}
	
	public void muoviPedina(int x,int y, int cordinate,int colore) {
		//"N=0", "S=1", "NE=2", "SW=3", "SE=4", "NW=5", "E=6","W=7"
		//bianchi = 0 noi
		//neri    = 1 opponent
		
		long maskBase=0b1000000000000000000000000000000000000000000000000000000000000000L;
		int posCorr=((x*8)+y);
		int posDestinazione=posCorr;
		long maskAggiungiPedinaNuova;
		long maskEliminaPedinaCorr;
		long maskEliminazioneNPedine = 0L; 
		long maskControlloMossa;
		boolean MossaValida=true;
		boolean SonoSbattuto=false;

		if(((maskBase>>>posCorr) & this.scacchiere[colore]) == 0) {System.out.println("PEDINA INESISTENTE");return;}
		switch(cordinate) {
			
			case (0)://NORD
				
				if(x<=0){MossaValida=false;break;}//Mossa non valida perche sono gia al bordo NORD
				maskControlloMossa=(maskBase>>>posCorr<<8);		
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x--;
			    	while(x>0) {
			    		maskControlloMossa=maskControlloMossa<<8;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE

			    			if(y+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
			    			}
			    			if(y-1>=0) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD															
							break;	
			    		}//Sbatto
			    	x--;		
			    	}//WHILE
	
			    }
				posDestinazione=y;
				break;
				
			case (1)://SUD
					
				if(x>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo SUD
				maskControlloMossa=(maskBase>>>posCorr>>>8);
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			  
			    else {//MOSSA VALIDA
			    	x++;
			    	while(x<7) {
			    		maskControlloMossa=maskControlloMossa>>>8;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE

			    			if(y+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
			    			
			    			}
			    			if(y-1>=0) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}	
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD															
							break;	
			    		}//Sbatto
			    	x++;		
			    	}//WHILE
	
			    }
				posDestinazione=(56)+y;
				break;
				
			case (2)://NORD-EST
				
				if(x<=0 || y>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo NORD o EST
				maskControlloMossa=(maskBase>>>posCorr<<7);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x--; y++;
			    	posDestinazione-=7;
			    	while(x>0 && y<7) {
			    		maskControlloMossa=maskControlloMossa<<7;
			    		
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>posDestinazione;// POSIZIONE CENTRALE  maskBase>>>((x*8)+y);
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x--;y++;
			    	posDestinazione-=7;
			    	}//WHILE
	
			    }
				break;
				
			case (3)://SUD-OVEST
				
				if(x>=7 || y<=0){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo SUD o OVEST
				maskControlloMossa=(maskBase>>>posCorr>>>7);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x++; y--;
			    	posDestinazione+=7;
			    	while(x<7 && y>0) {
			    		maskControlloMossa=maskControlloMossa>>>7;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>posDestinazione;// POSIZIONE CENTRALE  maskBase>>>((x*8)+y);
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x++;y--;
			    	posDestinazione+=7;
			    	}//WHILE
	
			    }
				break;
				
			case (4)://SUD-EST
				
				if(x>=7 || y>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo SUD o EST
				maskControlloMossa=(maskBase>>>posCorr>>>9);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x++; y++;
			    	posDestinazione+=9;
			    	while(x<7 && y<7) {
			    		maskControlloMossa=maskControlloMossa>>>9;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>posDestinazione;// POSIZIONE CENTRALE  maskBase>>>((x*8)+y);
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x++;y++;
			    	posDestinazione+=9;
			    	}//WHILE
	
			    }	
				break;
				
			case (5)://NORD-OVEST
				
				if(x<=0 || y<=0){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo NORD o OVEST
				maskControlloMossa=(maskBase>>>posCorr<<9);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x--; y--;
			    	posDestinazione-=9;
			    	while(x>0 && y>0) {
			    		maskControlloMossa=maskControlloMossa<<9;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>posDestinazione;// POSIZIONE CENTRALE  maskBase>>>((x*8)+y);
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x--;y--;
			    	posDestinazione-=9;
			    	}//WHILE
	
			    }					
				break;
				
			case (6)://EST
				
				if(y>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo EST
				maskControlloMossa=(maskBase>>>posCorr>>>1);		
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	y++;
			    	while(y<7) {
			    		maskControlloMossa=maskControlloMossa>>>1;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE
			    			
			    			if(x-1>=0) {			    				
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    				
			    			}
			    			if(x+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST																
							break;	
			    		}//Sbatto
			    	y++;		
			    	}//WHILE
	
			    }
				posDestinazione=(8*x)+7;
				break;
				
			case (7)://OVEST
				
				if(y<=0){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo OVEST
				maskControlloMossa=(maskBase>>>posCorr<<1);		
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	y--;
			    	while(y>0) {
			    		maskControlloMossa=maskControlloMossa<<1;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE
			    			
			    			if(x-1>=0) {			    				
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    				
			    			}
			    			if(x+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST																
							break;	
			    		}//Sbatto
			    	y--;		
			    	}//WHILE
	
			    }
			    
				posDestinazione=(8*x);
				break;
		}
		
		if(MossaValida) {
			maskEliminaPedinaCorr=(maskBase>>>posCorr);
			
				if(SonoSbattuto) {
					this.scacchiere[colore] = (~maskEliminaPedinaCorr) & this.scacchiere[colore];//ELIMINO LA PEDINA DELLA POSIZIONE DI PARTENZA
					this.scacchiere[colore] = (~maskEliminazioneNPedine) & this.scacchiere[colore];//ELIMINO LE PEDINE DI CONTORNO ALLO SCONTRO
					this.scacchiere[1-colore] = (~maskEliminazioneNPedine) & this.scacchiere[1-colore];//ELIMINO LE PEDINE DI CONTORNO ALLO SCONTRO
				}
				else { 
					//ELIMINO LA PEDINA NELLA POSIZIONE DI PARTENZA & AGGIUNGO LA PEDINA ALLA SCACCHIERA DI PARTENZA CON POSIZIONE DESTINAZIONE
					maskAggiungiPedinaNuova=maskBase>>>posDestinazione;
					this.scacchiere[colore]= (~maskEliminaPedinaCorr) & (maskAggiungiPedinaNuova | this.scacchiere[colore]);					
				}
		
		//Aggiorno totale		
		this.scacchiere[2]= (this.scacchiere[0] | this.scacchiere[1]);
			//System.out.println("MOSSA EFFETTUATA!");
		}
		
		else {
			//System.out.println("MOSSA NON EFFETTUATA!");
			}

	}
	

//----------------MUOVIPEDINADO----------------MUOVIPEDINADO----------------MUOVIPEDINADO----------------MUOVIPEDINADO----------------MUOVIPEDINADO
	public boolean muoviPedinaDo(int x,int y, int cordinate,int colore) {
		//"N=0", "S=1", "NE=2", "SW=3", "SE=4", "NW=5", "E=6","W=7"
		
		long maskBase=0b1000000000000000000000000000000000000000000000000000000000000000L;
		int posCorr=((x*8)+y);
		int posDestinazione=posCorr;
		long maskAggiungiPedinaNuova;
		long maskEliminaPedinaCorr;
		long maskEliminazioneNPedine = 0L; 
		long maskControlloMossa;
		boolean MossaValida=true;
		boolean SonoSbattuto=false;

		
		
		if(((maskBase>>>posCorr) & this.scacchiere[colore]) == 0) {System.out.println("PEDINA INESISTENTE"); return false;}
	
		
		switch(cordinate) {
			
			case (0)://NORD
				
				if(x<=0){MossaValida=false;break;}//Mossa non valida perchè sono già al bordo NORD
				maskControlloMossa=(maskBase>>>posCorr<<8);		
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	
					
			    	x--;
			    	while(x>0) {
			    		maskControlloMossa=maskControlloMossa<<8;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE

			    			if(y+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
			    			}
			    			if(y-1>=0) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD															
							break;	
			    		}//Sbatto
			    		
			   
			    	x--;		
			    	}//WHILE
	
			    }
				posDestinazione=y;
				break;
				
			case (1)://SUD
					
				if(x>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo SUD
				maskControlloMossa=(maskBase>>>posCorr>>>8);
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			  
			    else {//MOSSA VALIDA
			    	x++;
			    	while(x<7) {
			    		maskControlloMossa=maskControlloMossa>>>8;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE

			    			if(y+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
			    			
			    			}
			    			if(y-1>=0) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}	
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD															
							break;	
			    		}//Sbatto
			    	x++;		
			    	}//WHILE
	
			    }		
				//(8*7)+y;
				posDestinazione=(56)+y;
				break;
				
			case (2)://NORD-EST ok
				
				if(x<=0 || y>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo NORD o EST
				maskControlloMossa=(maskBase>>>posCorr<<7);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x--; y++;
			    	posDestinazione-=7;
			    	while(x>0 && y<7) {
			    		maskControlloMossa=maskControlloMossa<<7;
			    		
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine = maskBase>>>posDestinazione;// POSIZIONE CENTRALE maskBase>>>((x*8)+y)
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x--;y++;
			    	posDestinazione-=7;
			    	}//WHILE
	
			    }
				break;
				
			case (3)://SUD-OVEST ok
				
				if(x>=7 || y<=0){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo SUD o OVEST
				maskControlloMossa=(maskBase>>>posCorr>>>7);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x++; y--;
			    	posDestinazione+=7;
			    	while(x<7 && y>0) {
			    		maskControlloMossa=maskControlloMossa>>>7;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>posDestinazione;// POSIZIONE CENTRALE maskBase>>>((x*8)+y);
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x++;y--;
			    	posDestinazione+=7;
			    	}//WHILE
	
			    }
				break;
				
			case (4)://SUD-EST ok
				
				if(x>=7 || y>=7){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo SUD o EST
				maskControlloMossa=(maskBase>>>posCorr>>>9);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x++; y++;
			    	posDestinazione+=9;
			    	while(x<7 && y<7) {
			    		maskControlloMossa=maskControlloMossa>>>9;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine = maskBase>>>posDestinazione;// POSIZIONE CENTRALE maskBase>>>((x*8)+y);
			    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x++;y++;	
			    	posDestinazione+=9;
			    	}//WHILE
	
			    }
				break;
				
			case (5)://NORD-OVEST
				
				if(x<=0 || y<=0){MossaValida=false;break;}//Mossa non valida perche sono gia  al bordo NORD o OVEST
				maskControlloMossa=(maskBase>>>posCorr<<9);	
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	x--; y--;
			    	posDestinazione-=9;
			    	while(x>0 && y>0) {
			    		maskControlloMossa=maskControlloMossa<<9;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;
			    			
			    			maskEliminazioneNPedine = maskBase>>>posDestinazione;// POSIZIONE CENTRALE maskBase>>>((x*8)+y);
			    						    			
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
								
																							
							break;	
			    		}//Sbatto
			    	x--;y--;
			    	posDestinazione-=9;
			    	}//WHILE
	
			    }
				break;
				
			case (6)://EST
				
				if(y>=7){MossaValida=false;break;}//MOSSA NON VALIDA SONO GIA AL BORDO EST
				maskControlloMossa=(maskBase>>>posCorr>>>1);		
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//Mossa non valida perche una pedina ci ferma
			    else {//Mossa valida
			    	y++;
			    	while(y<7) {
			    		maskControlloMossa=maskControlloMossa>>>1;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);// POSIZIONE CENTRALE
			    			
			    			if(x-1>=0) {			    				
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    				
			    			}
			    			if(x+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y+1);    // POSIZIONE EST																
							break;	
			    		}//Sbatto
			    	y++;		
			    	}//WHILE
	
			    }
			    
				posDestinazione=(8*x)+7;
				break;
				
			case (7)://OVEST
				
				if(y<=0){MossaValida=false;break;}//MOSSA NON VALIDA SONO GIA AL BORDO OVEST
				maskControlloMossa=(maskBase>>>posCorr<<1);		
			    if((maskControlloMossa & (this.scacchiere[2])) != 0) {MossaValida=false;break;}//MOSSA NON VALIDA UNA PEDINA CI FERMA
			    else {//MOSSA VALIDA
			    	y--;
			    	while(y>0) {
			    		maskControlloMossa=maskControlloMossa<<1;
			    		if((maskControlloMossa & (this.scacchiere[2])) != 0) {//CONTROLLO SE SBATTO
			    			SonoSbattuto=true;

			    			maskEliminazioneNPedine =  maskBase>>>((x*8)+y);//POSIZIONE CENTRALE
			    			
			    			if(x-1>=0) {			    				
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
			    			    maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
			    				
			    			}
			    			if(x+1<=7) {
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
								maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
			    			}
			    				maskEliminazioneNPedine = maskEliminazioneNPedine | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST																
							break;	
			    		}//Sbatto
			    	y--;		
			    	}//WHILE
	
			    }
			    
				posDestinazione=(8*x);
				break;
		}
		
		if(MossaValida) {

			vecchieConfigurazioni.addFirst(this.scacchiere[0]);
			vecchieConfigurazioni.addFirst(this.scacchiere[1]);
			maskEliminaPedinaCorr=(maskBase>>>posCorr);
			
				if(SonoSbattuto) {				
					
					this.scacchiere[colore] = (~maskEliminaPedinaCorr) & this.scacchiere[colore];//ELIMINO LA PEDINA DELLA POSIZIONE DI PARTENZA
					this.scacchiere[colore] = (~maskEliminazioneNPedine) & this.scacchiere[colore];//ELIMINO LE PEDINE DI CONTORNO ALLO SCONTRO
					this.scacchiere[1-colore] = (~maskEliminazioneNPedine) & this.scacchiere[1-colore];//ELIMINO LE PEDINE DI CONTORNO ALLO SCONTRO					
				
				}
				else { 
					
					maskAggiungiPedinaNuova = maskBase>>>posDestinazione;// POSIZIONE CENTRALE	
					//ELIMINO LA PEDINA NELLA POSIZIONE DI PARTENZA & AGGIUNGO LA PEDINA ALLA SCACCHIERA DI PARTENZA CON POSIZIONE DESTINAZIONE					   
					this.scacchiere[colore]= (~maskEliminaPedinaCorr) & (maskAggiungiPedinaNuova | this.scacchiere[colore]);
					
					maskPerScegliMossa =  maskAggiungiPedinaNuova; 				
					if(x==0 && y==0) {
						
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
	    			
					}else if(x==0 && y==7) {
					
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
						
					}else if(x==7 && y==0) {
						maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
	    	
					}else if(x==7 && y==7 ) {
						
						maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
		    			
						
					}else if(y==7) {
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
	    				
	    				
	    			}else if(y==0) {
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
	

	    			}else if(x==0) {			    				
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y);  // POSIZIONE SUD	
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y+1);// POSIZIONE SUD-EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x+1)*8)+y-1);// POSIZIONE SUD-OVEST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
	    				
	    				
	    			}else if(x==7) {
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y);  // POSIZIONE NORD
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y+1);// POSIZIONE NORD-EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>(((x-1)*8)+y-1);// POSIZIONE NORD-OVEST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y+1);    // POSIZIONE EST
	    				maskPerScegliMossa = maskPerScegliMossa | maskBase>>>((x*8)+y-1);    // POSIZIONE OVEST
	    			}
												
				}
		
		//AGGIORNA TOTALE		
		this.scacchiere[2]= (this.scacchiere[0] | this.scacchiere[1]);
		//MOSSA EFFETTUATA
		return true;
		}
		
		else {
		//MOSSA NON EFFETTUATA!
		return false;
		}
	}
//----------------MUOVIPEDINADO----------------MUOVIPEDINADO----------------MUOVIPEDINADO----------------MUOVIPEDINADO----------------MUOVIPEDINADO
//----------------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO
	public void undo() {
		// System.out.println(vecchieConfigurazioni.size());
		this.scacchiere[1] = vecchieConfigurazioni.removeFirst();
		this.scacchiere[0] = vecchieConfigurazioni.removeFirst();
		this.scacchiere[2] = (scacchiere[0] | scacchiere[1]);
	}
//----------------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO--------UNDO DI MUOVIPEDINADO	
//----------------CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE	
	public ArrayList<Mossa> mosseValide(int col) {
		ArrayList<Mossa> listaMosse = new ArrayList<Mossa>();
		long mask = 1L << 63;
		for (int i = 0; i < 64; i++) {
			if ((mask & getScacchiere()[col]) != 0) {

				for (int j = 0; j < 8; j++) {

					if (muoviPedinaDo(i / 8, i % 8, j, col)) {
						listaMosse.add(new Mossa(i / 8, i % 8, j));
						undo();
					}
				}
			}
			mask = mask >>> 1;
		}
		return listaMosse;
	}
//----------------CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE----CALCOLARE LE MOSSE VALIDE		
	
	
	public static int contaNumeroDiBita1(long i) {
		i = i - ((i >> 1) & 0x5555555555555555L);
		i = (i & 0x3333333333333333L) + ((i >> 2) & 0x3333333333333333L);
		return (int) ((((i + (i >> 4)) & 0xF0F0F0F0F0F0F0FL) * 0x101010101010101L) >> 56);
	}
	
	
	public int funzioneUtilitaNodoFoglia(int colore) {
		int noi  = contaNumeroDiBita1(scacchiere[colore]);
		int avversario = contaNumeroDiBita1(scacchiere[1-colore]);
		if ((noi == 1 && avversario == 1) || (noi == 0 && avversario == 0))
			return 0;					// PAREGGIO
		if (noi >= 1 && avversario == 0)
			return 10000; 				// VITTORIA
		if (noi == 0 && avversario >= 1)
			return -10000; 				// SCONFITTA
		return 100;// PARTITA IN CORSO
	}
		
//GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------	
	public long[] getScacchiere() {
		return scacchiere;
	}
	
	public LinkedList<Long> getvecchieConfigurazioni() {
		return vecchieConfigurazioni;
	}
	
	public long getMaskPerScegliMossa() {
		return maskPerScegliMossa;
	}
//GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------	
//SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------
	public void setScacchiere(long bianchi, long neri) {
		this.scacchiere[0]=bianchi;
		this.scacchiere[1]=neri;
		this.scacchiere[2]=(scacchiere[0] | scacchiere[1]);
	}
	
	public void setvecchieConfigurazioni(LinkedList<Long> vecchieConfigurazioni) {
		this.vecchieConfigurazioni = vecchieConfigurazioni;
	}
	
	public void setMaskPerScegliMossa(long maskPerScegliMossa) {
		this.maskPerScegliMossa = maskPerScegliMossa;
	}
//SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------	
	@Override
	public String toString() {
		this.stampaScacchiereNew();
		return "";
	}
}
