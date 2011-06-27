import java.awt.*;

public class StatusPanel extends Panel 
{
	private String status;	
	public StatusPanel(){
		setPreferredSize( new Dimension(25,25));
		status ="";	
	}	
	
	public void update (Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.green);		
	    // draw image on the screen
	    FontMetrics fm = getFontMetrics( getFont() );
		int width = fm.stringWidth( status);
		g.drawString(status, (getWidth() - width) /2, getHeight() /2);
		paint(g);	
	  }

	public void setStatus(String status)
	{		
		this.status = status;
	}
}