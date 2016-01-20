
package shared;

import java.awt.*;

/**
 * Represents a permission level for a user
 * 
 * @author Vince Ou
 *
 */
public class FlowPermission {

	// Permission levels
	public static final byte	NONE			= 0;
	public static final byte	VIEW			= 1;
	public static final byte	EDIT			= 2;
	public static final byte	OWNER			= 3;

	// Colours
	private static final Color	NONE_COLOUR		= Color.WHITE;
	private static final Color	EDIT_COLOUR		= new Color(255, 200, 155); // orange
	private static final Color	VIEW_COLOUR		= new Color(200, 255, 200); // green
	private static final Color	OWNER_COLOUR	= new Color(200, 200, 255); // blurple

	private byte				permissionLevel;

	/**
	 * Creates a new FlowPermission (with byte)
	 * 
	 * @param level
	 *        permission
	 */
	public FlowPermission(byte level) {
		permissionLevel = level;
	}

	/**
	 * Creates a new FlowPermission (with int)
	 * 
	 * @param level
	 *        permission level
	 */
	public FlowPermission(int level) {
		permissionLevel = (byte) level;
	}

	/**
	 * Sets a permission level (byte)
	 * 
	 * @param level
	 *        level
	 */
	public void setPermission(byte level) {
		permissionLevel = level;
	}

	/**
	 * Sets a permission level (int)
	 * 
	 * @param level
	 *        level
	 */
	public void setPermission(int level) {
		setPermission((byte) level);
	}

	/**
	 * Gets the colour for this permission level
	 *
	 * @return the color for this permission level
	 */
	public Color getPermissionColor() {
		// Pretty self explanatoryS
		switch (permissionLevel) {
			case NONE:
				return NONE_COLOUR;
			case EDIT:
				return EDIT_COLOUR;
			case VIEW:
				return VIEW_COLOUR;
			case OWNER:
				return OWNER_COLOUR;
			default:
				return null;
		}
	}

	/**
	 * Gets the text-representation of a permission level
	 */
	@Override
	public String toString() {
		switch (permissionLevel) {
			case NONE:
				return "None";
			case VIEW:
				return "View (read only)";
			case EDIT:
				return "Edit (read/write)";
			case OWNER:
				return "Owner";

			default:
				return null;
		}
	}

	/**
	 * Gets the byte-representation of a permission level
	 * @return the level
	 */
	public byte getPermissionLevel() {
		return permissionLevel;
	}
}
