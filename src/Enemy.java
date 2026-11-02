public class Enemy {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SUB_BOSS = 1;
    public static final int TYPE_BOSS = 2;

    private String name;
    private int type;
    private int level;
    private int hp, maxHp;
    private int attack;
    private int defense;
    private long expReward;
    private int goldReward;
    private int x, y;
    private boolean isAlive;

    public Enemy(String name, int type, int level, int baseHp, int baseAtk, int baseDef, long expReward, int goldReward, int startX, int startY) {
        this.name = name;
        this.type = type;
        this.level = level;
        
        double multiplier = 1.0;
        if (type == TYPE_SUB_BOSS) multiplier = 2.5;
        else if (type == TYPE_BOSS) multiplier = 5.0;

        this.maxHp = (int)(baseHp + (level * 15 * multiplier));
        this.hp = this.maxHp;
        this.attack = (int)(baseAtk + (level * 3 * multiplier));
        this.defense = (int)(baseDef + (level * 2 * multiplier));
        this.expReward = (long)(expReward * multiplier);
        this.goldReward = (int)(goldReward * multiplier);
        this.x = startX;
        this.y = startY;
        this.isAlive = true;
    }

    public boolean takeDamage(int damage) {
        int actualDamage = damage - defense;
        if (actualDamage < 1) actualDamage = 1;
        hp -= actualDamage;
        if (hp <= 0) {
            hp = 0;
            isAlive = false;
        }
        return !isAlive;
    }

    public void moveTowards(int targetX, int targetY) {
        if (!isAlive) return;
        if (x < targetX) x++;
        else if (x > targetX) x--;
        if (y < targetY) y++;
        else if (y > targetY) y--;
    }

    public String getName() { return name; }
    public int getType() { return type; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public long getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAlive() { return isAlive; }
}
