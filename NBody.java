// Tony Fu
// Class: NBody
// Professor: Patrick Homer
// Programming Project 1: NBody problem.

//$ javac -cp ".:./jars/common.jar" helloworld.java 
//$ java -cp ".:./jars/common.jar" helloworld

import java.util.Random;

public class NBody
{
	public static final double gravity = 0.0000000000667408;
	public static final double DT = 0.5;
	
	public static void main(String[] args)
	{
		if (args.length < 4)
		{
			System.err.println("Not enough arguments.");
			return;
		}
		int numWorkers = Integer.parseInt(args[0]);
		if (numWorkers > 32 || numWorkers < 0)
		{
			System.err.println("Wrong amount of workers.");
		}
		int numBodies = Integer.parseInt(args[1]);
		int sizeOfBodies = Integer.parseInt(args[2]);
		int timeSteps = Integer.parseInt(args[3]);

		Body[] allBodies = new Body[numBodies];
		CalcForces[] calculators = new CalcForces[numWorkers];
		Barrier barrier1 = new Barrier(numWorkers);
		Barrier barrier2 = new Barrier(numWorkers);
		StdDraw.setScale( -100, 100 );
        StdDraw.clear();
        Random random = new Random();
        
        
        for (int i = 0; i < numBodies; i++)
		{
        	int xVelocity = 0;
        	int yVelocity = 0;
        	// velocities go from slow to fast. SPECIAL CASE blacks don't move
        	if (i % 8 == 0) 
        	{
        		StdDraw.setPenColor(StdDraw.RED);
        		xVelocity = yVelocity = 1;
        	}
        	else if (i % 8 == 1) 
        	{
        		StdDraw.setPenColor(StdDraw.ORANGE);
        		xVelocity = yVelocity = 2;
        	}  
        	else if (i % 8 == 2) 
        	{
        		StdDraw.setPenColor(StdDraw.YELLOW);
        		xVelocity = yVelocity = 3;
        	}
        	else if (i % 8 == 3) 
        	{
        		StdDraw.setPenColor(StdDraw.GREEN);
        		xVelocity = yVelocity = 5;
        	}
        	else if (i % 8 == 4)
        	{
        		StdDraw.setPenColor(StdDraw.BLUE);
        		xVelocity = yVelocity = 7;
        	}
        	else if (i % 8 == 5)
        	{
        		StdDraw.setPenColor(StdDraw.MAGENTA);
        		xVelocity = yVelocity = 9;
        	}
        	else if (i % 8 == 6)
        	{
        		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        		xVelocity = yVelocity = 11;
        	}
        	else
        	{
        		if (i % 8 == 7)
        		{
            		StdDraw.setPenColor(StdDraw.BLACK);
            		xVelocity = yVelocity = 0;
        		}
        	}
        	
        	int x = random.nextInt(98) * (random.nextBoolean() ? -1 : 1); 
        	int y = random.nextInt(98) * (random.nextBoolean() ? -1 : 1); 
        	
        	// Loop: goes through all bodies, compares distance between objects.
        	// if object is randomly generated into each other:
        	//    generate new positions, then
        	//    break out of the for-loop
        	// if the the randomly generated positions aren't in the space of any other object, count should be zero, break out of the
        	// while true loop.
        	while (true)
        	{
        		int count = 0;
	        	for (int j = 0; j < i; j++)
	        	{
	        		if (Math.sqrt((allBodies[j].x - x)*(allBodies[j].x - x) + (allBodies[j].y - y)*(allBodies[j].y - y)) < 6)
	        		{
	        			count++;
	        			x = random.nextInt(98) * (random.nextBoolean() ? -1 : 1); 
	                	y = random.nextInt(98) * (random.nextBoolean() ? -1 : 1); 
	        			break;
	        		}
	        	}
	        	if (count == 0)
	        	{
	        		break;
	        	}
        	}
        	if (x > 0) {
				xVelocity = -xVelocity;
			}
			if (y > 0) {
				yVelocity = -yVelocity;
			}
        	// new Body(x, y, radius, xVelocity, yVelocity, xForce, yForce, mass)
			allBodies[i] = new Body(x, y, 0, xVelocity, yVelocity, 0, 0, sizeOfBodies, -1);
			StdDraw.filledCircle(x, y, 3);
		}
		for (int i = 0; i < calculators.length; i++)
		{
			//String t_name, Body[] bodies, int start, int end
			if (i == calculators.length-1)
			{
				//CalcForces(String t_name, Body[] bodies, int start, int end, double DT, Barrier barrier1, Barrier barrier2, int totalSteps)
				calculators[i] = new CalcForces("thread_" + i, allBodies, numBodies/numWorkers * i, numBodies, DT, barrier1, barrier2, timeSteps);
			}
			else 
			{
				calculators[i] = new CalcForces("thread_" + i, allBodies, numBodies/numWorkers * i, numBodies/numWorkers * (i+1), DT, barrier1, barrier2, timeSteps); 
			}
		}
		
		for (int i = 0; i < calculators.length; i++)
		{
			calculators[i].start();
		}
	}
	

}