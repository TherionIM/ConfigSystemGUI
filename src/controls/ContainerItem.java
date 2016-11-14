/**
 * 
 */
package controls;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.Importer;

import values.IValue;

/**
 * @author Giannis
 *
 */
public class ContainerItem extends RulesItem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<RulesItem> Items = new ArrayList<RulesItem>();	
	
	/* (non-Javadoc)
	 * @see controls.RulesItem#deserialize(org.w3c.dom.Element, java.awt.event.ActionListener)
	 */
	@Override
	public boolean deserialize(Element element, ActionListener listener, List<String> messages)
	{
		// check for child items
		NodeList nodeList = element.getChildNodes();
		final int count = nodeList.getLength();
		for (int s = 0; s < count; s++)
		{
			Node node = nodeList.item(s);
			if (node.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element childElement = (Element)node;
				if (childElement.getNodeName().equalsIgnoreCase("item") && childElement.hasAttribute("control"))
				{
					final String controlType = childElement.getAttribute("control");
					RulesItem item = ItemFactory.createItem(controlType);
					if (item != null)
					{	
						if (item.deserialize(childElement, listener, messages))
						{
							Items.add(item);
						}
					}
					else
					{
						messages.add("Could not create item '" + controlType + "'");
					}
				}				
			}
		}
		return super.deserialize(element, listener, messages);
	}
	
	@Override
	public boolean importValue(ArrayList<Importer.DataItem> items)
	{	
		boolean result = true;
		if (isVisible() && isEnabled())
		{
			for (RulesItem item : Items)
			{
				result &= item.importValue(items);
			}
		}		
		return result;
	}

	@Override
	protected void initialize(Element element, ActionListener listener)
	{		
		switch (Control)
		{			
			case GROUP:	
				if (Label.length() > 0)
				{
					setBorder(new TitledBorder(Label));
				}
				else
				{
					setBorder(BorderFactory.createLineBorder(Color.black));
				}
			break;
			case TAB:		
				JScrollPane pane = new JScrollPane(this);
				HelperComponent = pane;
			break;
			default:
			break;
		}
		
		for (RulesItem item : Items)
		{
			add(item);
		}		
	}

	@Override
	public boolean isValidItem()
	{
		boolean result = true;
		for (RulesItem item : Items)
		{
			result &= item.isValidItem();
		}
		return result;
	}
	
	@Override
	public void export(BufferedWriter out) throws IOException
	{
		for (RulesItem item : Items)
		{
			item.export(out);
		}		
	}

	@Override
	public IValue findValue(String param)
	{
		for (RulesItem item : Items)
		{
			IValue value = item.findValue(param);
			if (value != null)
			{
				return value;
			}
		}
		return null;
	}

	@Override
	public void enforceRules(ArrayList<RulesItem> items)
	{
		super.checkRules(items);
		for (RulesItem item : Items)
		{
			item.enforceRules(items);
		}		
	}

	@Override
	public void setActivity(boolean valid)
	{
		for (RulesItem item : Items)
		{
			item.setActivity(valid);
		}
		setEnabled(valid);
	}

	@Override
	public void setVisibility(boolean valid)
	{
		for (RulesItem item : Items)
		{
			item.setVisibility(valid);
		}
		setVisible(valid);		
	}
}
