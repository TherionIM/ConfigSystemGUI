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
public class StringValue extends IValue
{
	protected String m_value;
	/* (non-Javadoc)
	 * @see values.IValue#deserialize(org.w3c.dom.Element)
	 */
	@Override
	public boolean deserialize(Element element, List<String> messages)
	{
		if (element.hasAttribute("default"))
		{
			m_value = element.getAttribute("default");
		}
		return super.deserialize(element, messages);
	}

	/* (non-Javadoc)
	 * @see values.IValue#getBoolValue()
	 */
	@Override
	public boolean getBoolValue()
	{	
		return Boolean.valueOf(m_value);
	}

	/* (non-Javadoc)
	 * @see values.IValue#getDoubleValue()
	 */
	@Override
	public double getDoubleValue()
	{	
		return Double.valueOf(m_value);
	}

	/* (non-Javadoc)
	 * @see values.IValue#getIntValue()
	 */
	@Override
	public int getIntValue()
	{		
		return Integer.valueOf(m_value);
	}

	/* (non-Javadoc)
	 * @see values.IValue#getValue()
	 */
	@Override
	public String getValue()
	{	
		return m_value;
	}

	/* (non-Javadoc)
	 * @see values.IValue#isValidValue(java.lang.String)
	 */
	@Override
	public boolean isValidValue(String value)
	{
		return true;
	}
	
	@Override
	public boolean update(String string)
	{	
		if (isValidValue(string))
		{
			m_value = string;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(String value)
	{		
		return value.equalsIgnoreCase(m_value);
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
