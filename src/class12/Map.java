package class12;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// JavaFX Canvas
public class Map extends Canvas {

    /**
     * ゲーム終了判定インタフェース
     */
    public interface GameEndListener {
    	void gameClear();
        void gameOver();
    }

    private int cookieCount = 0;

    // マップの詳細情報
    private int mapSize; // 1マスのサイズ

    // マップのサイズ
    public final int mapWidth;
    public final int mapHeight;

    // フィールド内のキャラクターと木の位置を記録
    private Avatar[][] field;

    // ネコとイヌ
    private ArrayList<Creature> creatures;

    // 木
    private ArrayList<Tree> trees;
    private Tree obstacleTree = new Tree(null); // 外側の壁を表現するためのTree

    // クッキー
    // 可変の配列を扱える(https://docs.oracle.com/javase/jp/8/docs/api/)
    private ArrayList<Cookie> cookies;

    // キャンディ
    private ArrayList<Candy> candies;

    // サンタ
    private Santa santa;

    //ゲーム終了イベントリスナー
    private GameEndListener listener;

    // HPラベルの表示
    private boolean hpLabelVisible = true;

    public Map(int mapWidth, int mapHeight, int size, int creatureCount, int treeCount, int cheeseCount) {
        super(mapWidth * size, mapHeight * size);

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapSize = size;

        this.cookieCount = cookieCount;

        field = new Avatar[mapHeight][mapWidth];
        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                field[j][i] = null;
            }
        }

        // トナカイとコウモリをセット
        creatures = new ArrayList<Creature>();
        setCreatures(creatureCount);

        // 木をセット
        trees = new ArrayList<Tree>();
        setTrees(treeCount);

        // クッキー用のリストを初期化
        cookies = new ArrayList<Cookie>();
        setCheeses(cheeseCount);

        // キャンディ用のリストを初期化
        candies = new ArrayList<Candy>();

        // サンタをセット
        while (true) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null) {
                continue;
            }
            santa = new Santa(new Point(xx, yy));
            field[yy][xx] = santa;
            break;
        }
    }

    public void setGameEndListener(GameEndListener listener) {
        this.listener = listener;
    }

    public Avatar[][] getField() {
        return field;
    }

    public int getMapSize() {
        return mapSize;
    }

    public int getCookieCount() {
        return cookies.size();
    }

    public int getCandyCount() {
        return candies.size();
    }

    public void setHpLabelVisible(boolean hpLabelVisible) {
        this.hpLabelVisible = hpLabelVisible;
    }

    public Santa getSanta() {
        return santa;
    }

    public void setField(Avatar[][] field) {
        if (field == null) return;

        this.field = field;
        creatures = new ArrayList<Creature>();
        trees = new ArrayList<Tree>();
        cookies = new ArrayList<Cookie>();

        for (int j = 0; j < mapHeight; j++) {
            for (int i = 0; i < mapWidth; i++) {
                Avatar a = field[j][i];
                if (a instanceof Santa) {
                    santa = (Santa) a;

                } else if (a instanceof Reindeer) {
                    creatures.add((Reindeer) a);

                } else if (a instanceof Tree) {
                    trees.add((Tree) a);
                } else if (a instanceof Cookie) {
                    cookies.add((Cookie) a);
                } else if (a instanceof Bat) {
                    creatures.add((Bat) a);
                } else if (a instanceof Candy) {
                    candies.add((Candy) a);
                }
            }
        }

        paint();
    }

    public void paint() {
        GraphicsContext gc = getGraphicsContext2D();

        // 背景を描画
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // コウモリとトナカイを描画
        paintCreatures(gc, creatures);

        // 木を描画
        paintTrees(gc, trees);

        // クッキーを描画
        paintFood(gc, cookies);

        // キャンディを描画
        paintFood(gc, candies);

        // サンタを描画
        gc.drawImage(santa.image,
                santa.getPositionX() * mapSize,
                santa.getPositionY() * mapSize,
                mapSize,
                mapSize);
        gc.setFill(Color.RED);
        
        int hp = santa.getHitpoint();
        if (hp < 0) hp = 0;
        gc.fillText("" + hp,
                santa.getPositionX() * mapSize,
                santa.getPositionY() * mapSize,
                mapSize);
    }

    /**
     * 次のステップを実行する
     */
    public void nextStep() {
        // コウモリとトナカイを動かす
        moveAnimals(creatures);

        if (listener != null) {

            if (cookies.size() == 0 && santa.getHitpoint() > 0) {
                listener.gameClear();  // ゲームクリア
            } else if (santa.getHitpoint() <= 0) {
                listener.gameOver(); // ゲームオーバー
            }

        }
    }

    /**
     * サンタを動かす
     */
    public void moveSanta(int key) {
        move(santa, key);
    }

    /**
     * @param x クリックされた位置X
     * @param y クリックされた位置Y
     *          何もない場所にキャンディを置く，キャンディがクリックされたら取り除く
     */
    public void setCandy(double x, double y) {
        int positionX = (int) (x / mapSize);
        int positionY = (int) (y / mapSize);

        // キャンディを配置
        boolean canSet = field[positionY][positionX] == null;
        if (canSet) {
            Candy candy = new Candy(new Point(positionX, positionY));
            candies.add(candy);
            field[candy.getPositionY()][candy.getPositionX()] = candy;
        } else {
            if (field[positionY][positionX] instanceof Candy) {
                Candy honey = (Candy) field[positionY][positionX];
                field[honey.getPositionY()][honey.getPositionX()] = null;
                candies.remove(honey);
            }
        }
    }

    /**
     * Avatarを継承したものを画面に配置する
     */
    private void setCreatures(int count) {
        for (int i = 0; i < count; i++) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null) {
                i--;
                continue;
            }

            if (i % 2 == 0) {
                creatures.add(new Reindeer(new Point(xx, yy)));
            } else {
                creatures.add(new Bat(new Point(xx, yy)));
            }

            field[yy][xx] = creatures.get(creatures.size() - 1);
        }
    }

    /**
     * Avatarを継承したものを画面に配置する
     */
    private void setTrees(int count) {
        for (int i = 0; i < count; i++) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null) {
                i--;
                continue;
            }
            Tree tree = new Tree(new Point(xx, yy));
            trees.add(tree);
            field[yy][xx] = tree;
        }
    }

    /**
     * チーズを画面に配置する
     */
    private void setCheeses(int count) {
        for (int i = 0; i < count; i++) {
            int xx = (int) (mapWidth * Math.random());
            int yy = (int) (mapHeight * Math.random());

            if (field[yy][xx] != null) {
                i--;
                continue;
            }

            Cookie cheese = new Cookie(new Point(xx, yy));
            cookies.add(cheese);
            field[yy][xx] = cheese;
        }
    }

    /**
     * Avatarを継承したものを描画する
     */
    private void paintTrees(GraphicsContext gc, List<Tree> avatars) {
        for (int i = 0; i < avatars.size(); i++) {
            Avatar avatar = avatars.get(i);
            gc.drawImage(avatar.image,
                    avatar.getPositionX() * mapSize,
                    avatar.getPositionY() * mapSize,
                    mapSize,
                    mapSize);
        }
    }

    /**
     * Creatureを継承したものを描画する
     */
    private void paintCreatures(GraphicsContext gc, List<Creature> creatures) {
        for (int i = 0; i < creatures.size(); i++) {
            Creature creature = creatures.get(i);
            gc.drawImage(creature.image,
                    creature.getPositionX() * mapSize,
                    creature.getPositionY() * mapSize,
                    mapSize,
                    mapSize);

            if (hpLabelVisible) {
                gc.setFill(Color.RED);
                int hp = creature.getHitpoint();
                if (hp < 0) hp = 0;
                gc.fillText("" + hp,
                        creature.getPositionX() * mapSize,
                        creature.getPositionY() * mapSize,
                        mapSize);
            }
        }
    }

    /**
     * Avatarを継承したものを描画する
     */
    private void paintFood(GraphicsContext gc, List avatars) {
        for (int i = 0; i < avatars.size(); i++) {
            Avatar avatar = (Avatar) avatars.get(i);
            gc.drawImage(avatar.image,
                    avatar.getPositionX() * mapSize,
                    avatar.getPositionY() * mapSize,
                    mapSize,
                    mapSize);
        }
    }

    /**
     * Avatarを継承したAnimalを動かす
     */
    private void moveAnimals(List<Creature> animals) {
        for (int i = 0; i < animals.size(); i++) {
            int key = (int) (Math.random() * 4 + 1) * 2;
            move(animals.get(i), key);
        }
    }

    private void move(Creature creature, int key) {
        // 動物の位置を予めクリアしておく
        field[creature.position.y][creature.position.x] = null;

        Avatar avatar = nextAvatar(creature, key);
        if (avatar == null) {
            creature.move(key);

        } else if (avatar instanceof Santa) {
            Santa m = (Santa) avatar;
            m.damage();

        } else if (avatar instanceof Candy) {
            creature.move(key);
            getCandy(avatar);
            creature.heal();
        } else if (creature instanceof Santa && avatar instanceof Cookie) {
            creature.move(key);
            getCheese(avatar);
        } else if (creature instanceof Santa && (avatar instanceof Bat || avatar instanceof Reindeer)) {
            creature.damage();

        }

        // 動物の位置を記録する
        field[creature.position.y][creature.position.x] = creature;
    }

    /**
     * ターゲットのマップ移動先にあるものを返す
     *
     * @return Avatar
     */
    private Avatar nextAvatar(Avatar avatar, int key) {
        int newValue;
        switch (key) {
            case 6:
                newValue = avatar.position.x + 1;
                if (newValue < mapWidth) {
                    return field[avatar.position.y][newValue];
                }
                break;

            case 4:
                newValue = avatar.position.x - 1;
                if (0 <= newValue) {
                    return field[avatar.position.y][newValue];
                }
                break;

            case 8:
                newValue = avatar.position.y - 1;
                if (0 <= newValue) {
                    return field[newValue][avatar.position.x];
                }
                break;

            case 2:
                newValue = avatar.position.y + 1;
                if (newValue < mapHeight) {
                    return field[newValue][avatar.position.x];
                }
                break;
        }

        // 画面サイズを超えていた場合に外側の木を返す
        return obstacleTree;
    }

    /**
     * ターゲットがキャンディの上に移動した場合の処理
     */
    private void getCandy(Avatar avatar) {
        Candy candy = (Candy) field[avatar.getPositionY()][avatar.getPositionX()];
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        candies.remove(candy);
    }

    /**
     * ターゲットがクッキーの上に移動した場合の処理
     */
    private void getCheese(Avatar avatar) {
        Cookie cookie = (Cookie) field[avatar.getPositionY()][avatar.getPositionX()];
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        cookies.remove(cookie);
    }
}
