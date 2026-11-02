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
    
    // Posición en el mundo abierto (Coordenadas de mapa / tiles)
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

    // Inicializa estadísticas base según la clase elegida
    private void initClassStats() {
        switch (classType) {
            case CLASS_KNIGHT: // Tanque: Mucha vida y defensa, velocidad baja
                maxHp = 180;
                maxMp = 40;
                baseAtk = 16;
                baseDef = 22;
                baseSpd = 3;
                break;
                
            case CLASS_THIEF: // Ágil: Críticos altos, velocidad y evasión
                maxHp = 110;
                maxMp = 60;
                baseAtk = 14;
                baseDef = 10;
                baseSpd = 7;
                break;
                
            case CLASS_ARCHER: // Distancia: Daño constante, equilibrado
                maxHp = 130;
                maxMp = 50;
                baseAtk = 18;
                baseDef = 12;
                baseSpd = 5;
                break;
        }
    }

    // Método para ganar experiencia y gestionar subidas de nivel hasta 1000
    public void addExp(long amount) {
        if (level >= 1000) return; // Tope máximo de nivel alcanzado
        
        exp += amount;
        while (exp >= expNext && level < 1000) {
            exp -= expNext;
            levelUp();
        }
    }

    private void levelUp() {
        if (level >= 1000) return;
        level++;
        
        // Crecimiento de atributos personalizado por clase al subir de nivel
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
        
        // Recompensa clásica de RPG: Curación completa al subir de nivel
        hp = maxHp;
        mp = maxMp;
        
        // Curva de experiencia exponencial suave compatible con CLDC 1.1 (Math.pow)
        // Esto escala perfectamente desde el nivel 1 hasta el 1000 sin desbordar un tipo 'long'
        expNext = (long)(150 * Math.pow(level, 1.25));
    }

    // Movimiento en el mundo abierto
    public void move(int dx, int dy, int mapWidth, int mapHeight) {
        x += dx;
        y += dy;
        // Limitar dentro de los bordes del mapa si es necesario
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= mapWidth) x = mapWidth - 1;
        if (y >= mapHeight) y = mapHeight - 1;
    }

    // Getters esenciales para el renderizado y la lógica de combate
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

