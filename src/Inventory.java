class Item {
    public static final int TYPE_WEAPON = 0;
    public static final int TYPE_ARMOR = 1;
    public static final int TYPE_POTION = 2;

    private String name;
    private int type;
    private int bonusValue;

    public Item(String name, int type, int bonusValue) {
        this.name = name;
        this.type = type;
        this.bonusValue = bonusValue;
    }

    public String getName() { return name; }
    public int getType() { return type; }
    public int getBonusValue() { return bonusValue; }
}

public class Inventory {
    private static final int MAX_SLOTS = 20;
    private Item[] items;
    private int[] quantities;
    private int itemCount;

    public Inventory() {
        items = new Item[MAX_SLOTS];
        quantities = new int[MAX_SLOTS];
        itemCount = 0;
    }

    public boolean addItem(Item newItem, int qty) {
        for (int i = 0; i < itemCount; i++) {
            if (items[i] != null && items[i].getName().equals(newItem.getName())) {
                quantities[i] += qty;
                return true;
            }
        }
        if (itemCount < MAX_SLOTS) {
            items[itemCount] = newItem;
            quantities[itemCount] = qty;
            itemCount++;
            return true;
        }
        return false;
    }

    public Item getItem(int index) {
        if (index >= 0 && index < itemCount) return items[index];
        return null;
    }

    public int getQuantity(int index) {
        if (index >= 0 && index < itemCount) return quantities[index];
        return 0;
    }

    public int getItemCount() {
        return itemCount;
    }
}
