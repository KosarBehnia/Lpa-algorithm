import java.awt.Label;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.regex.Matcher;
import java.lang.Math;
import java.net.NetworkInterface;



public class Lpa {
	public static Vector graph =  new Vector<Vector>();
	public int n;
	public double nmi;
	public double timee;
	public ArrayList<Integer> active ;   //for active nodes
	public ArrayList<Integer> passive ;  //for passive nodes
	public ArrayList<Integer> labels; //for labels
	public ArrayList<Integer> labels1;
	public int y = 0;
//reading files
	public Lpa() throws Exception
	{

		 //Vector graph = new Vector<Vector>();
		nmi = 0;
		timee = 0;
		 n = 100000; //number of nodes
		for (int i = 0; i <= n; i++)
		graph.add(new Vector<int[]>());
		BufferedReader br = new BufferedReader(new FileReader("network.txt"));
		String line = br.readLine();
		while (line != null) {
		String[] parts = line.split(" ");
		int source = Integer.parseInt(parts[0]);
		int destination = Integer.parseInt(parts[1]);
		((Vector) (graph.get(source))).add(new int[]{destination,0});
		((Vector) (graph.get(destination))).add(new int[]{source,0});
		line = br.readLine();
		}
		br.close();
		for(int i = 0 ; i < n ; i++)
		{
			for(int j = 0; j <((Vector) (graph.get(i))).size();j++ )
			{
				int b = ((int[])((Vector) (graph.get(i))).get(j))[0];
				int num = 0;
				int index = 0;
				for(int k = 0 ; k < ((Vector) (graph.get(i))).size(); k ++)
				{	
					int f = ((int[])((Vector) (graph.get(i))).get(k))[0];
					for(int h = 0 ; h < ((Vector) (graph.get(b))).size() ; h ++)
					{
						if (((int[])((Vector) (graph.get(b))).get(h))[0] == f)
							num ++;
						if (((int[])((Vector) (graph.get(b))).get(h))[0] == i)
							index = h;
					}
					
				}
				((int[])((Vector) (graph.get(i))).get(j))[1]  = num ;  
				((int[])((Vector) (graph.get(b))).get(index))[1]  = num;
			}
			
		}
		active = new ArrayList();   //initializing
		//passive = new ArrayList();  //initializing
		labels = new ArrayList();
		labels1 = new ArrayList();
		for(int i = 1 ; i <= n ; i++)
		{
			active.add(i);
			labels.add(i); //at first each node has its own label
		}
		update();
	}
	
