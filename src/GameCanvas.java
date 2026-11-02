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

        world = new World();
        initEnemies();
    }

    private void initEnemies() {
        currentEnemies = new Enemy[4];
        currentEnemies[0] = new Enemy("Goblin", Enemy.TYPE_NORMAL, 1, 40, 12, 4, 30, 10, 34, 32);
        currentEnemies[1] = new Enemy("Lobo", Enemy.TYPE_NORMAL, 3, 70, 16, 7, 60, 15, 28, 30);
        currentEnemies[2] = new Enemy("Orco", Enemy.TYPE_SUB_BOSS, 10, 200, 30, 15, 250, 80, 36, 32);
        currentEnemies[3] = new Enemy("Dragon", Enemy.TYPE_BOSS, 50, 1000, 80, 40, 2500, 1000, 50, 50);
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
            try { Thread.sleep(40); } catch (InterruptedException e) {}
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
                        if (dead) player.addExp(currentEnemies[i].getExpReward());
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
            // Lee acción estándar o códigos directos de navegación de Symbian (-1, -2, -3, -4)
            if (action == UP || keyCode == -1 || keyCode == '2') {
                if (world.isWalkable(pX, pY - 1)) player.move(0, -1, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (action == DOWN || keyCode == -2 || keyCode == '8') {
                if (world.isWalkable(pX, pY + 1)) player.move(0, 1, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (action == LEFT || keyCode == -3 || keyCode == '4') {
                if (world.isWalkable(pX - 1, pY)) player.move(-1, 0, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (action == RIGHT || keyCode == -4 || keyCode == '6') {
                if (world.isWalkable(pX + 1, pY)) player.move(1, 0, World.MAP_WIDTH, World.MAP_HEIGHT);
            } else if (keyCode == KEY_NUM1 || keyCode == '1') {
                Localization.setLanguage((Localization.getCurrentLanguage() + 1) % 3);
            }
        }
    }

    protected void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, w, h);

        if (gameState == STATE_PLAYING) {
            world.render(g, player.getX(), player.getY(), w, h);

            int cameraX = player.getX() * World.TILE_SIZE - (w / 2) + (World.TILE_SIZE / 2);
            int cameraY = player.getY() * World.TILE_SIZE - (h / 2) + (World.TILE_SIZE / 2);

            for (int i = 0; i < currentEnemies.length; i++) {
                if (currentEnemies[i] != null && currentEnemies[i].isAlive()) {
                    int screenX = (currentEnemies[i].getX() * World.TILE_SIZE) - cameraX;
                    int screenY = (currentEnemies[i].getY() * World.TILE_SIZE) - cameraY;

                    if (screenX >= -World.TILE_SIZE && screenX < w && screenY >= -World.TILE_SIZE && screenY < h) {
                        if (currentEnemies[i].getType() == Enemy.TYPE_BOSS) g.setColor(255, 215, 0);
                        else if (currentEnemies[i].getType() == Enemy.TYPE_SUB_BOSS) g.setColor(186, 85, 211);
                        else g.setColor(220, 20, 60);
                        
                        g.fillRect(screenX + 1, screenY + 1, World.TILE_SIZE - 2, World.TILE_SIZE - 2);
                    }
                }
            }

            int playerScreenX = w / 2 - (World.TILE_SIZE / 2);
            int playerScreenY = h / 2 - (World.TILE_SIZE / 2);
            g.setColor(0, 255, 128);
            g.fillRect(playerScreenX + 1, playerScreenY + 1, World.TILE_SIZE - 2, World.TILE_SIZE - 2);

            g.setColor(0, 0, 0);
            g.fillRect(0, 0, w, 20);
            
            g.setColor(255, 255, 255);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            
            String hudText = Localization.getString(Localization.KEY_LEVEL) + " " + player.getLevel() + 
                             " | " + Localization.getString(Localization.KEY_HP) + ": " + player.getHp() + "/" + player.getMaxHp();
            g.drawString(hudText, 4, 2, Graphics.TOP | Graphics.LEFT);

            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("[1] Lang", w - 50, 2, Graphics.TOP | Graphics.LEFT);
        }
    }
}
