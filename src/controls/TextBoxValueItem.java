package controls;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Element;

import app.Importer;
import app.Window;

public class TextBoxValueItem extends ValueItem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize(Element element, ActionListener listener)
	{	
		add(new JLabel(Label));		
		JTextField field = new JTextField(Value.getValue(), Size);
		field.setHorizontalAlignment(JTextField.RIGHT);
		field.addActionListener(listener);								
		add(field);
		ValueComponent = field;				
	}

	@Override
	public boolean updateValue()
	{
		String value = ((JTextField)ValueComponent).getText();
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
					((JTextField)ValueComponent).setText(item.m_value);
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
