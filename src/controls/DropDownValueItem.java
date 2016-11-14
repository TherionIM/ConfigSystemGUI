package controls;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.Importer;
import app.Window;

public class DropDownValueItem extends ValueItem
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize(Element element, ActionListener listener)
	{
		ArrayList<String> strings = new ArrayList<String>();
		NodeList entriesNodes = element.getElementsByTagName("entries");	
		for (int i = 0; i < entriesNodes.getLength(); i++)
		{
			Node entriesNode = entriesNodes.item(i);
			if (entriesNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element entriesNodeElement = (Element)entriesNode;
				NodeList entryNode = entriesNodeElement.getElementsByTagName("entry");	
				for (int j = 0; j < entryNode.getLength(); j++)
				{
					Node entry = entryNode.item(j);
					if (entry.getNodeType() == Node.ELEMENT_NODE) 
					{
						Element entryElement = (Element)entry;
						strings.add(entryElement.getAttribute("value"));								
					}
				}
			}
		}
		
		add(new JLabel(Label));
		JComboBox combo = new JComboBox();
		combo.addActionListener(listener);
		for (String str : strings)
		{
			combo.addItem(str);
		}
		add(combo);
		
		// set the default selected item
		boolean foundItem = false;
		int items = combo.getItemCount();
		String value = Value.getValue();
		for (int i=0; i<items; ++i)
		{
			String item = combo.getItemAt(i).toString();
			if (item.equalsIgnoreCase(value))
			{
				combo.setSelectedIndex(i);
				foundItem = true;
				break;
			}
		}		
		
		if (!foundItem)
		{
			combo.setSelectedIndex(0);
		}		
		ValueComponent = combo;
	}

	@Override
	public boolean updateValue()
	{
		String value = ((JComboBox)ValueComponent).getSelectedItem().toString();
		return Value.update(value);
	}
	
	@Override
	public boolean importValue(ArrayList<Importer.DataItem> items)
	{		
		boolean result = false;
		if (ValueComponent.isVisible() && ValueComponent.isEnabled())
		{
			for (Importer.DataItem item : items)
			{
				if (item.m_name.equalsIgnoreCase(Value.Name))
				{
					item.m_imported = true;
					((JComboBox)ValueComponent).setSelectedItem(item.m_value);
					if (updateValue())
					{					
						result = true;
					}
					else
					{
						Window.addMsgToStatusArea("Import: Cannot load '" + Value.Name + "' \n");
					}		
					break;
				}
			}
			if (!result)
			{
				Window.addMsgToStatusArea("Import: Cannot find a value for '" + Value.Name + "' \n");
			}
		}
		else
		{
			result = true;
		}
		return result;
	}

}
