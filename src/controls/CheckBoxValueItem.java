package controls;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import org.w3c.dom.Element;

import app.Importer;
import app.Window;

public class CheckBoxValueItem extends ValueItem
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize(Element element, ActionListener listener)
	{
		JCheckBox checkBox = new JCheckBox(Label, Value.getBoolValue());				 
		checkBox.addActionListener(listener);				
		add(checkBox);
		ValueComponent = checkBox;
	}

	@Override
	public boolean updateValue()
	{
		String value = String.valueOf(((JCheckBox)ValueComponent).isSelected());
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
					((JCheckBox)ValueComponent).setSelected(Boolean.parseBoolean(item.m_value));
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
