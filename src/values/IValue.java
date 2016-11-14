package values;

import java.util.List;

import org.w3c.dom.Element;

public abstract class IValue
{	
	public String Name;
	
	public boolean deserialize(Element element, List<String> messages)
	{
		if (element.hasAttribute("name"))
		{
			Name = element.getAttribute("name");
			return true;
		}
		else
		{
			messages.add("Could not find name attribute for value");
		}
		return false;
	}
	public abstract String getValue();	
	public abstract int getIntValue();
	public abstract boolean getBoolValue();
	public abstract double getDoubleValue();
	public abstract boolean isValidValue(String value);
	public abstract boolean update(String string);
	public abstract boolean equals(String value);
	public abstract boolean moreThan(String value);
	public abstract boolean lessThan(String value);	
	public abstract String getTip();
}
