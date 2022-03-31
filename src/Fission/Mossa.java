package Fission;

public class Mossa {
   
    private String direzioneStr;
    private String posizioneXStr;
    private String posizioneYStr;
    
    private int x;
	private int y;
	private int dir;
    
	
	public void convertStrToInt() {

		switch (this.posizioneXStr) {
		case "A":
			this.x = 0;
			break;
		case "B":
			this.x = 1;
			break;
		case "C":
			this.x = 2;
			break;
		case "D":
			this.x = 3;
			break;
		case "E":
			this.x = 4;
			break;
		case "F":
			this.x = 5;
			break;
		case "G":
			this.x = 6;
			break;
		case "H":
			this.x = 7;
			break;
		}

		switch (this.direzioneStr) {
		case "N":
			this.dir = 0;
			break;
		case "S":
			this.dir = 1;
			break;
		case "NE":
			this.dir = 2;
			break;
		case "SW":
			this.dir = 3;
			break;
		case "SE":
			this.dir = 4;
			break;
		case "NW":
			this.dir = 5;
			break;
		case "E":
			this.dir = 6;
			break;
		case "W":
			this.dir = 7;
			break;
		}

		this.y = Integer.parseInt(posizioneYStr) - 1;

	}
    
    
	public void convertIntToStr() {

		switch (this.x) {
		case 0:
			this.posizioneXStr = "A";
			break;
		case 1:
			this.posizioneXStr = "B";
			break;
		case 2:
			this.posizioneXStr = "C";
			break;
		case 3:
			this.posizioneXStr = "D";
			break;
		case 4:
			this.posizioneXStr = "E";
			break;
		case 5:
			this.posizioneXStr = "F";
			break;
		case 6:
			this.posizioneXStr = "G";
			break;
		case 7:
			this.posizioneXStr = "H";
			break;
		}

		// { "N", "S", "NE", "SW", "SE", "NW", "E","W" };
		switch (this.dir) {
		case 0:
			this.direzioneStr = "N";
			break;
		case 1:
			this.direzioneStr = "S";
			break;
		case 2:
			this.direzioneStr = "NE";
			break;
		case 3:
			this.direzioneStr = "SW";
			break;
		case 4:
			this.direzioneStr = "SE";
			break;
		case 5:
			this.direzioneStr = "NW";
			break;
		case 6:
			this.direzioneStr = "E";
			break;
		case 7:
			this.direzioneStr = "W";
			break;
		}

		posizioneYStr = (y + 1) + "";

	}
    
    //Client a Server
    public Mossa(int x, int y, int dir) {
    	
    	this.x=x;
    	this.y=y;
    	this.dir=dir;   	
    	//convertIntToStr();   	
    }
    
    //Server a client
    public Mossa(String posizione, String direzione) {
    
    	this.posizioneXStr=posizione.substring(0,1);
    	this.posizioneYStr=posizione.substring(1,2);
        this.direzioneStr = direzione;
        convertStrToInt();
    } 
 
//GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getDir() {
		return dir;
	}

	public String getPosizioneXStr() {
		return posizioneXStr;
	}

	public String getPosizioneYStr() {
		return posizioneYStr;
	}

	public String getDirezioneStr() {
		return direzioneStr;
	}
//GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------GET-------
//SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------
	public void setPosizioneXStr(String posizioneXStr) {
		this.posizioneXStr = posizioneXStr;
	}

	public void setPosizioneYStr(String posizioneYStr) {
		this.posizioneYStr = posizioneYStr;
	}

	public void setDirezioneStr(String direzioneStr) {
		this.direzioneStr = direzioneStr;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}
//SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------SET-------
	@Override
	public String toString() {
		return "MOVE "+posizioneXStr+posizioneYStr+","+direzioneStr;
	}
  
}
