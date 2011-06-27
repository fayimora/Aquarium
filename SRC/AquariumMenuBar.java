import java.awt.*;
import java.awt.event.*;

public class AquariumMenuBar extends MenuBar implements AquariumConstants
{
	//Constant fields
	//private static final long serialVersionUID = 1L;
	private Aquarium aquarium;

	//Instance fields
	//Menu Items
	private MenuItem startMenuItem;
	private MenuItem endMenuItem;
	private MenuItem setSpeedMenuItem;
	private MenuItem exitMenuItem;
	private MenuItem loadBgImageItem;
	private MenuItem loadNumberFishItem;
	private MenuItem aboutMenuItem;
	private MenuItem viewCommandsItem;
	private MenuItem viewHealthStats;
	private MenuItem viewLocationStats;

	public AquariumMenuBar(Aquarium aquarium)
	{	
		this.aquarium = aquarium;
		initMenuBar();
	}

	private void initMenuBar()
	{
		AquariumMenuBarHandler menuHandler = new AquariumMenuBarHandler();

		Menu menu = new Menu("File");

		loadBgImageItem = new MenuItem("Load Image");
		loadBgImageItem.addActionListener( menuHandler );
		menu.add( loadBgImageItem );
		menu.addSeparator();

		startMenuItem = new MenuItem("Start");
		startMenuItem.addActionListener( menuHandler );
		menu.add( startMenuItem );


		endMenuItem = new MenuItem("Pause");
		endMenuItem.addActionListener( menuHandler );
		menu.add( endMenuItem );


		exitMenuItem = new MenuItem("Exit");
		exitMenuItem.addActionListener( menuHandler );
		menu.addSeparator();
		menu.add( exitMenuItem );

		//View menu
		Menu viewMenu = new Menu("View");

		/*viewCommandsItem = new MenuItem("Commands");
		viewCommandsItem.addActionListener( menuHandler );
		viewMenu.add( viewCommandsItem );*/

		viewLocationStats = new MenuItem("Location stats");
		viewLocationStats.addActionListener( menuHandler );
		viewMenu.add( viewLocationStats );
		
		viewHealthStats = new MenuItem("Health stats");
		viewHealthStats.addActionListener( menuHandler );
		viewMenu.add( viewHealthStats );
		
		//Options menu
		Menu optionsMenu = new Menu("Options");

		setSpeedMenuItem = new MenuItem("Set Speed");
		setSpeedMenuItem.addActionListener( menuHandler );
		optionsMenu.add( setSpeedMenuItem );

		loadNumberFishItem  = new MenuItem("Set fish number");
		loadNumberFishItem.addActionListener( menuHandler );
		optionsMenu.add( loadNumberFishItem );

		/*Menu aboutMenu = new Menu("About");
		aboutMenuItem = new MenuItem("Info");
		aboutMenuItem.addActionListener( menuHandler );
		aboutMenu.add( aboutMenuItem );*/

		add( menu );
		add( viewMenu );
		add( optionsMenu );
		//add( aboutMenu );
		
	}

	private class AquariumMenuBarHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			
			Object source = event.getSource();
			
			if( source == startMenuItem )
			{
				//Start menu item clicked
				
				//If not not running then start the aquarium
				if( !aquarium.isRunning() )
				{
					aquarium.startRunning();
				}
				
			}
			else if ( source == setSpeedMenuItem)
			{
				aquarium.showSetFishSpeed();//Set the fish speed menu item clicked
			}
			else if ( source == loadNumberFishItem)
			{
				aquarium.showSetFishNumber();//Set the fish number menu item clicked
			}
			else if ( source == loadBgImageItem)
			{
				aquarium.loadBackgroundImage();//Load background menu item clicked
			}
			else if( source == endMenuItem )
			{
				//Pause menu item clicked
				if( aquarium.isRunning() )
				{
					aquarium.stopRunning();
				}
				
			}
			/*else if ( source == viewCommandsItem)
			{
				create a method in aquarium to call the a command menu class to animate the commands list
			}*/
			else if ( source == viewLocationStats)
			{
				aquarium.toggleStats(LOCATION_STATS); 
			}
			else if ( source == viewHealthStats )
			{
				aquarium.toggleStats(HEALTH_STATS);
			}
			/*else if ( source == aboutMenuItem)
			{
				//create a method in aquarium and call an about class to animate the about wndow
			}*/
			else if( source == exitMenuItem )
			{
				aquarium.quitProgram();//Quit menu item clicked
			}//End if else clause
		}//End method actionPerformed		
	}//End inner class AquariumMenuBarHandler	
}//End class AquariumMenuBar