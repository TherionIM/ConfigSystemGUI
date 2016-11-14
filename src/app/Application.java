package app;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giannis
 *
 */
public class Application
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			WindowData data = new WindowData();			
			Parser parser = new Parser();	
			List<String> messages = new ArrayList<String>();
			if (parser.load(args[0], data, messages))
			{
				Window window = new Window();				
				if (window.initialize(data))
				{
					data.actionPerformed(null);
					
					if (messages.size() > 0)
					{
						for (String msg : messages)
						{
							Window.addMsgToStatusArea(msg);
						}
					}
					
					window.setVisible(true);
				}
				else
				{
					System.out.println("Error while initializing window");
				}
			}
			else
			{
				System.out.println("Error while parsing setup xml");
			}
		}
		else
		{
			System.out.println("You need to provide a setup xml");
		}
	}

}
