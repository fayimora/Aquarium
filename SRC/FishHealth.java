public class FishHealth 
{
	public static final int INITIAL_HEALTH = 100;
	private int currentHealth;
	private boolean hasHealth;
	
	public FishHealth()
	{		
		currentHealth = INITIAL_HEALTH;
		hasHealth = true;
	}
		
	public void decreaseHealth(){
		
		int newHealthAmount = (int) (currentHealth - Math.random() * 5);
		if ( newHealthAmount > 0)
		{
			currentHealth = newHealthAmount;
		}
		if( newHealthAmount <= 0)
		{
			currentHealth = 0;
			hasHealth = false;
		}
	}
	
	/**
	 * Increases the fish health 
	 * @return Returns the amount of health increased
	 */
	public int increaseHealth()
	{
		int feedAmount = (int) (Math.random() * 5);
		
		int newHealthAmount = (int) (currentHealth + feedAmount);
		if ( newHealthAmount  <= INITIAL_HEALTH)
		{
			currentHealth = newHealthAmount;
		}
		else if ( newHealthAmount > INITIAL_HEALTH)
		{
			feedAmount = INITIAL_HEALTH - currentHealth;
			currentHealth = INITIAL_HEALTH;
			
		}
		return feedAmount;
	}
	
	public boolean isEmpty() 
	{
		return !hasHealth;
	}

	public int getValue() 
	{	
		return currentHealth;
	}

}