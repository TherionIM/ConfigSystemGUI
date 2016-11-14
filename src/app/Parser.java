package app;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rules.Rule;

import controls.ItemFactory;
import controls.RulesItem;

public class Parser
{
	public Parser()
	{		
	}
	
	public boolean load(String filename, WindowData data, List<String> messages)
	{		
		boolean result = true;
		try 
		{
			File file = new File(filename);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			Element rootElement = doc.getDocumentElement();
			rootElement.normalize();			
			
			data.Title = rootElement.getAttribute("title");	
			data.Execute = rootElement.getAttribute("execute");
			data.AboutStr = rootElement.getAttribute("about");
			data.OutputFilename = rootElement.getAttribute("output");
			if (rootElement.hasAttribute("timeout"))
			{
				data.Timeout = Integer.parseInt(rootElement.getAttribute("timeout")) * 1000; // make it milliseconds
			}
			  
			NodeList nodeList = rootElement.getChildNodes();
			final int count = nodeList.getLength();
			for (int s = 0; s < count; s++)
			{
				Node node = nodeList.item(s);
				if (node.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element element = (Element)node;
					if (element.getNodeName().equalsIgnoreCase("item") && element.hasAttribute("control"))
					{
						final String controlType = element.getAttribute("control");
						RulesItem item = ItemFactory.createItem(controlType);
						if (item != null)
						{
							if (!item.deserialize(element, data, messages))
							{
								messages.add("Could not deserialize item: " +  element.getNodeName());
							}			
							data.Items.add(item);
						}
						else
						{
							messages.add("Could not create item '" + controlType + "'");
						}
					}
					else if (element.getNodeName().equalsIgnoreCase("runtime_rules"))
					{
						NodeList rules = node.getChildNodes();
						for (int i=0; i<rules.getLength(); ++i)
						{
							Node rule = rules.item(i);
							if (rule.getNodeType() == Node.ELEMENT_NODE) 
							{
								Rule ruleItem = new Rule();
								if (ruleItem.deserialize((Element)rule, messages))
								{
									data.RunTimeRules.add(ruleItem);
								}
								else								
								{
									messages.add("Error: Cannot deserialize rule!");
								}
							}
						}
					}
					else
					{
						messages.add("Invalid element '" + element.getNodeName() + "'");
					}
				}
			}
		}
		catch (Exception e) 
		{
			messages.add(e.getMessage());
			e.printStackTrace();
		}		
		return result;
	}
}
