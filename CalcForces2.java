
import java.util.concurrent.TimeUnit;

public class CalcForces2 extends Thread
{
	private Thread t;
	private String t_name;
	private Body[] bodies;
	private int start;
	private int end;
	private double DT;
	private Barrier2 barrier1, barrier2;
	private int totalSteps;
	
	public CalcForces2(String t_name, Body[] bodies, int start, int end, double DT, Barrier2 barrier1, Barrier2 barrier2, int totalSteps)
	{
		this.t_name = t_name;
		this.bodies = bodies;
		this.start = start;
		this.end = end;
		this.DT = DT;
		this.barrier1 = barrier1;
		this.barrier2 = barrier2;
		this.totalSteps = totalSteps;
		System.out.println("Creating thread name is " + t_name);
	}
	
	public void setBodies(Body[] bodies)
	{
		this.bodies = bodies;
	}
	
	public void start()
	{
		if (t == null)
		{
			t = new Thread(this, t_name);
			t.start();
		}
		System.out.println("starting " + t_name);
	}
	
	private static String print(Body[] bodies, int start, int end, String params) {
		String s = "";
		if (params.equals("force")) {
			for (int i = start; i < end; i++) {
				s += "body at " + i + ": force x: " + bodies[i].xForce + ", force y: " + bodies[i].yForce + "\n";
			}
		}
		if (params.equals("velocity")) {
			for (int i = start; i < end; i++) {
				s += "body at " + i + ": velocity x: " + bodies[i].xVelocity + ", velocity y: " + bodies[i].yVelocity + "\n";
			}
		}
		if (params.equals("position")) {
			for (int i = start; i < end; i++) {
				s += "body at " + i + ": position x: " + bodies[i].x + ", position y: " + bodies[i].y + "\n";
			}
		}
		return s;
	}
	
