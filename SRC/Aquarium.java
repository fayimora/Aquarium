import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.util.concurrent.*;
import javax.swing.*;
import java.io.*;


public class Aquarium extends Frame implements AquariumMediaTrackerConstants, AquariumFilePathConstants, AquariumFishConstants
{
    static String user;
    static String pass;
    private static JFrame frame;
    private String message;
    
     private static JTextField username;
     private static JPasswordField password;
    //Constant fields
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "Aquarium";

    //Instance fields
    private MediaTracker mTracker;//Keeps tracks of images
    private Image backgroundImage, bufferImage; //Images for the background and an image for drawing things off screen
    private Graphics bufferGraphics;//Graphics context for drawing off screen
    private Image[] fishImages;//Images of all fish in the aquarium tank
    private Vector<Fish> fishes = new Vector<Fish>();//Holds a list of all fish objects
    private ExecutorService aquariumExecutor;   //Manages threads for the Aquarium
    private AquariumRunnable aquariumRunnable;//Runs the aquarium program till it is told to quit
    private AquariumKeyProcessor aquariumKeyProcessor;//Handles processing user key actions
    private int currentSpeed;//The current aquarium speed
    private StatusPanel statusPanel;
    private Panel bottomStatus;
    private boolean firstRun;
    private boolean simulationRunning;

    public Aquarium()
    {
        this(DEFAULT_NUM_FISH);
    }

    public Aquarium( int numFish)
    {
        mTracker = new MediaTracker(this);
        currentSpeed = DEFAULT_USER_FISH_SPEED;
        firstRun = true;
        //If we load the fish successfully  then we can start the program
        if( loadFish(numFish) )
        {
            initFrame();
            startProgram();         
        }
        else
        {
            System.err.print("Could not load fish");
        }
    }

