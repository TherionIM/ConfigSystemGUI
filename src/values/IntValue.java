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
public class IntValue extends IValue
{
	protected int m_value;
	protected int m_min;
	protected boolean m_hasMin = false;
	protected int m_max;
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
			m_min = Integer.parseInt(element.getAttribute("min"));
			m_hasMin = true;
		}
		else
		{
			messages.add("Could not find min value for int value");
		}
		
		if (element.hasAttribute("max"))
		{
			m_max = Integer.parseInt(element.getAttribute("max"));
			m_hasMax = true;
		}
		else
		{
			messages.add("Could not find max value for int value");
		}
		
		if (element.hasAttribute("default"))
		{
			m_value = Integer.parseInt(element.getAttribute("default"));
		}
		else
		{
			messages.add("Could not find default value for int value");
		}
		
		return super.deserialize(element, messages);
	}

	@Override
	public boolean isValidValue(String value)
	{
		boolean isValid = true;
		try
		{			
			int intValue = Integer.parseInt(value);
			if (m_hasMin)
			{
				isValid &= (intValue >= m_min); 
			}
			
			if (m_hasMax)
			{
				isValid &= (intValue <= m_max); 
			}
			return isValid;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Window.addMsgToStatusArea("Invalid Integer value '" + value + "' for '" + Name + "' \n");
			isValid = false;
		}
		return isValid;
	}

	@Override
	public boolean getBoolValue()
	{
		return (m_value > 0);
	}

	@Override
	public double getDoubleValue()
	{
		return (double)m_value;
	}

	@Override
	public int getIntValue()
	{		
		return m_value;
	}

	@Override
	public boolean update(String string)
	{	
		if (isValidValue(string))
		{
			m_value = Integer.valueOf(string);
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(String value)
	{		
		return (Integer.valueOf(value) == m_value);
	}

	@Override
	public boolean lessThan(String value)
	{		
		return (Integer.valueOf(value) > m_value);
	}

	@Override
	public boolean moreThan(String value)
	{		
		return (Integer.valueOf(value) < m_value);
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
