public class Localization {
    public static final int LANG_ENGLISH = 0;
    public static final int LANG_SPANISH = 1;
    public static final int LANG_VIETNAMESE = 2; // Tiếng Việt

    private static int currentLang = LANG_SPANISH;

    private static final String[][] MESSAGES = {
        { "Open World RPG", "Knight", "Thief", "Archer", "Level", "HP", "MP", "Inventory", "Options" },
        { "RPG Mundo Abierto", "Caballero", "Ladrón", "Arquero", "Nivel", "PV", "PM", "Inventario", "Opciones" },
        { "RPG The Giới Mở", "Hiệp Sĩ", "Kẻ Trộm", "Cung Thủ", "Cấp độ", "HP", "MP", "Hành Trang", "Tùy chọn" }
    };

    public static void setLanguage(int lang) {
        if (lang >= LANG_ENGLISH && lang <= LANG_VIETNAMESE) {
            currentLang = lang;
        }
    }

    public static int getCurrentLanguage() {
        return currentLang;
    }

    public static String getString(int keyId) {
        if (keyId >= 0 && keyId < MESSAGES[currentLang].length) {
            return MESSAGES[currentLang][keyId];
        }
        return "ERR";
    }

    public static final int KEY_TITLE = 0;
    public static final int KEY_KNIGHT = 1;
    public static final int KEY_THIEF = 2;
    public static final int KEY_ARCHER = 3;
    public static final int KEY_LEVEL = 4;
    public static final int KEY_HP = 5;
    public static final int KEY_MP = 6;
    public static final int KEY_INVENTORY = 7;
    public static final int KEY_OPTIONS = 8;
}