    private void initFrame() {

        setTitle(TITLE);
        setResizable( false );

        setMenuBar( new AquariumMenuBar(this) );
        createStatusBar();

        loadDefaultBackgroundImage(DEFAULT_BACKGROUND_IMAGE);

        aquariumKeyProcessor = new AquariumKeyProcessor( );
        addKeyListener( aquariumKeyProcessor );

        //Sets the frame size to the size of the background image
        addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                quitProgram();
            }
        });
    }

    private void createStatusBar() {

        bottomStatus = new Panel( new BorderLayout() );
        statusPanel = new StatusPanel();
        bottomStatus.add( statusPanel );
        add( bottomStatus, BorderLayout.SOUTH);

    }

    //Starts this program
    private void startProgram() {

        bufferImage = createImage(getSize().width, getSize().height);
        bufferGraphics = bufferImage.getGraphics();

        addNewFish();
        aquariumExecutor = Executors.newCachedThreadPool();
        aquariumRunnable = new AquariumRunnable(this, fishes);
        setSpeed(currentSpeed);
        startRunning();
        firstRun = false;
        bottomStatus.setVisible(false);
    }
    
    //Loads a default background image when the program initializes
    public void loadDefaultBackgroundImage(String background)
    {
        Image image = getInternalImage(background);
        if(image != null )
        {
            updateBackgroundImage( image );
            setVisible(true );
            setSize( backgroundImage.getWidth( this ) , backgroundImage.getHeight( this ));
        }
    }
    
    //Loads a background selected background into the aquarium
    public void loadBackgroundImage()
    {
        Image newImage = getImage();
        if( newImage != null )
        {
            updateBackgroundImage( newImage );
            setSize( backgroundImage.getWidth( this ) , backgroundImage.getHeight( this ));
            updateFishBorders();            
        }
    }

    //Updates the edges of the background so that the fish doesnt swim out.lol
    private void updateBackgroundImage(Image newImage)
    {
        if( backgroundImage != null)
        {
            mTracker.removeImage(backgroundImage, BACKGROUND_IMAGE_ID);//Remove old image
        }
        
        backgroundImage = newImage;//assign new image as background
        mTracker.addImage(backgroundImage, BACKGROUND_IMAGE_ID);//Add the new image to media tracker
        loadTrackerImages(BACKGROUND_IMAGE_ID);//Load the new background image
    }
    
    /*
     * Loads a list of fish images from an internal directory
     * If this fails then we return right away.lolol
     */
    private boolean loadFish(int numberFish)
    {
        boolean success = false;
        fishImages = new Image[numberFish*2];//Each fish has 2 images for left and right so we multiply by 2
        FishListEnum[] fishImagesList = FishListEnum.values();//Get an enumeration of the fish list image paths

        //Get it's path and read in it's image 
        //Then add each image to a list that holds the fish images
        for(int i = 0; i < fishImages.length; i++)
        {
            //Get each file image name, looping back to the first image after we reach the end of the file image list 
            Image fishImage =  getInternalImage( fishImagesList[i % fishImagesList.length  ].toString() ); //Get the file images path and create an image to add to the fish images list
            if( fishImage != null)
            {
                fishImages[i] = fishImage;
                mTracker.addImage( fishImages[i], FISH_IMAGE_ID);//Add the image to media tracker
                //if the fish image is null, the fish images count will be null!
            }
            else
                return false;
        }//End for loop
        loadTrackerImages(FISH_IMAGE_ID);

        success = true;//If we successfully loaded all fish then we return true
        return success;
    }//End method loadFishImages

    //Adds a given number of fish to a database for fish 
    private void addNewFish()
    {
        //Create a rectangle that holds the frames actual border edges/boundaries 
        Rectangle edges = getBorderBoundaries();
        for (int i = 0; i < fishImages.length; i += 2)
        {
            fishes.add( new Fish(fishImages[i], fishImages[i+1], edges, this));
            try
            {
                Thread.sleep(20);
            }
            catch (Exception exp)
            {
                System.out.println("Unable to add new fish\n" + exp.getMessage());
            }
        }//End for loop
    }//End addFish method

    //Gets the border boundaries for the aquarium
    private Rectangle getBorderBoundaries()
    {
        int sPanelHeight = 0;
        if( bottomStatus.isVisible() )
        {
            sPanelHeight = statusPanel.getHeight(); 
        }
        return  new Rectangle(0 + getInsets().left,  0  + getInsets().top, getSize().width - (getInsets().left + getInsets().right), getSize().height - ( (getInsets().top + getInsets().bottom) + sPanelHeight ));
    }

    //Removes all fish from the aquarium and loads a new number of fish to the tank
    private void changeNumberOfFish(int numberFish)
    {
        loadFish(numberFish);
        if( !fishes.isEmpty() )
        {
            fishes.removeAllElements();
        }
        addNewFish();
    }

    
    //Updates all fish in the aquarium then alerts the fish.
    private void updateFishBorders()
    {
        //Create a rectangle that holds the frames actual border edges/boundaries 
        Rectangle edges = getBorderBoundaries();

        for(Fish fish : fishes)
        {
            fish.updateEdges( edges );
        }
    }

    //Loads all tracker images with the given tracker id
    private void loadTrackerImages(int id)
    {
        try
        {
            mTracker.waitForID(id);
        }
        catch (InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    //Allows the user to get an image from there file system
    private Image getImage()
    {
        JFileChooser jfc = new JFileChooser();
        int state = jfc.showOpenDialog( this );
        if( state == JFileChooser.APPROVE_OPTION)
        {
            File file = jfc.getSelectedFile();
            return Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
        }
        return null;
    }

    //Loads an internal image in the applications path
    private Image getInternalImage(String imagePath)
    {
        URL url = this.getClass().getResource(imagePath);

        Image image = null;
        try
        {
            image = ImageIO.read(url);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }   
        return image;
    }

    //This overridden update method allows the background area to not be fully redrawn
    public void update(Graphics g)
    {
        drawBackgroundImage();  

        for (int i = 0; i < fishes.size(); i++)
        {
            //Get each fish and draw it to an off screen buffered image with that images graphic context
            ((Fish)fishes.elementAt(i)).drawFishImage(bufferGraphics);
        }
        //Draw the off screen buffered image to the frame with it's graphic context
        g.drawImage(bufferImage, 0, 0, this);   
    }


    //Draw the given background image with the buffered images graphics context
    private void drawBackgroundImage()
    {
        bufferGraphics.drawImage(backgroundImage, 0, 0, this);
    }

    public void checkFishHealth()
    {
        if( simulationRunning )
        {
            aquariumExecutor.execute(new Runnable(){
                        public void run()
                        {
                            Fish fish;
                            for (int i = 0; i < fishes.size(); i++)
                            {
                                fish = (Fish)fishes.elementAt(i);
                                if( fish.hasLowHealth())
                                {    
                                    setInfoStatusAndAnimate("Fish health is low!", null);
                                    if( !fish.isAlive())
                                    {
                                        setInfoStatusAndAnimate("Fish has died!", null);
                                    }
                                }
                            }   

                        }
                    }//End anonymous Runnable
            );//End aquariumExecutor argument/Invoke execute method
        }//End checking if simulationRunning clause
    }//End method checkFishHealth

    public void feedFish()
    {

        if( simulationRunning )
        {
            int totalFeedAmount= 0;
            Fish fish;
            for (int i = 0; i < fishes.size(); i++)
            {
                fish = (Fish)fishes.elementAt(i);
                totalFeedAmount += fish.feed();
            }   
            setInfoStatusAndAnimate("You give the fish " + totalFeedAmount + " fish food.", null);
        }
        else
        {
            setInfoStatusAndAnimate("Start the simulation to feed the fish", null);
        }
    }
    
    //Increases animation speed
    public void increaseSpeed()
    {
        //This will get the user to the max speed possible
        while(  (currentSpeed + 5 > MAX_USER_FISH_SPEED ))
        {
            currentSpeed--;
        }
        setSpeed(currentSpeed +=5);
    }
    
    //Decreases animation speed
    public void decreaseSpeed()
    {
        //This will get the user to the minimum speed possible
        while(  (currentSpeed - 5 < 0))
        {
            currentSpeed++;
        }

        setSpeed(currentSpeed -=5);
    }

    //Sets the info status and animates the info panel
    //If the boolean state is null then we only show the status
    //Otherwise if state is not null then we show the status along with the boolean state
    private void setInfoStatusAndAnimate(String status, Boolean state)
    {
        if( state != null )
        {//If a boolean value was passed then we append that to the status
            statusPanel.setStatus(status + state.booleanValue());   
        }
        else
        {//Other wise we only show the status
            statusPanel.setStatus(status);
        }

        statusPanel.repaint();      
        animateInfoPanel();
    }

    //Animates the bottom info panel to show for a certain amount of time, then disappears
    private void animateInfoPanel()
    {
        //Runnable thread that shows the info panel at the bottom of the aquarium and disappears after 5 seconds.
        aquariumExecutor.execute(new Runnable()
        {
                    final int PANEL_SHOW_TIME = 5000;
                    public void run()
                    {
                        //If the bottom status is not visible
                        //( The user did not show the information panel already ) then we can toggle it/Show it
                        if( !bottomStatus.isVisible() )
                        {//If no info panel
                            toggleInfoPanel();//Show the info panel

                            //Wait for a awhile
                            try
                            {
                                Thread.sleep(PANEL_SHOW_TIME);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            //After waiting, if the info panel was not manually taken away by the user, then we can get rid of it
                            if( bottomStatus.isVisible())
                            {//If we still have a visible info panel
                                toggleInfoPanel();  //Get rid of the info panel
                            }
                        }               
                    }//End method run
                }//End anonymous Runnable class
        );//End aquariumExecutor argument/invoking execute method 
    }//End animateInfoPanel

    
    //Sets the speed with a given speed as the parameter
    private void setSpeed(int userSpeed)
    {
        if( userSpeed > 0 )
        {
            userSpeed = MAX_MILL_SPEED_FISH/userSpeed;
            aquariumRunnable.setSleepTime(userSpeed);
            if( !firstRun)
            {
                setInfoStatusAndAnimate("Fish speed: " + currentSpeed, null);
            }
        }
    }

    /*
     * **************************************************************************
     *                          Toggle operation methods
     ****************************************************************************
     */

    public void toggleFishHunger()
    {
        Fish fish;
        int index = 0;
        for (; index < fishes.size(); index++)
        {
            fish = (Fish)fishes.elementAt(index);
            fish.toggleFishHunger();
        }       

        final boolean running = fishes.elementAt( index -1 ).isHungerTimerRunning();//Get a fish to see if the timer is running
        simulationRunning = running;
        Boolean state = new Boolean(running);
        setInfoStatusAndAnimate("Fish Simulation on: " , state);
    }


    public void toggleInfoPanel()
    {
        if( bottomStatus.isVisible() )
        {
            bottomStatus.setVisible( false );
        }
        else
        {
            bottomStatus.setVisible( true );
        }
        updateFishBorders();
    }


    public void toggleStats(String choice)
    {
        Fish fish;
        boolean success = false;
        for (int i = 0; i < fishes.size(); i++)
        {
            fish = (Fish)fishes.elementAt(i);
            success = fish.toggleStats(choice);
        }
        setInfoStatusAndAnimate(choice + " on: ", success);
    }

    //Toggles the aquarium menu bar off and on.real fun!
    public void toggleMenuBar()
    {
        if( getMenuBar() != null )
        {
            setMenuBar( null );
        }
        else
        {
            setMenuBar( new AquariumMenuBar( this ));//Create a new menu bar
        }
    }
    
    //Shows info for setting the fish speed
    public void showSetFishSpeed()
    {
        String results = JOptionPane.showInputDialog(this, "Min speed " + MIN_USER_FISH_SPEED + "   ||   Max speed " + MAX_USER_FISH_SPEED + "\nCurrent Speed: " + currentSpeed + "\n( Higher is faster )", "Enter speed for fish.", JOptionPane.INFORMATION_MESSAGE);

        if(results != null && results.length() >0 )
        {
            try
            {
                int speed = Integer.parseInt( results );
                //Check for a valid speed
                if( speed >0 && speed <= MAX_USER_FISH_SPEED)
                {
                    currentSpeed = speed;
                    setSpeed(currentSpeed);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Enter value between ( " + MIN_USER_FISH_SPEED + " - " + MAX_USER_FISH_SPEED + " )");
                }
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog( this , "Only numbers are allowed", "Oops" , JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Shows info to set the number of fish in the aquarium
    public void showSetFishNumber()
    {
        String results = JOptionPane.showInputDialog(this, "Min fish " + MIN_NUMBER_FISH + "   ||   Max fish " + MAX_NUMBER_FISH+ "\nCurrent fish: " + fishes.size(), "Enter number of fish.", JOptionPane.INFORMATION_MESSAGE);

        if( results != null && results.length() >0  )
        {//make sure we have a valid value from the user

            try
            {
                //Convert the string to an integer
                int fishNumber = Integer.parseInt( results );

                if( fishNumber >= 0 && fishNumber <= MAX_NUMBER_FISH )
                {
                    changeNumberOfFish( fishNumber ); 
                    setInfoStatusAndAnimate("Fish number set to: " + fishNumber, null);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Enter value between ( " + MIN_NUMBER_FISH + " - " + MAX_NUMBER_FISH + " )");
                }
            }
            catch(NumberFormatException e)
            {
                JOptionPane.showMessageDialog( this , "Only numbers are allowed", "Oops" , JOptionPane.ERROR_MESSAGE);
            }
        }
    }//End method showSetFishNumber

    
    //Shows a list of commands for the Aquarium
    public void showCommands()
    {
        String cmdList = aquariumKeyProcessor.getCommands();
        //new AquariumInfoDialog("Commands", cmdList);
    }//End method showCommansd

    
    //starts running the aquarium
    public void startRunning()
    {
        aquariumRunnable.startRunOK();
        aquariumExecutor.execute( aquariumRunnable ); //passes the object aquariumrunnable to the executor service that manages the threads
    }

    //stops running the aquarium
    public void stopRunning()
    {
        aquariumRunnable.stopRunOK();
    }

    //Toggles the program from running to stopping or vice versa
    public void toggleProgramRunning()
    {
        if( !aquariumRunnable.isRunOK() )
        {
            startRunning();         
        }
        else
        {
            stopRunning();
        }
    }

    
    //Checks to see if the aquarium is running
    //@return  Returns true if it is running or false if not
    public boolean isRunning() 
    {
        return aquariumRunnable.isRunOK(); 
    }
    
    //quits the program
    public void quitProgram()
    {
        stopRunning();
        try{
            BufferedReader buffer = new BufferedReader(new FileReader("deadFish.txt"));
            while(buffer.ready()){
                String read = buffer.readLine();
                message += read + "\n";
            }
            JOptionPane.showMessageDialog(null, message + "\n" + aquariumRunnable.getDeadFish() + " fish died during the simulation", "Fish Death Info", JOptionPane.PLAIN_MESSAGE);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
    
    //main methos. Just calls the constructor of this class
    public static void main(String[] args)
    {
       /* frame = new JFrame();
        
        frame.setSize(150, 150);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        username = new JTextField("Enter Username", 8);
        password = new JPasswordField("Enter Password", 8);
        JButton button = new JButton("GO!");
        
        user = username.getText().trim();
        pass = password.getText().trim();
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if ((username.getText().equals("AQUARIUM")) && (password.getText().equals("sweet")))
                {
                    frame.setVisible(false);
                    new Aquarium();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please enter a correct username or password", "ERROR! lol", JOptionPane.ERROR_MESSAGE);
                    System.out.println("VALUES: "+user+" "+pass);
                }
            }
        });
        panel.add(username, BorderLayout.NORTH);
        panel.add(password, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        
        
        frame.add(panel);
        frame.setVisible(true);*/
        new Aquarium();
    }

}