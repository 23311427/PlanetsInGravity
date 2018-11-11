
public class Barrier 
{
	private int workers;
	private int waiting;
	private int waiting2;
	
	public Barrier(int numberWorkers)
	{
		workers = numberWorkers;
	}
	
	public synchronized void checkBarrier(String thread, Body[] bodies) throws InterruptedException
	{
		waiting += 1;
		if (waiting == workers)
		{
			System.out.println(thread + " is notifying all at the barrier 1");
			notifyAll();
			return;
		}
		
		System.out.println(thread + " waiting");
		wait();
		waiting = 0;
		System.out.println(thread + " done waiting");
		System.out.println();
	}
		
	public synchronized void checkBarrier2(String thread, Body[] bodies) throws InterruptedException
	{
		waiting2 += 1;
		if (waiting2 == workers)
		{
			System.out.println(thread + " is notifying all at the barrier 2");
			StdDraw.clear();
			for (int i = 0; i < bodies.length; i++)
			{
				if (i % 8 == 0) 
		    	{
		    		StdDraw.setPenColor(StdDraw.RED);
		       	}
		    	else if (i % 8 == 1) 
		    	{
		    		StdDraw.setPenColor(StdDraw.ORANGE);
		    	}  
		    	else if (i % 8 == 2) 
		    	{
		    		StdDraw.setPenColor(StdDraw.YELLOW);
		    	}
		    	else if (i % 8 == 3) 
		    	{
		    		StdDraw.setPenColor(StdDraw.GREEN);
		    	}
		    	else if (i % 8 == 4)
		    	{
		    		StdDraw.setPenColor(StdDraw.BLUE);
		    	}
		    	else if (i % 8 == 5)
		    	{
		    		StdDraw.setPenColor(StdDraw.MAGENTA);
		    	}
		    	else if (i % 8 == 6)
		    	{
		    		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
		    	}
		    	else
		    	{
		    		if (i % 8 == 7)
		    		{
		        		StdDraw.setPenColor(StdDraw.BLACK);
		    		}
		    	}
		    	StdDraw.filledCircle(bodies[i].x, bodies[i].y, 3);
		    	System.out.println("barrier drawing circles at (" + bodies[i].x + ", " + bodies[i].y + ")");
			}
			notifyAll();
			return;
		}
		
		System.out.println(thread + " waiting");
		
		wait();
		waiting2 = 0;
		System.out.println(thread + " done waiting");
		System.out.println();
	}
}
