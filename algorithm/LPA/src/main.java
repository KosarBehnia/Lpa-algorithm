import java.io.IOException;


public class main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		double avnmi = 0;
		double avtime = 0;   
		for(int i = 0 ; i < 100  ; i++)
		{
		Lpa l = new Lpa();
		avnmi = avnmi + l.getnmi();
		avtime = avtime + l.gettime();
		}
		avnmi = avnmi/100;
		avtime = avtime/100;
		System.out.println("avnmi is"+ avnmi+"avtime is"+ avtime/1000);
		
		
		

	}

}
