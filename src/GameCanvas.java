import javax.microedition.lcdui.*;

public class GameCanvas extends Canvas implements Runnable {
    private MainMIDlet midlet;
    private boolean running = false;
    private Thread thread;

    private Player player;
    private World world;
    private Enemy[] currentEnemies;

    // Sprites procedurales de 16x16
    private Image sprPlayer;
    private Image sprNormalEnemy;
    private Image sprBossEnemy;

    private static final int STATE_PLAYING = 0;
    private int gameState = STATE_PLAYING;

    public GameCanvas(MainMIDlet midlet) {
        this.midlet = midlet;
        setFullScreenMode(true);
        
        player = new Player(Player.CLASS_KNIGHT, "HeroeE72");
        player.setPosition(32, 32);

        world = new World();
        initEnemies();

        // Generar los sprites por código en memoria
        sprPlayer = createProceduralSprite(0);
        sprNormalEnemy = createProceduralSprite(1);
        sprBossEnemy = createProceduralSprite(2);
    }

    // Método que dibuja los píxeles uno por uno para armar el personaje y enemigos
    private Image createProceduralSprite(int type) {
        int width = 16;
        int height = 16;
        int[] rgbData = new int[width * height];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int color = 0x00000000; // Transparente por defecto
                
                if (type == 0) {
                    // --- JUGADOR (Caballero verde y gris) ---
                    if ((x >= 4 && x <= 11) && (y >= 2 && y <= 13)) {
                        color = 0xFF00FF80; // Cuerpo
                        if (y >= 3 && y <= 6 && x >= 5 && x <= 10) color = 0xFF303030; // Casco
                        if (y == 5 && (x == 6 || x == 9)) color = 0xFFFFFFFF; // Ojos
                    }
                } else if (type == 1) {
                    // --- ENEMIGO NORMAL (Goblin rojo) ---
                    if ((x >= 3 && x <= 12) && (y >= 4 && y <= 13)) {
                        color = 0xFFDC143C; // Rojo
                        if (y == 6 && (x == 5 || x == 10)) color = 0xFFFFFF00; // Ojos amarillos
                    }
                } else {
                    // --- JEFE / BOSS (Morado) ---
                    if ((x >= 2 && x <= 13) && (y >= 2 && y <= 14)) {
                        color = 0xFFBA55D3; // Morado
                        if (y >= 4 && y <= 7 && x >= 4 && x <= 11) color = 0xFFFFD700; // Corona
                    }
                }
                
                rgbData[index] = color;
            }
        }
        
        return Image.createRGBImage(rgbData, width, height, true);
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
        int w = getHeight() > getWidth() ? getWidth() : getWidth(); // Ancho seguro
        int h = getHeight();
        w = getWidth();

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
                        Image currentImg = sprNormalEnemy;
                        if (currentEnemies[i].getType() == Enemy.TYPE_BOSS || currentEnemies[i].getType() == Enemy.TYPE_SUB_BOSS) {
                            currentImg = sprBossEnemy;
                        }

                        if (currentImg != null) {
                            g.drawImage(currentImg, screenX, screenY, Graphics.TOP | Graphics.LEFT);
                        } else {
                            g.setColor(220, 20, 60);
                            g.fillRect(screenX + 1, screenY + 1, World.TILE_SIZE - 2, World.TILE_SIZE - 2);
                        }
                    }
                }
            }

            int playerScreenX = w / 2 - (World.TILE_SIZE / 2);
            int playerScreenY = h / 2 - (World.TILE_SIZE / 2);

            if (sprPlayer != null) {
                g.drawImage(sprPlayer, playerScreenX, playerScreenY, Graphics.TOP | Graphics.LEFT);
            } else {
                g.setColor(0, 255, 128);
                g.fillRect(playerScreenX + 1, playerScreenY + 1, World.TILE_SIZE - 2, World.TILE_SIZE - 2);
            }

            // HUD Superior
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
