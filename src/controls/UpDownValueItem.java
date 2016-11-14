package controls;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.w3c.dom.Element;

import app.Importer;
import app.Window;

public class UpDownValueItem extends ValueItem
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected boolean m_isDecimal;
	
	protected double m_incrementDouble;
	protected double m_minDouble;
	protected double m_maxDouble;
	
	protected int m_incrementInt;
	protected int m_minInt;
	protected int m_maxInt;
	
	@Override
	public boolean deserialize(Element element, ActionListener listener, List<String> messages)
	{		
		m_isDecimal = element.getAttribute("decimal").equalsIgnoreCase("true") ? true : false;
		if (m_isDecimal)
		{
			m_incrementDouble = Double.valueOf(element.getAttribute("increment"));
			m_minDouble = Double.valueOf(element.getAttribute("min"));		
			m_maxDouble = Double.valueOf(element.getAttribute("max"));
		}
		else
		{
			m_incrementInt = Integer.valueOf(element.getAttribute("increment"));
			m_minInt = Integer.valueOf(element.getAttribute("min"));		
			m_maxInt = Integer.valueOf(element.getAttribute("max"));
		}
		return super.deserialize(element, listener, messages);
	}

	@Override
	protected void initialize(Element element, ActionListener listener)
	{
		add(new JLabel(Label));			
		SpinnerNumberModel model = new SpinnerNumberModel();
		if (m_isDecimal)
		{
			model.setMaximum(m_maxDouble);
			model.setMinimum(m_minDouble);
			model.setStepSize(m_incrementDouble);
			model.setValue(Value.getDoubleValue());
		}
		else
		{
			model.setMaximum(m_maxInt);
			model.setMinimum(m_minInt);
			model.setStepSize(m_incrementInt);
			model.setValue(Value.getIntValue());			
		}	
		
		JSpinner spinner = new JSpinner(model);		
		ValueComponent = spinner;
		add(spinner);
	}

	@Override
	public boolean updateValue()
	{
		String value = ((JSpinner)ValueComponent).getValue().toString();		
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
					try
					{
						if (m_isDecimal)
						{
							((JSpinner)ValueComponent).setValue(Double.parseDouble(item.m_value));
						}
						else
						{
							((JSpinner)ValueComponent).setValue(Integer.parseInt(item.m_value));
						}
					}
					catch (Exception e) 
					{
						Window.addMsgToStatusArea("Import: Illegal value '" + item.m_value + "' for '" + Value.Name + "'  \n");
						e.printStackTrace();
					}
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
