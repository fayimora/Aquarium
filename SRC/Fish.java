import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

public class Fish implements AquariumConstants, ActionListener
{

	private Component tank;
	private Image image1;
	private Image image2;
	private Point location;
	private Point velocity;
	private Rectangle edges;
	private Random random;
	private FishStats stats;
	private FishHealth health;
	private Timer hungerTimer;


	/**
	 * Constructs a new Fish
	 * @param image The first image for the fish
	 * @param image2 The second image for the fish
	 * @param edges The border boundaries for the fish to swim around
	 * @param tank The Aquarium tank that the fish swims in
	 */
	public Fish(Image image, Image image2, Rectangle edges, Component tank)
	{

		random = new Random(System.currentTimeMillis());//Holds random values for the fish swimming
		image1 = image;
		this.image2 = image2;
		this.edges = edges;
		this.tank = tank;
		this.location = new Point(100 + (Math.abs(random.nextInt()) % 300), 100 + (Math.abs(100 + random.nextInt()) % 100));

		this.velocity = new Point(random.nextInt() % 8, random.nextInt() % 8);
		
		int time = (int) (Math.random() * (1000 * 60) * 5  );//5 minutes
		hungerTimer = new Timer(time, this);
		
		health = new FishHealth();
		stats = new FishStats(location, health);

	}
	
	// This method toggles Toggles the fishes hunger on and off
	public void toggleFishHunger()
	{	
		if( hungerTimer.isRunning() )
		{
			stopFishHunger();
		}
		else
		{
			startFishHunger();
		}
	}
	
	/**
	 * Checks to see if the fish hunger timer is running
	 * @return Returns true if running or false if not
	 */
	public boolean isHungerTimerRunning()
	{
		
		return hungerTimer.isRunning();
	}
	
	/*
	 * Starts the fish hunger timer
	 */
	private void startFishHunger()
	{
		hungerTimer.start();
	}
	
	/*
	 * Stops the fish hunger timer
	 */
	private void stopFishHunger()
	{
		hungerTimer.stop();
	} 
	
	/**
	 * Checks to see if the fish has low health 
	 * @return Returns true if the fish has low health or false if not
	 */
	public boolean hasLowHealth()
	{
		
		return health.getValue() <= 25;
	}
	
	/**
	 * 
	 * @return Returns the fish health
	 */
	public int getHealth()
	{
		
		return health.getValue();
	}

	/**
	 * Allows the fish to swim
	 */
	public void swim()
	{

		if(random.nextInt() % 7 <= 1){

			velocity.x += random.nextInt() % 4;

			velocity.x = Math.min(velocity.x, 8);
			velocity.x = Math.max(velocity.x, -8);

			velocity.y += random.nextInt() % 4;

			velocity.y = Math.min(velocity.y, 8);
			velocity.y = Math.max(velocity.y, -8);
		}
		
		location.x += velocity.x;
		location.y += velocity.y;

		checkBoundaries();
	}

	//Checks the fish boundaries limit
	private void checkBoundaries()
	{
		if (location.x < edges.x)
		{
			location.x = edges.x;
			velocity.x = -velocity.x;
		}

		if ( (location.x + image1.getWidth(tank) ) > (edges.x + edges.width)){
			location.x = edges.x + edges.width - image1.getWidth(tank);
			velocity.x = -velocity.x;
		}

		if (location.y < edges.y)
		{
			location.y = edges.y;
			velocity.y = -velocity.y;
		}

		if ( (location.y + image1.getHeight(tank) )	> (edges.y + edges.height) )
		{
			location.y = edges.y + edges.height - image1.getHeight(tank);
			velocity.y = -velocity.y;
		}
	}

	//Draws the fish image. g is the graphics object to draw with
	public void drawFishImage(Graphics g)
	{
		if(velocity.x < 0) {
			g.drawImage(image1, location.x, location.y, tank);
		}
		else {
			g.drawImage(image2, location.x, location.y, tank);
		}
		//If stats are enable draw statistics with the fish
		if( stats.hasLocationStatsEnabled() ){
			drawLocationStats(g);
		}
		
		if( stats.hasHealthStatsEnabled()){
			drawHealthStats(g);
		}
	}
	
	//Draws statistics for the fish
	private void drawLocationStats(Graphics g)
	{
		final int X_SPACER = -5;
	
		String statsString =  "x: " + location.x + " | y: " + location.y;
		g.setFont( new Font("Arial",Font.BOLD, 12));
		
		int x = stats.getXLocation();
		int y = stats.getYLocation();
		
		g.setColor(Color.yellow);
		g.drawString(statsString, x - X_SPACER , y -20);
	}
	
	//Draws statistics for the fish
	private void drawHealthStats(Graphics g)
	{
		final int X_SPACER = 15;
		int healthValue = health.getValue();
		
		g.setFont( new Font("Arial",Font.BOLD, 12));
		
		int x = stats.getXLocation();
		int y = stats.getYLocation() +10;
		
		if( healthValue > 50)
		{
			g.setColor(Color.green);
		}
		else if( healthValue >25 )
		{
			g.setColor(Color.yellow);
		}
		else
		{
			g.setColor(Color.red);
		}
		String healthString ="Health: "+ String.valueOf(healthValue);
		g.drawString(healthString, x + X_SPACER , y - 10);
	}

	/**
	 * Toggles the given stats choice to enabled or disabled 
	 * @param statsChoice - The statistic choice to enable or disable
	 */
	public boolean toggleStats(String statsChoice) {
		
		boolean state = true;
		if( statsChoice.equals(LOCATION_STATS))
		{
			stats.toggleLocationStats();	
			state = stats.hasLocationStatsEnabled();
		}
		else if ( statsChoice.equals(HEALTH_STATS))
		{
			stats.toggleHealthStats();	
			state = stats.hasHealthStatsEnabled();
		}
		else
		{
			state = false;
		}
		return state;
	}
	
	/**
	 * Feeds the fish
	 */
	public int feed()
	{
		
		return health.increaseHealth();
	}
	
	/**
	 * Checks if the fish is alive
	 * @return Returns true if the fish is alive or false if not
	 */
	public boolean isAlive()
	{	
		return !health.isEmpty();//If health is not empty then the fish is alive
	}
	
	//Updates the boundary edges for the fish boundaries.
	public void updateEdges(Rectangle newEdges)
	{
		this.edges = newEdges;
	}

	public void actionPerformed(ActionEvent event)
	{
		if( !health.isEmpty()){
			health.decreaseHealth();	
		}
	}
}