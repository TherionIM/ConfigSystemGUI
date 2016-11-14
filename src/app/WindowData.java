package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import rules.Rule;

import controls.RulesItem;

public class WindowData implements ActionListener
{		
	public String Version = "1.8.2";
	public String Title = "Test";
	public String Execute = "";
	public String AboutStr = "";
	public String OutputFilename = "output.txt";
	public int Timeout = 5 * 1000;
	public ArrayList<RulesItem> Items = new ArrayList<RulesItem>();	
	public ArrayList<Rule> RunTimeRules = new ArrayList<Rule>();
	
	@Override
	public void actionPerformed(ActionEvent event)
	{	
		for (RulesItem item : Items)
	    {			
	    	item.enforceRules(Items);
	    }		
	}
}
