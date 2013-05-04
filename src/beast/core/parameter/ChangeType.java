package beast.core.parameter;

import beast.core.Description;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Types of changes that can occcur.")
public enum ChangeType {
    POINTER_CHANGED,
    MULTIPLE_POINTERS_CHANGED,
    POINTERS_SWAPPED,
    VALUE_CHANGED,
    REMOVED,
    ADDED,
    SPLIT,
    MERGE,
    ALL,
    NONE

}
