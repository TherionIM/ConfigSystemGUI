/**
 * 
 */
package rules;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import controls.RulesItem;

/**
 * @author Giannis
 *
 */
public class Rule
{
	public enum RuleType
	{
		VISIBLE,
		ENABLE
	};
	
	public ArrayList<Condition> Conditions = new ArrayList<Condition>();
	public RuleType Type;
	public String Msg = "";

	public Rule()
	{	 
	}
	
	public boolean deserialize(Element element, List<String> messages)
	{	
		final String ruleType = element.getAttribute("type");
		if (ruleType.equalsIgnoreCase("enable"))
		{
			Type = RuleType.ENABLE;
		}
		else if (ruleType.equalsIgnoreCase("visible"))
		{
			Type = RuleType.VISIBLE;
		}
		else
		{
			messages.add("Could not deserialize rule with type '" + ruleType + "'");
			return false;
		}
		
		Msg = element.getAttribute("msg");
		final NodeList nodeList = element.getChildNodes();
		final int count = nodeList.getLength();
		for (int s = 0; s < count; s++)
		{
			final Node node = nodeList.item(s);
			if (node.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element childElement = (Element)node;
				final String name = childElement.getNodeName();
				if (name.equalsIgnoreCase("condition"))
				{
					Condition condition = new Condition();
					condition.deserialize(childElement, messages);
					Conditions.add(condition);
				}
			}
		}
		return (Conditions.size() > 0);
	}
	
	public boolean isValid(ArrayList<RulesItem> items)
	{
		boolean isValid = true;
		for (Condition condition : Conditions)
		{
			isValid &= condition.isValid(items);
		}
		return isValid; 
	}
}
