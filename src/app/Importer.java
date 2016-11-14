/**
 * 
 */
package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import controls.RulesItem;

/**
 * @author Therion
 *
 */
public class Importer
{
	protected static final char s_commentChar = '!';
	
	public class DataItem
	{
		public String m_name;
		public String m_value;
		public boolean m_imported;
	}
	
	public Importer()
	{
		
	}
	
	public void importConfiguration(WindowData data, String filename)
	{
		try
		{
			Window.clearStatusArea();
			Window.addMsgToStatusArea("Importing '" + filename + "'... \n");
			FileReader fstream = new FileReader(filename);
			BufferedReader out = new BufferedReader(fstream);		
			
			ArrayList<DataItem> items = new ArrayList<DataItem>();
			
			while (out.ready())
			{
				String line = out.readLine();
				if (line.length() > 0)
				{
					if (line.charAt(0) != s_commentChar)
					{
						String[] tokens = line.split("( |\t)");
						if (tokens.length >= 2)
						{
							DataItem item = new DataItem();
							item.m_name = tokens[0];
							item.m_value = tokens[1];
							item.m_imported = false;
							items.add(item);
						}
						else
						{
							System.err.println("Error: '" + line + "' not set!");
							Window.addMsgToStatusArea("Error: '" + line + "' not set! \n");
						}
					}
					else					
					{
						System.err.println("Import: Ignoring comment line '" + line + "'");
					}
				}				
			}		
			
			if (items.size() > 0)
			{
				boolean result = true;
				for (RulesItem item : data.Items)
			    {
					result &= item.importValue(items);
			    }
				
				if (!result)
				{
					Window.addMsgToStatusArea("Info: There were import errors while loading '" + filename + "'! \n");
				}
				
				boolean shownHeader = false;
				for (DataItem item : items)
				{
					if (!item.m_imported)
					{
						if (!shownHeader)
						{
							Window.addMsgToStatusArea("\nInfo: The following items were found in the import file but not set in the UI: \n");
							shownHeader = true;
						}
						
						Window.addMsgToStatusArea("Option '" + item.m_name + "' not imported! \n");
					}
				}				
			}
			
			out.close();			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage());
	    }	
	}
}
