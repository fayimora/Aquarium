import java.awt.*;

public class FishStats
 {
	
	private boolean locationStatsEnabled;//Enable or disable location stats
	private boolean healthStatsEnabled;
	private Point location;
	private FishHealth health;

	public FishStats(Point location, FishHealth health) 
	{
		setLocation(location);
		this.health = health;
	}

	public void setLocation(Point location) 
	{
		this.location = location;
	}

	public int getXLocation()
	{
		return location.x;
	}
	
	public int getYLocation()
	{
		return location.y;
	}
	
	public int getHealth()
	{
		
		return health.getValue();
	}
	

	public boolean hasLocationStatsEnabled()
	{
		return locationStatsEnabled;
	}
	
	public boolean hasHealthStatsEnabled()
	{
		return healthStatsEnabled;
	}
	
	public void toggleLocationStats()
	{
		
		if( locationStatsEnabled == false){
			locationStatsEnabled = true;
		}else{
			locationStatsEnabled = false;
		}
	}
	
	public void toggleHealthStats()
	{
		
		if( healthStatsEnabled == false )
		{
			healthStatsEnabled = true;
		}
		else
		{
			healthStatsEnabled = false;
		}
	}


}