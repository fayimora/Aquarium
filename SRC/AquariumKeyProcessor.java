import java.awt.event.*;

public class AquariumKeyProcessor implements KeyListener, AquariumCommandConstants, AquariumConstants
{
	//Instance fields
	private Commands cmdList = new Commands();//List of commands
	
	public void keyPressed(KeyEvent event)
	{
		int keyCode = event.getKeyCode();
		Object source =  event.getSource() ;
		Aquarium aquarium = null;

		//User pressed a key on the aquarium window
		if( source instanceof Aquarium )
		{
			aquarium = (Aquarium)source;
			processCommand( keyCode , aquarium);
		}
	}
	
	//Gets a String list of commands and returns a list of commands
	public String getCommands()
	{
		return cmdList.getCommandList();
	}

	private void processCommand(int keyCode, Aquarium aquarium)
	{
		String commandString = cmdList.getCommand(keyCode);
		
		if ( commandString == PAUSE)
		{//Pause the aquarium
			aquarium.toggleProgramRunning();
		}
		else if( commandString == COMMANDS)
		{//Show list of commands
			//Passes a string representation of the command list to the aquarium
			aquarium.showCommands( );
		}
		else if(commandString == START_STOP_FISH_HUNGER)
		{//Start/Stop fish simulation
			aquarium.toggleFishHunger();
		}
		else if( commandString == SHOW_HEALTH)
		{//Show fish health
			aquarium.toggleStats(HEALTH_STATS);
		}
		else if ( commandString == SHOW_LOCATION)
		{//Show x y location
			aquarium.toggleStats(LOCATION_STATS);
		}
		else if ( commandString == SHOW_INFO_PANEL)
		{//Show the info status panel
			aquarium.toggleInfoPanel();
		}
		else if( commandString == STATISICS)
		{//Show All statistics
			aquarium.toggleStats(LOCATION_STATS);
			aquarium.toggleStats(HEALTH_STATS);
		}
		else if ( commandString == LOAD_BG_IMAGE)
		{//Load a background image
			//aquarium.loadBackgroundImage();
		}
		else if ( commandString == PLUS)
		{//Increase Aquarium speed
			aquarium.increaseSpeed();
		}
		else if ( commandString == MINUS)
		{//Decrease Aquarium speed
			aquarium.decreaseSpeed();
		}
		else if ( commandString == TOGGLE_MENU)
		{//Toggle menu bar on or off
			aquarium.toggleMenuBar();
		}
		else if ( commandString == QUIT)
		{//Quit the aquarium program
			aquarium.quitProgram();
		}
		else if( commandString == FEED_FISH)
		{//Feed the fish
			aquarium.feedFish();
		}	
	}
	
	public void keyReleased(KeyEvent arg0)
	{

	}
	
	public void keyTyped(KeyEvent arg0) {

	}


}
