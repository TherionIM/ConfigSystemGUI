/**
 * 
 */
package controls;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.Importer;

import rules.Rule;
import values.IValue;

/**
 * @author Giannis
 *
 */
public abstract class RulesItem extends JPanel
{
	public enum ControlType
	{
		TEXTBOX,
		DROPDOWN,
		CHECKBOX,
		BROWSEFILE,
		UPDOWN,
		GROUP,
		TAB,
		
		TOTAL
	};
	
	public static final String[] ControlTypeStr = new String[]
	{
		"textbox", "dropdown", "checkbox", "file", "updown", "group", "tab"
	};
	
	public ControlType Control;
	public Component HelperComponent = null;
	public String Label;		
	public int Size;	
	public String Id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ArrayList<HashMap<Rule.RuleType, ArrayList<Rule>>> Rules = new ArrayList<HashMap<Rule.RuleType, ArrayList<Rule>>>(); 
	
	protected void deserializeRules(Element element, List<String> messages)
	{ 		
		HashMap<Rule.RuleType, ArrayList<Rule>> rules = new HashMap<Rule.RuleType, ArrayList<Rule>>();
		NodeList nodeList = element.getChildNodes();
		final int count = nodeList.getLength();
		for (int s = 0; s < count; s++)
		{
			Node node = nodeList.item(s);
			if (node.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element childElement = (Element)node;
				final String name = childElement.getNodeName();
				if (name.equalsIgnoreCase("rule"))
				{
					Rule rule = new Rule();
					if (rule.deserialize(childElement, messages))
					{
						if (rules.containsKey(rule.Type))
						{
							rules.get(rule.Type).add(rule);
						}
						else
						{
							ArrayList<Rule> list = new ArrayList<Rule>();
							list.add(rule);
							rules.put(rule.Type, list);						
						}
					}
					else
					{
						messages.add("Error: Cannot deserialize rule item!");	
					}
				}
			}
		}
		Rules.add(rules);
	}
	
	protected ControlType getControlType(String attribute)
	{
		for (int i=0; i<ControlType.TOTAL.ordinal(); ++i)
		{
			if (ControlTypeStr[i].equalsIgnoreCase(attribute))
			{
				return ControlType.class.getEnumConstants()[i];
			}
		}		
		System.out.println("Unhandled control type: " + attribute);		
		return ControlType.TEXTBOX;
	}
	
	protected void checkRules(ArrayList<RulesItem> items)
	{		
		boolean isVisible = true;
		boolean foundVisibleRules = false;
		for (HashMap<Rule.RuleType, ArrayList<Rule>> RuleMap : Rules)
		{
			if (RuleMap.containsKey(Rule.RuleType.VISIBLE))
			{
				ArrayList<Rule> visibleRulesList = RuleMap.get(Rule.RuleType.VISIBLE);
				if (visibleRulesList.size() > 0)
				{
					foundVisibleRules = true;
					boolean isValid = false;
					for (Rule rule : visibleRulesList)
					{
						isValid |= rule.isValid(items);			
					}				
					isVisible &= isValid;		
				}
			}
			
			if (foundVisibleRules)
			{
				setVisibility(isVisible);
			}	
		}
		
		boolean isEnabled = true;
		boolean foundEnableRules = false;
		for (HashMap<Rule.RuleType, ArrayList<Rule>> RuleMap : Rules)
		{
			if (RuleMap.containsKey(Rule.RuleType.ENABLE))
			{
				ArrayList<Rule> enableRulesList = RuleMap.get(Rule.RuleType.ENABLE);
				if (enableRulesList.size() > 0)
				{
					foundEnableRules = true;
					boolean isValid = false;
					for (Rule rule : enableRulesList)
					{
						isValid |= rule.isValid(items);			
					}	
					isEnabled &= isValid;
				}
			}
			
			if (foundEnableRules)
			{
				setActivity(isEnabled);
			}
		}
		getParent().repaint();			
	}	
	
	public boolean deserialize(Element element, ActionListener listener, List<String> messages)
	{
		boolean result = true;	
		
		if (element.getNodeName().equalsIgnoreCase("item"))
		{
			Control = getControlType(element.getAttribute("control"));		
			Label = element.getAttribute("label");
			Id = element.getAttribute("id");
			if (element.hasAttribute("size"))
			{
				Size = Integer.parseInt(element.getAttribute("size"));
			}
			if (element.hasAttribute("orientation"))
			{
				if (element.getAttribute("orientation").equalsIgnoreCase("horizontal"))
				{
					setLayout(new FlowLayout());
				}
				else				
				{
					setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				}
			}
			else
			{
				setLayout(new FlowLayout());
			}		
			
			// Initialise the element
			initialize(element, listener);
			
			NodeList nodeList = element.getChildNodes();
			for (int s = 0; s < nodeList.getLength(); s++)
			{
				Node node = nodeList.item(s);
				if (node.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element childElement = (Element)node;
					if (childElement.getNodeName().equalsIgnoreCase("rules"))
					{
						deserializeRules(childElement, messages);
					}
				}
			}			
		}
			
		return result;
	}
		
	public abstract void export(BufferedWriter out) throws IOException;		
	public abstract boolean importValue(ArrayList<Importer.DataItem> items);
	protected abstract void initialize(Element element, ActionListener listener);
	public abstract boolean isValidItem();
	public abstract IValue findValue(String param);		
	public abstract void enforceRules(ArrayList<RulesItem> items);	
	public abstract void setVisibility(boolean valid);
	public abstract void setActivity(boolean valid);		
	
}
