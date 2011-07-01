package beast.core.parameter;

import beast.core.Description;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Types of changes that can occcur.")
public enum ChangeType {
    POINTER_CHANGED,
    VALUE_CHANGED,
    REMOVED,
    ADDED,
    ALL,
    NONE

}