	public void update() throws Exception
	{ 
		double start = System.currentTimeMillis();
		while(active.size()> 0)
		{		
			
			Random rand = new Random();
			int  index =  rand.nextInt(active.size());
			int m = active.get(index);
			int size1 = ((Vector) (graph.get(active.get(index)))).size();
			ArrayList<Integer> locallabel = new ArrayList();   
			ArrayList<Double> score = new ArrayList();   //for storing scores
			ArrayList<Integer> neiarr = new ArrayList();
	
			// updating 
			for (int i = 0 ; i < size1 ; i++)
			{
				y = y + 1;	
				int r =(((int[])((Vector) (graph.get(active.get(index)))).get(i))[0]); //getting neighbours
				int p = locallabel.indexOf(labels.get(r - 1)); //if it's label is new
				double c = 0;   
				if( p != -1)  //it is not new
				{
					
					double bouns = score.get(p) + 1 +
							c*(((int[])((Vector) (graph.get(active.get(index)))).get(i))[1]) ;//adding score
					score.set(p, bouns);
				}
				if(p == -1) 
				{
					double bouns = 1 +	c*(((int[])((Vector) (graph.get(active.get(index)))).get(i))[1]) ;//adding score
					score.add(bouns);
					locallabel.add(labels.get(r - 1)); 
				
				}
				
			}
			Double maximum = Collections.max(score);
			int maxindex = score.indexOf(maximum);
			for(int i = 0 ; i < locallabel.size(); i ++)
			{
				if(( score.get(i)== maximum )&& (locallabel.get(i)< locallabel.get(maxindex)))
					maxindex = i;
			}
			int  temp = labels.get(active.get(index) - 1);
			labels.set(active.get(index)- 1, locallabel.get(maxindex));
			int newlabel = locallabel.get(maxindex);
			//if(temp != newlabel)
			{
			for(int j = 0 ; j < size1; j++)
			{
				int nei = (((int[])((Vector) (graph.get(active.get(index)))).get(j))[0]);
				neiarr.add(nei);
			}
				
			for(int j = 0 ; j < size1 ; j++)
			{
				//System.out.println(active.get(index));
				int nei = neiarr.get(j);
				int neilabel = labels.get(nei - 1); 
				//if an interior neighbour became an active boundary add it to active list
				//previously passive boundary neighbour became active
				if((neilabel == temp)&& (temp != newlabel))
				{
					int iidex = active.indexOf(nei);
					if(iidex == -1)  
					active.add(nei);
				}
				//remove previously active neighbour
				if((neilabel == newlabel))
				{
					int iddex = active.indexOf(nei);
					if(iddex != -1)
						active.remove(iddex);
						
				}
				
			}
			if(temp == newlabel)
			{
				int l = active.indexOf(m);
				active.remove(l);
				
			}
			}
			
			
		}
		double end = System.currentTimeMillis();
		timee = end - start;
		labels1.add(0);
		for(int i = 1 ; i <= n ; i ++)
			labels1.add(labels.get(i - 1));
		 Vector<Integer> v = new Vector<Integer>();
		 for(int m = 0 ; m<= n ; m++)
			 v.add(labels1.get(m));
		 nmi = NMI(v,"community.txt");
		 //System.out.println(nmi);
	}
	double getnmi()
	{
		return nmi;
	}
	double gettime()
	{
		return timee;
	}
	public static float NMI(Vector<Integer> Prediction, String TrueCommunityPathTXT )throws Exception {
	     Vector<Integer> TrueLabel = new Vector<Integer>();
	     int countGuess = 0, countGold = 0;
	     float NMI = 0, up = 0, down = 0;
	     int n = 0;
	     TrueLabel.add(0);
	     BufferedReader br = new BufferedReader(new FileReader(TrueCommunityPathTXT));
	     String line = br.readLine();
	     while (line != null) {
	         String[] parts = line.split(" ");
	         int node = Integer.parseInt(parts[0]);
	         int label = Integer.parseInt(parts[1]);
	         TrueLabel.add(label);
	         n++;
	         line = br.readLine();
	     }
	     br.close();
	     if (n != Prediction.size() - 1)
	         return -1;
	     Hashtable<Integer, Integer> temp = new Hashtable<Integer, Integer>();
	     int k = 1;
	     for (int i = 1; i <= n; i++) {
	         if (temp.containsKey((Integer) Prediction.get(i)))
	             Prediction.set(i, temp.get(Prediction.get(i)));
	         else {
	             temp.put(Prediction.get(i), k);
	             Prediction.set(i, temp.get(Prediction.get(i)));
	             k++;
	         }
	     }
	     for (int i = 1; i <= n; i++) {
	         if (Prediction.get(i) > countGuess)
	             countGuess = Prediction.get(i);
	         if (TrueLabel.get(i) > countGold)
	             countGold = TrueLabel.get(i);
	     }
	     float NRow[] = new float[countGold];
	     float NCol[] = new float[countGuess];
	     float matrix[][] = new float[countGold][countGuess];
	     for (int i = 0; i < countGold; i++)
	         matrix[i] = new float[countGuess];
	     for (int i = 1; i <= n; i++) {
	         matrix[TrueLabel.get(i) - 1][Prediction.get(i) - 1]++;
	         NRow[TrueLabel.get(i) - 1]++;
	         NCol[Prediction.get(i) - 1]++;
	     }
	     for (int i = 0; i < countGold; i++)
	         if (NRow[i] != 0)
	             down += NRow[i] * Math.log(NRow[i] / (float) (n));
	     for (int i = 0; i < countGuess; i++)
	         if (NCol[i] != 0)
	             down += NCol[i] * Math.log(NCol[i] / (float) (n));
	     for (int i = 0; i < countGold; i++)
	         for (int j = 0; j < countGuess; j++)
	             if (matrix[i][j] != 0)
	                 up += matrix[i][j] * Math.log((matrix[i][j] * (float) (n) / ((NRow[i] * NCol[j]))));
	     up *= (float) -2;
	     return NMI = up / down;
	     
	}

}
 