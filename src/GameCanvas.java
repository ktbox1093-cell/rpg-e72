import javax.microedition.lcdui.*;

public class GameCanvas extends Canvas implements Runnable {
    private MainMIDlet midlet;
    private boolean running = false;
    private Thread thread;

    private Player player;
    private World world;
    private Enemy[] currentEnemies;

    private static final int STATE_PLAYING = 0;
    private int gameState = STATE_PLAYING;

    public GameCanvas(MainMIDlet midlet) {
        this.midlet = midlet;
        setFullScreenMode(true);
        
        player = new Player(Player.CLASS_KNIGHT, "HeroeE72");
        player.setPosition(32, 32);

        world = new World(getWidth(), getHeight());
        initEnemies();
    }

    private void initEnemies() {
        currentEnemies = new Enemy[4];
        currentEnemies[0] = new Enemy("Goblin", Enemy.TYPE_NORMAL, 1, 40, 12, 4, 30, 10, 34, 32);
        currentEnemies[1] = new Enemy("Lobo Salvaje", Enemy.TYPE_NORMAL, 3, 70, 16, 7, 60, 15, 28, 30);
        currentEnemies[2] = new Enemy("Sub-Jefe Orco", Enemy.TYPE_SUB_BOSS, 10, 200, 30, 15, 250, 80, 40, 35);
        currentEnemies[3] = new Enemy("Dragon Ancestral", Enemy.TYPE_BOSS, 50, 1000, 80, 40, 2500, 1000, 50, 50);
    }

    public void startThread() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stopThread() {
        running = false;
        thread = null;
    }

    public void run() {
        while (running) {
            updateLogic();
            repaint();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                // Manejar excepción
            }
        }
    }

    private void updateLogic() {
        if (gameState != STATE_PLAYING) return;

        for (int i = 0; i < currentEnemies.length; i++) {
            if (currentEnemies[i] != null && currentEnemies[i].isAlive()) {
                int dx = Math.abs(currentEnemies[i].getX() - player.getX());
                int dy = Math.abs(currentEnemies[i].getY() - player.getY());
                
                if (dx < 7 && dy < 7) {
                    currentEnemies[i].moveTowards(player.getX(), player.getY());
                    
                    if (currentEnemies[i].getX() == player.getX() && currentEnemies[i].getY() == player.getY()) {
                        boolean dead = currentEnemies[i].takeDamage(player.getAtk());
                        if (dead) {
                            player.addExp(currentEnemies[i].getExpReward());
                        }
                    }
                }
            }
        }
    }

    protected void keyPressed(int keyCode) {
        int action = getGameAction(keyCode);
        int pX = player.getX();
        int pY = player.getY();

        if (gameState == STATE_PLAYING) {
            if (action == UP || keyCode == KEY_NUM2 || keyCode == '8') {
                if (world.isWalkable(pX, pY - 1)) player.move(0, -1, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (action == DOWN || keyCode == KEY_NUM8 || keyCode == '2') {
                if (world.isWalkable(pX, pY + 1)) player.move(0, 1, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (action == LEFT || keyCode == KEY_NUM4 || keyCode == '4') {
                if (world.isWalkable(pX - 1, pY)) player.move(-1, 0, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (action == RIGHT || keyCode == KEY_NUM6 || keyCode == '6') {
                if (world.isWalkable(pX + 1, pY)) player.move(1, 0, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (keyCode == KEY_NUM1 || keyCode == '1') {
                int nextLang = (Localization.getCurrentLanguage() + 1) % 3;
                Localization.setLanguage(nextLang);
            }
        }
    }

    protected void paint(Graphics g) {
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (gameState == STATE_PLAYING) {
            world.render(g, player.getX(), player.getY(), 0, 0);

            for (int i = 0; i < currentEnemies.length; i++) {
                if (currentEnemies[i] != null && currentEnemies[i].isAlive()) {
                    int screenX = (currentEnemies[i].getX() - player.getX()) * World.TILE_SIZE + (getWidth() / 2);
                    int screenY = (currentEnemies[i].getY() - player.getY()) * World.TILE_SIZE + (getHeight() / 2);

                    if (screenX >= -World.TILE_SIZE && screenX < getWidth() && screenY >= -World.TILE_SIZE && screenY < getHeight()) {
                        if (currentEnemies[i].getType() == Enemy.TYPE_BOSS) {
                            g.setColor(255, 215, 0);
                        } else if (currentEnemies[i].getType() == Enemy.TYPE_SUB_BOSS) {
                            g.setColor(186, 85, 211);
                        } else {
                            g.setColor(220, 20, 60);
                        }
                        g.fillRect(screenX + 1, screenY + 1, World.TILE_SIZE - 2, World.TILE_SIZE - 2);
                    }
                }
            }

            int playerScreenX = getWidth() / 2;
            int playerScreenY = getHeight() / 2;
            g.setColor(0, 255, 128);
            g.fillRect(playerScreenX + 1, playerScreenY + 1, World.TILE_SIZE - 2, World.TILE_SIZE - 2);

            g.setColor(0, 0, 0);
            g.fillRect(0, 0, getWidth(), 20);
            
            g.setColor(255, 255, 255);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            
            String hudText = Localization.getString(Localization.KEY_LEVEL) + " " + player.getLevel() + 
                             " | " + Localization.getString(Localization.KEY_HP) + ": " + player.getHp() + "/" + player.getMaxHp();
            g.drawString(hudText, 4, 2, Graphics.TOP | Graphics.LEFT);

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("[1] Lang", getWidth() - 50, 2, Graphics.TOP | Graphics.LEFT);
        }
    }
}
