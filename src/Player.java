public class Player {
    // Constantes de Clases
    public static final int CLASS_KNIGHT = 0;
    public static final int CLASS_THIEF = 1;
    public static final int CLASS_ARCHER = 2;

    private int classType;
    private String name;
    
    // Sistema de Niveles y Experiencia (Límite: 1000)
    private int level;
    private long exp;
    private long expNext;
    
    // Atributos de Combate
    private int hp, maxHp;
    private int mp, maxMp;
    private int baseAtk, baseDef, baseSpd;
    
    // Posición en el mundo abierto
    private int x, y;
    private int direction; // 0: Abajo, 1: Arriba, 2: Izquierda, 3: Derecha

    public Player(int classType, String name) {
        this.classType = classType;
        this.name = name;
        this.level = 1;
        this.exp = 0;
        this.expNext = 150; // Experiencia inicial para nivel 2
        
        initClassStats();
        this.hp = this.maxHp;
        this.mp = this.maxMp;
    }

    private void initClassStats() {
        switch (classType) {
            case CLASS_KNIGHT: // Tanque
                maxHp = 180;
                maxMp = 40;
                baseAtk = 16;
                baseDef = 22;
                baseSpd = 3;
                break;
                
            case CLASS_THIEF: // Ágil
                maxHp = 110;
                maxMp = 60;
                baseAtk = 14;
                baseDef = 10;
                baseSpd = 7;
                break;
                
            case CLASS_ARCHER: // Distancia
                maxHp = 130;
                maxMp = 50;
                baseAtk = 18;
                baseDef = 12;
                baseSpd = 5;
                break;
        }
    }

    public void addExp(long amount) {
        if (level >= 1000) return;
        
        exp += amount;
        while (exp >= expNext && level < 1000) {
            exp -= expNext;
            levelUp();
        }
    }

    private void levelUp() {
        if (level >= 1000) return;
        level++;
        
        switch (classType) {
            case CLASS_KNIGHT:
                maxHp += 30;
                maxMp += 6;
                baseAtk += 4;
                baseDef += 5;
                break;
            case CLASS_THIEF:
                maxHp += 18;
                maxMp += 10;
                baseAtk += 5;
                baseDef += 2;
                break;
            case CLASS_ARCHER:
                maxHp += 22;
                maxMp += 8;
                baseAtk += 6;
                baseDef += 3;
                break;
        }
        
        hp = maxHp;
        mp = maxMp;
        expNext = (long)(150 * Math.pow(level, 1.25));
    }

    public void move(int dx, int dy, int mapWidth, int mapHeight) {
        x += dx;
        y += dy;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= mapWidth) x = mapWidth - 1;
        if (y >= mapHeight) y = mapHeight - 1;
    }

    public int getLevel() { return level; }
    public long getExp() { return exp; }
    public long getExpNext() { return expNext; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getAtk() { return baseAtk + (level * 2); }
    public int getDef() { return baseDef + level; }
    public int getSpd() { return baseSpd; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public String getName() { return name; }
    public int getClassType() { return classType; }
}
