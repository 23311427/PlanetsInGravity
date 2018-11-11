
public class Body
{
	double x;
	double y;
	double radius;
	double xVelocity;
	double yVelocity;
	double xForce;
	double yForce;
	double mass;
	int collidedWith;
	
	public Body(int x, int y, int radius, int xVelocity, int yVelocity, int xForce, int yForce, double mass, int collidedWith)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.xForce = xForce;
		this.yForce = yForce;
		this.mass = mass;
		this.collidedWith = -1;
	}
}

