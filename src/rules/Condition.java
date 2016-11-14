/**
 * 
 */
package rules;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import app.Window;

import values.IValue;

import controls.RulesItem;

/**
 * @author Giannis
 *
 */
public class Condition
{
	public enum OperatorType
	{
		EQUALS,
		NOT_EQUALS,
		MORE_THAN,
		LESS_THAN
	};
	
	public String Param;
	public OperatorType Operator = null; 
	public String Value;
	
	public Condition()
	{		
	}
	
	public void deserialize(Element element, List<String> messages)
	{	
		Param = element.getAttribute("param");
		final String operator = element.getAttribute("op");
		if (operator.equalsIgnoreCase("equals"))
		{
			Operator = OperatorType.EQUALS;
		}
		else if (operator.equalsIgnoreCase("notEquals"))
		{
			Operator = OperatorType.NOT_EQUALS;
		}
		else if (operator.equalsIgnoreCase("moreThan"))
		{
			Operator = OperatorType.MORE_THAN;
		}
		else if (operator.equalsIgnoreCase("lessThan"))
		{
			Operator = OperatorType.LESS_THAN;
		}
		else
		{
			messages.add("Could not find operator: '" + operator + "'");			
		}
		Value = element.getAttribute("value");
	}

	public boolean isValid(ArrayList<RulesItem> items)
	{	
		if (Operator != null)
		{
			IValue value = null;
			for (RulesItem item : items)
	    	{
				value = item.findValue(Param);
				if (value != null)
				{
					break;
				}
	    	}
			if (value != null)
			{
				switch(Operator)
				{
					case EQUALS: return value.equals(Value);
					case NOT_EQUALS: return !value.equals(Value);
					case MORE_THAN:	return value.moreThan(Value);			
					case LESS_THAN:	return value.lessThan(Value);			
				}
	    	}
			else		
			{
				Window.addMsgToStatusArea("\n Could not find parameter '" + Param + "' for rule!");
			}
		}
		return false;
	}
}
