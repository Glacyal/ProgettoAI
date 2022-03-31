package Fission;


import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class ThreadTempoPerMossa extends TimerTask  {
	
	   private AtomicBoolean continua = new AtomicBoolean(true);
		
	   public void run() {
		   continua.set(false);
	   }
	  

	   public boolean isAlive() {
	        return this.continua.get();
	    }	
	   
}