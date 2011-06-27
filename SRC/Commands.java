import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Commands implements AquariumCommandConstants
{	
	//Instance fields
	private HashMap<Integer,String> cmds;	
	
	public Commands()
	{
		cmds = new HashMap<Integer, String>();
		
		cmds.put(KeyEvent.VK_P, PAUSE);
		cmds.put(KeyEvent.VK_H, SHOW_HEALTH);
		cmds.put(KeyEvent.VK_X, SHOW_LOCATION);
		cmds.put(KeyEvent.VK_I, SHOW_INFO_PANEL);
		cmds.put(KeyEvent.VK_L, LOAD_BG_IMAGE);
		cmds.put(KeyEvent.VK_C, COMMANDS);
		cmds.put(KeyEvent.VK_M, TOGGLE_MENU);
		cmds.put(KeyEvent.VK_ADD, PLUS);
		cmds.put(KeyEvent.VK_F, FEED_FISH);
		cmds.put(KeyEvent.VK_S, START_STOP_FISH_HUNGER);
		cmds.put(KeyEvent.VK_SUBTRACT, MINUS);
		cmds.put(KeyEvent.VK_Q, QUIT);
		cmds.put(KeyEvent.VK_ESCAPE, QUIT);

	}
	
	//Returns a list of commands 
	public String getCommandList()
	{		
		String results = "";
		Set<Integer> keys = cmds.keySet();
		Iterator<Integer> it = keys.iterator();
		
		/*
		 * Get each key and value pair
		 * We get the integer Key constant's string equivalent
		 * Then concat that to it's String value 
		 */
		while( it.hasNext() )
		{			
			int keyInt =  it.next(); //Get the integer key 
			String keyCmd = KeyEvent.getKeyText(keyInt);//Get the String representation of the key code.
			
			//If the key command string is "Quit" then we concat the command string "Quit" with the "Escape" string.
			if(keyCmd.equals("Q"))
			{				
				results += keyCmd + " or Escape " + " : " + cmds.get( keyInt) + "\n";

			}
			else if(keyCmd.equals("Escape"))
			{//If the key command is "Escape" then we skip adding it to be printed because it has been concated with "Quit" key command
				continue;
			}
			else
			{ //Other wise we add the string command with it's string value normally
				results += keyCmd + " : " + cmds.get( keyInt) + "\n";
			}
		}		
		return results;
	}	
	
	//Gets a user command cmd The key code for the command Returns a command for the key code	 
	public String getCommand(int cmd)
	{	
		return cmds.get(cmd);	
	}

	public static void main(String[] args)
	{	
		Commands cmds = new Commands();
		System.out.println( cmds.getCommandList() );
	}

	
}
