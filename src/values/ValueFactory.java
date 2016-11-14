/**
 * 
 */
package values;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Giannis
 *
 */
public class ValueFactory
{	
	public static IValue createValue(Element element, List<String> messages)
	{
		IValue value = null;
		final NodeList itemNodes = element.getElementsByTagName("value");
		final int count = itemNodes.getLength();
		for (int i = 0; i < count; i++)
		{
			final Node itemNode = itemNodes.item(i);
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				final Element valueElement = (Element)itemNode;
				final String valueType = valueElement.getAttribute("type");
				if (valueType.compareTo("int") == 0)
				{
					value = new IntValue();			
				}
				else if (valueType.compareTo("bool") == 0)
				{
					value = new BoolValue();			
				}
				else if (valueType.compareTo("double") == 0)
				{
					value = new DoubleValue();			
				}
				else if (valueType.compareTo("string") == 0)
				{
					value = new StringValue();			
				}
				else
				{
					messages.add("Unhandled value type: '" + valueType + "'");
				}
				
				if (value != null)
				{
					value.deserialize(valueElement, messages);
					break;
				}
			}
		}
		return value;
	}
}
