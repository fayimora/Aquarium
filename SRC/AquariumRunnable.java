 /*
 AquariumRunnable handles running the Aquarium
 It allows all fish to swim.
 Sets the speed at which they swim.
 */
import java.util.*;
import java.io.*;

public class AquariumRunnable implements Runnable
{   
    //Instance fields
    private boolean runOK;//Flag signaling if the aquarium is running
    private Aquarium aquarium;//The aquarium the fish are in, the tank.
    private Vector<Fish>fishList;//A list of fish
    private int sleepTime = 110;//Controls the fish speed
    private int deadFish;
    
    private String message = "A fish has died";
    //Constructs a new AquariumRunnable
    public AquariumRunnable(Aquarium aquarium, Vector<Fish> allFish)
    {       
        runOK = false;
        this.aquarium = aquarium;
        fishList = allFish;     
    }
    
    public int getDeadFish(){
        return deadFish;
    }
    public void run() 
    {           
        Fish fish;
        //While ok to run we loop through all fish in the aquarium and allow them to swim
        while (runOK)
        {
            for (int i = 0; i < fishList.size(); i++)
            {   
                fish = (Fish)fishList.elementAt(i);//Get each fish
                if( fish.isAlive() )
                {//Check that the fish is alive
                    fish.swim();//Let it swim
                    aquarium.checkFishHealth();
                }
                else
                {
                    fishList.remove(fish);//If the fish is not alive remove it
                    try{
                        BufferedWriter buffer = new BufferedWriter(new FileWriter("deadFish.txt"));
                        buffer.write(message);
                        deadFish++;
                        buffer.close();
                    }
                    catch(IOException e){
                        System.err.println(e.getMessage());
                    }
                }
            }

            try
            {
                Thread.sleep(sleepTime);//Sleep for the given time then we get the next fish
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
            aquarium.repaint();
        }
    }

    
    //Sets the sleep time to the given value
    public void setSleepTime(int time)
    {
        if( sleepTime + time >= 0)
        {
            sleepTime = time;
        }
    }
    
    //returns he sleep time
    public int getSleepTime()
    {
        return sleepTime;
    }
    
    //Returns true if the aquarium is running
    public boolean isRunOK()
    {       
        return  runOK;
    }

    //Stops running the aquarium
    public void stopRunOK()
    {       
        runOK = false;        
    }
    
    //Resumes the aquarium
    public void startRunOK()
    {
        runOK = true;
    }

}//End class AquariumRunnable