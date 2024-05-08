package Interfaces;

public interface Debuggable {
    /**
     * Objektum kiírásának kezelése
     * @return - Objektum sztring reprezentációja
     */
    String debug();
    /*format:
     * ----<ITEM TYPE> <ITEM ID>----
     * <var1> : <value1>
     * <varN> : <valueN>
     * <List1> :
     *  - ListItem1
     *  - ListItem2
     *  - ListItemN
     * <ListN> :
     *  ... 
     * ----<ITEM TYPE> <ITEM ID>----
    */
}