	@Override
	public void run() 
	{
		long startTime = System.nanoTime();
		for (int step = 0; step < totalSteps; step++)
		{
			System.out.println("running " + t_name + " for time step " + (step+1));
			double distance, magnitude, directionX, directionY;
			double gravity = 0.0000000000667408;
			// CALCULATE FORCES THREADS START HERE
			if (step == 0)
			{
				System.out.println(print(bodies, start, end, "force"));
				System.out.println(print(bodies, start, end, "velocity"));
				System.out.println(print(bodies, start, end, "position"));
			}
			if (step == 1)
			{
				System.out.println(print(bodies, start, end, "force"));
				System.out.println(print(bodies, start, end, "velocity"));
				System.out.println(print(bodies, start, end, "position"));
			}
			System.out.println("   Calculating forces for " + t_name);
//			for (int i = start; i < end - 1; i++)
//			{
//				for (int j = i + 1; j < end; j++)
//				{
			for (int i = start; i < bodies.length - 1; i++)
			{
				for (int j = i + 1; j < bodies.length; j++)
				{
					distance = Math.sqrt((bodies[j].x - bodies[i].x) * (bodies[j].x - bodies[i].x) + (bodies[j].y - bodies[i].y) * (bodies[j].y - bodies[i].y));
					magnitude = (gravity * bodies[i].mass * bodies[j].mass)/(distance * distance);
					directionX = bodies[j].x-bodies[i].x;
					directionY = bodies[j].y-bodies[i].y;
					bodies[i].xForce = bodies[i].xForce + magnitude * directionX / distance;
					bodies[i].yForce = bodies[i].yForce + magnitude * directionY / distance;
					
					bodies[j].xForce = bodies[j].xForce - magnitude * directionX / distance;
					bodies[j].yForce = bodies[j].yForce - magnitude * directionY / distance;
				}
			}
			print(bodies, start, end, "force");
			System.out.println("start is " + start + ", end is " + end);
			try 
			{ 
				barrier1.checkBarrier(t_name + " at barrier1", bodies); 
			}
			catch (InterruptedException e)
			{
				e.printStackTrace(); 
			}
			// BARRIER HERE, CALCULATE FORCES THREADS FINISH HERE AND WAIT FOR EACH OTHER
			double deltaVX, deltaVY, deltaPX, deltaPY;
			// UPDATE VELOCITIES/POSITIONS STARTS HERE
			System.out.println("   Calculating velocity/positions for " + t_name);
			for (int i = start; i < end; i++)
			{
				deltaVX = bodies[i].xForce/bodies[i].mass * DT;
				deltaVY = bodies[i].yForce/bodies[i].mass * DT;
				
				deltaPX = (bodies[i].xVelocity + deltaVX/2) * DT;
				deltaPY = (bodies[i].yVelocity + deltaVY/2) * DT;
				
				bodies[i].xVelocity = bodies[i].xVelocity + deltaVX;
				bodies[i].yVelocity = bodies[i].yVelocity + deltaVY;
				
				bodies[i].x = bodies[i].x + deltaPX;
				bodies[i].y = bodies[i].y + deltaPY;
				
				bodies[i].xForce = bodies[i].yForce = 0;
			}
			print(bodies, start, end, "velocity");
			print(bodies, start, end, "position");
			// AFTER MOVING HANDLE COLLISIONS
//			for (int i = start; i < end - 1; i++)
//			{
//				for (int j = i + 1; j < end; j++)
//				{
			for (int i = start; i < bodies.length - 1; i++)
			{
				for (int j = i + 1; j < bodies.length; j++)
				{
					double distanceXY = Math.sqrt((bodies[j].x - bodies[i].x) * (bodies[j].x - bodies[i].x)
							+ (bodies[j].y - bodies[i].y) * (bodies[j].y - bodies[i].y));
					if (distanceXY <= 6 && distanceXY > 5.7 && bodies[i].collidedWith != j) 
					{
						bodies[i].collidedWith = j;
						double obj1_VX, obj1_VY, obj2_VX, obj2_VY;
						double a, b, c, d, xDist_squared, yDist_squared; 
						double xDistance, yDistance;
						xDistance = bodies[j].x - bodies[i].x; 
						yDistance = bodies[j].y - bodies[i].y;
	
						a = bodies[j].xVelocity * (xDistance) * (xDistance);
						b = bodies[j].yVelocity * (xDistance) * (yDistance);
						c = bodies[i].xVelocity * (yDistance) * (yDistance);
						d = bodies[i].yVelocity * (xDistance) * (yDistance);
						xDist_squared = (xDistance) * (xDistance);
						yDist_squared = (yDistance) * (yDistance);
	
						obj1_VX = (a + b + c - d) / (xDist_squared + yDist_squared);
	
						a = bodies[j].xVelocity * (xDistance) * (yDistance);
						b = bodies[j].yVelocity * (yDistance) * (yDistance);
						c = bodies[i].xVelocity * (xDistance) * (yDistance);
						d = bodies[i].yVelocity * xDist_squared;
	
						obj1_VY = (a + b - c + d) / (xDist_squared + yDist_squared);
	
						a = bodies[i].xVelocity * xDist_squared;
						b = bodies[i].yVelocity * xDistance * yDistance;
						c = bodies[j].xVelocity * yDist_squared;
						d = bodies[j].yVelocity * xDistance * yDistance;
	
						obj2_VX = (a + b + c - d) / (xDist_squared + yDist_squared);
	
						a = bodies[i].xVelocity * xDistance * yDistance;
						b = bodies[i].yVelocity * yDist_squared;
						c = bodies[j].xVelocity * xDistance * yDistance;
						d = bodies[j].yVelocity * xDist_squared;
	
						obj2_VY = (a + b - c + d) / (xDist_squared + yDist_squared);
	
						bodies[i].xVelocity = obj1_VX;
						bodies[i].yVelocity = obj1_VY;
						bodies[j].xVelocity = obj2_VX;
						bodies[j].yVelocity = obj2_VY;
					} else if (distanceXY <= 5.7 && bodies[i].collidedWith != j) {
						bodies[i].collidedWith = j;
						double obj1_VX, obj1_VY, obj2_VX, obj2_VY;
						double a, b, c, d, xDist_squared, yDist_squared;
						double xDistance, yDistance;
	
						xDistance = bodies[j].x - bodies[i].x;
						yDistance = bodies[j].y - bodies[i].y;
	
						xDist_squared = (xDistance) * (xDistance);
						yDist_squared = (yDistance) * (yDistance);
	
						xDistance = 6 * xDistance / Math.sqrt(xDist_squared + yDist_squared);
						yDistance = 6 * yDistance / Math.sqrt(xDist_squared + yDist_squared);
	
						xDist_squared = (xDistance) * (xDistance);
						yDist_squared = (yDistance) * (yDistance);
	
						a = bodies[j].xVelocity * (xDistance) * (xDistance);
						b = bodies[j].yVelocity * (xDistance) * (yDistance);
						c = bodies[i].xVelocity * (yDistance) * (yDistance);
						d = bodies[i].yVelocity * (xDistance) * (yDistance);
	
						obj1_VX = (a + b + c - d) / (xDist_squared + yDist_squared);
	
						a = bodies[j].xVelocity * (xDistance) * (yDistance);
						b = bodies[j].yVelocity * (yDistance) * (yDistance);
						c = bodies[i].xVelocity * (xDistance) * (yDistance);
						d = bodies[i].yVelocity * xDist_squared;
	
						obj1_VY = (a + b - c + d) / (xDist_squared + yDist_squared);
	
						a = bodies[i].xVelocity * xDist_squared;
						b = bodies[i].yVelocity * xDistance * yDistance;
						c = bodies[j].xVelocity * yDist_squared;
						d = bodies[j].yVelocity * xDistance * yDistance;
	
						obj2_VX = (a + b + c - d) / (xDist_squared + yDist_squared);
	
						a = bodies[i].xVelocity * xDistance * yDistance;
						b = bodies[i].yVelocity * yDist_squared;
						c = bodies[j].xVelocity * xDistance * yDistance;
						d = bodies[j].yVelocity * xDist_squared;
	
						obj2_VY = (a + b - c + d) / (xDist_squared + yDist_squared);
	
						bodies[i].xVelocity = obj1_VX;
						bodies[i].yVelocity = obj1_VY;
						bodies[j].xVelocity = obj2_VX;
						bodies[j].yVelocity = obj2_VY;
					}
					else 
					{
						bodies[i].collidedWith = -1;
					}
				}
				
			}
			
			for (int i = start; i < end; i++)
			{
				if (bodies[i].collidedWith == -1)
				{
					if (Math.abs(bodies[i].x + bodies[i].xVelocity) > 100 - 3) bodies[i].xVelocity = -bodies[i].xVelocity;
		            if (Math.abs(bodies[i].y + bodies[i].yVelocity) > 100 - 3) bodies[i].yVelocity = -bodies[i].yVelocity;
				}
			}
			// BARRIER HERE, UPDATING VELOCITIES/POSITIONS THREADS FINISH HERE AND WAIT FOR EACH OTHER
			try 
			{ 
				barrier2.checkBarrier2(t_name + " at barrier2", bodies); 
			}
			catch (InterruptedException e)
			{
				e.printStackTrace(); 
			}
//			try 
//			{
//				TimeUnit.SECONDS.sleep(1);
//			}	
//			catch (InterruptedException e)
//			{
//				e.printStackTrace(); 
//			}
		}
		
//		p.print("\n\nFINAL STAGE\nForces:\n" + print(bodies, start, end, "force") + "\n\nVelocities:\n"
//		     	+ print(bodies, start, end, "velocity") + "\n\nPositions:\n" + print(bodies, start, end, "position"));
		System.out.println(t_name + " finished excuting");
		
		System.out.println(print(bodies, start, end, "force"));
		System.out.println(print(bodies, start, end, "velocity"));
		System.out.println(print(bodies, start, end, "position"));
		
		long endTime = System.nanoTime();
		long diff = endTime - startTime;
		System.out.println("Computation time for " + t_name + ": seconds " + diff/1000000000 + " milliseconds " + (diff-(diff/1000000000)*1000000000)/1000000);
		return;
	}
}

