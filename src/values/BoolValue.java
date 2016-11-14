/**
 * 
 */
package values;

import java.util.List;

import org.w3c.dom.Element;

/**
 * @author Giannis
 *
 */
public class BoolValue extends IValue
{
	protected boolean m_value;
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
		if (element.hasAttribute("default"))
		{
			m_value = Boolean.parseBoolean(element.getAttribute("default"));
		}
		return super.deserialize(element, messages);
	}

	@Override
	public boolean isValidValue(String value)
	{		
		return true;
	}
	
	@Override
	public boolean getBoolValue()
	{
		return m_value;
	}

	@Override
	public double getDoubleValue()
	{
		return (m_value ? 1.0 : 0.0);
	}

	@Override
	public int getIntValue()
	{		
		return (m_value ? 1 : 0);
	}
	
	@Override
	public boolean update(String string)
	{	
		if (isValidValue(string))
		{
			m_value = Boolean.valueOf(string);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(String value)
	{		
		boolean input = Boolean.valueOf(value);
		return (input == m_value);
	}

	@Override
	public boolean lessThan(String value)
	{		
		return false;
	}

	@Override
	public boolean moreThan(String value)
	{		
		return false;
	}
	
	@Override
	public String getTip()
	{
		return "";		
	}
}
