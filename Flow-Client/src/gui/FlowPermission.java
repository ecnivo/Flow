package gui;

import java.awt.Color;

public class FlowPermission {

    public static final byte NONE = 0;
    public static final byte VIEW = 1;
    public static final byte EDIT = 2;
    public static final byte OWNER = 3;

    private static final Color NONE_COLOUR = Color.WHITE;
    private static final Color EDIT_COLOUR = new Color(0xFFC46F);
    private static final Color VIEW_COLOUR = new Color(0xC8FFAD);
    private static final Color OWNER_COLOUR = new Color(606060);

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

    public boolean canChangeCollabs() {
	if (permissionLevel >= EDIT) {
	    return true;
	} else
	    return false;
    }

    public boolean canChangeOwner() {
	if (permissionLevel == OWNER)
	    return true;
	else
	    return false;
    }

    @Override
    public String toString() {
	switch (permissionLevel) {
	case NONE:
	    return "None";
	case VIEW:
	    return "Read only";
	case EDIT:
	    return "Read/write";
	case OWNER:
	    return "Owner";

	default:
	    return null;
	}
    }
}
