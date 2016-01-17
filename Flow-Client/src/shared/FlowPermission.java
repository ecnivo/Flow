package shared;

import java.awt.Color;

public class FlowPermission {

    public static final byte NONE = 0;
    public static final byte VIEW = 1;
    public static final byte EDIT = 2;
    public static final byte OWNER = 3;

    private static final Color NONE_COLOUR = Color.WHITE;
    private static final Color EDIT_COLOUR = new Color(255,200,155);
    private static final Color VIEW_COLOUR = new Color(200,255,200);
    private static final Color OWNER_COLOUR = new Color(200,200,255);

    private byte permissionLevel;

    public FlowPermission(byte level) {
	permissionLevel = level;
    }

    public FlowPermission(int level) {
	permissionLevel = (byte) level;
    }

    public void setPermission(byte level) {
	permissionLevel = level;
    }

    public void setPermission(int level) {
	setPermission((byte) level);
    }

    public Color getPermissionColor() {
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
    
    public byte getPermissionLevel(){
	return permissionLevel;
    }
}
