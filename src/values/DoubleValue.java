/**
 * 
 */
package values;

import java.util.List;

import org.w3c.dom.Element;

import app.Window;

/**
 * @author Giannis
 *
 */
public class DoubleValue extends IValue
{
	protected double m_value;	
	protected double m_min;
	protected double m_max;
	protected boolean m_hasMin = false;	
	protected boolean m_hasMax = false;
	
	/* (non-Javadoc)
	 * @see values.IValue#getValue()
	 */
	@Override
	public String getValue()
	{
		return String.valueOf(m_value);
	}

	@Override
	public boolean deserialize(Element element, List<String> messages)
	{
		if (element.hasAttribute("min"))
		{
			m_min = Double.parseDouble(element.getAttribute("min"));
			m_hasMin = true;
		}
		else
		{
			messages.add("Could not find min value for double value");
		}
		
		if (element.hasAttribute("max"))
		{
			m_max = Double.parseDouble(element.getAttribute("max"));
			m_hasMax = true;
		}
		else
		{
			messages.add("Could not find max value for double value");
		}
		
		if (element.hasAttribute("default"))
		{
			String str = element.getAttribute("default");			
			m_value = Double.parseDouble(str);
		}
		else
		{
			messages.add("Could not find default value for double value");
		}
		
		return super.deserialize(element, messages);
	}

	@Override
	public boolean isValidValue(String value)
	{		
		boolean isValid = true;
		try
		{			
			double doubleValue = Double.parseDouble(value);
			if (m_hasMin)
			{
				isValid &= (doubleValue >= m_min); 
			}
			
			if (m_hasMax)
			{
				isValid &= (doubleValue <= m_max); 
			}
			return isValid;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Window.addMsgToStatusArea("Invalid Double value '" + value + "' for '" + Name + "' \n");
			isValid = false;
		}
		return isValid;
	}
	
	@Override
	public boolean getBoolValue()
	{
		return (m_value > 0.0);
	}

	@Override
	public double getDoubleValue()
	{
		return m_value;
	}

	@Override
	public int getIntValue()
	{		
		return (int)m_value;
	}
	
	@Override
	public boolean update(String string)
	{	
		if (isValidValue(string))
		{
			m_value = Double.valueOf(string);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(String value)
	{		
		return (Double.valueOf(value) == m_value);
	}

	@Override
	public boolean lessThan(String value)
	{		
		return (Double.valueOf(value) > m_value);
	}

	@Override
	public boolean moreThan(String value)
	{		
		return (Double.valueOf(value) < m_value);
	}

	@Override
	public String getTip()
	{
		if (m_hasMin && m_hasMax)
		{
			return m_min + " - " + m_max;
		}
		else
		{
			return "";
		}	
	}
}
