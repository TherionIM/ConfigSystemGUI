/**
 * 
 */
package controls;

/**
 * @author Giannis
 *
 */
public class ItemFactory
{
	public static RulesItem createItem(String tag)
	{
		RulesItem result = null;
		if (tag.equalsIgnoreCase("tab") 	|| 
			tag.equalsIgnoreCase("group")	)
		{
			result = new ContainerItem();
		}
		else if (tag.equalsIgnoreCase("textbox"))
		{
			return new TextBoxValueItem();
		}
		else if (tag.equalsIgnoreCase("dropdown"))
		{
			return new DropDownValueItem();			
		}
		else if (tag.equalsIgnoreCase("checkbox"))
		{
			return new CheckBoxValueItem();
		}
		else if (tag.equalsIgnoreCase("file"))
		{
			return new BrowseFileValueItem();
		}
		else if (tag.equalsIgnoreCase("updown"))		
		{
			result = new UpDownValueItem();
		}
		return result;
	}
}
