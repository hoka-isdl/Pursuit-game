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
     * �Q�[���I������C���^�t�F�[�X
     */
    public interface GameEndListener {
    	void gameClear();
        void gameOver();
    }

    private int cookieCount = 0;

    // �}�b�v�̏ڍ׏��
    private int mapSize; // 1�}�X�̃T�C�Y

    // �}�b�v�̃T�C�Y
    public final int mapWidth;
    public final int mapHeight;

    // �t�B�[���h���̃L�����N�^�[�Ɩ؂̈ʒu���L�^
    private Avatar[][] field;

    // �l�R�ƃC�k
    private ArrayList<Creature> creatures;

    // ��
    private ArrayList<Tree> trees;
    private Tree obstacleTree = new Tree(null); // �O���̕ǂ�\�����邽�߂�Tree

    // �N�b�L�[
    // �ς̔z���������(https://docs.oracle.com/javase/jp/8/docs/api/)
    private ArrayList<Cookie> cookies;

    // �L�����f�B
    private ArrayList<Candy> candies;

    // �T���^
    private Santa santa;

    //�Q�[���I���C�x���g���X�i�[
    private GameEndListener listener;

    // HP���x���̕\��
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

        // �g�i�J�C�ƃR�E�������Z�b�g
        creatures = new ArrayList<Creature>();
        setCreatures(creatureCount);

        // �؂��Z�b�g
        trees = new ArrayList<Tree>();
        setTrees(treeCount);

        // �N�b�L�[�p�̃��X�g��������
        cookies = new ArrayList<Cookie>();
        setCheeses(cheeseCount);

        // �L�����f�B�p�̃��X�g��������
        candies = new ArrayList<Candy>();

        // �T���^���Z�b�g
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

        // �w�i��`��
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // �R�E�����ƃg�i�J�C��`��
        paintCreatures(gc, creatures);

        // �؂�`��
        paintTrees(gc, trees);

        // �N�b�L�[��`��
        paintFood(gc, cookies);

        // �L�����f�B��`��
        paintFood(gc, candies);

        // �T���^��`��
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
     * ���̃X�e�b�v�����s����
     */
    public void nextStep() {
        // �R�E�����ƃg�i�J�C�𓮂���
        moveAnimals(creatures);

        if (listener != null) {

            if (cookies.size() == 0 && santa.getHitpoint() > 0) {
                listener.gameClear();  // �Q�[���N���A
            } else if (santa.getHitpoint() <= 0) {
                listener.gameOver(); // �Q�[���I�[�o�[
            }

        }
    }

    /**
     * �T���^�𓮂���
     */
    public void moveSanta(int key) {
        move(santa, key);
    }

    /**
     * @param x �N���b�N���ꂽ�ʒuX
     * @param y �N���b�N���ꂽ�ʒuY
     *          �����Ȃ��ꏊ�ɃL�����f�B��u���C�L�����f�B���N���b�N���ꂽ���菜��
     */
    public void setCandy(double x, double y) {
        int positionX = (int) (x / mapSize);
        int positionY = (int) (y / mapSize);

        // �L�����f�B��z�u
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
     * Avatar���p���������̂���ʂɔz�u����
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
     * Avatar���p���������̂���ʂɔz�u����
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
     * �`�[�Y����ʂɔz�u����
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
     * Avatar���p���������̂�`�悷��
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
     * Creature���p���������̂�`�悷��
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
     * Avatar���p���������̂�`�悷��
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
     * Avatar���p������Animal�𓮂���
     */
    private void moveAnimals(List<Creature> animals) {
        for (int i = 0; i < animals.size(); i++) {
            int key = (int) (Math.random() * 4 + 1) * 2;
            move(animals.get(i), key);
        }
    }

    private void move(Creature creature, int key) {
        // �����̈ʒu��\�߃N���A���Ă���
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

        // �����̈ʒu���L�^����
        field[creature.position.y][creature.position.x] = creature;
    }

    /**
     * �^�[�Q�b�g�̃}�b�v�ړ���ɂ�����̂�Ԃ�
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

        // ��ʃT�C�Y�𒴂��Ă����ꍇ�ɊO���̖؂�Ԃ�
        return obstacleTree;
    }

    /**
     * �^�[�Q�b�g���L�����f�B�̏�Ɉړ������ꍇ�̏���
     */
    private void getCandy(Avatar avatar) {
        Candy candy = (Candy) field[avatar.getPositionY()][avatar.getPositionX()];
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        candies.remove(candy);
    }

    /**
     * �^�[�Q�b�g���N�b�L�[�̏�Ɉړ������ꍇ�̏���
     */
    private void getCheese(Avatar avatar) {
        Cookie cookie = (Cookie) field[avatar.getPositionY()][avatar.getPositionX()];
        field[avatar.getPositionY()][avatar.getPositionX()] = null;
        cookies.remove(cookie);
    }
}
