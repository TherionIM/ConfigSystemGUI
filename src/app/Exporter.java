package app;

import java.io.BufferedWriter;
import java.io.FileWriter;

import controls.RulesItem;

public class Exporter
{
	public Exporter()
	{	
	}
	
	public boolean isValid(WindowData data)
	{
		boolean result = true;
		for (RulesItem item : data.Items)
	    {
			result &= item.isValidItem();    		
	    }
		return result;
	}
	
	public boolean export(WindowData data, String filename)
	{
		try
		{
		    // Create file 
		    FileWriter fstream = new FileWriter((filename.length() > 0) ? filename : data.OutputFilename);
		    BufferedWriter out = new BufferedWriter(fstream);
		    for (RulesItem item : data.Items)
		    {
		    	item.export(out);
		    }	
		    
		    //Close the output stream
		    out.close();	
		    fstream.close();
		    return true;
	    }
		catch (Exception e)
		{
			Window.addMsgToStatusArea(e.getMessage());
			System.err.println("Error: " + e.getMessage());			
	    }		
		return false;
	}	
}
