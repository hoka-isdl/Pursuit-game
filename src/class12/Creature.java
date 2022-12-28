package class12;

import java.awt.Point;

class Creature extends Avatar {
    private int MAX_HP = 40;
    private int step = 0;
    private int hitpoint = 20;
    private int healCount;

    public Creature(Point p, String avatarImage) {
        super(p, avatarImage);
    }

    public int getStep() {
        return step;
    }

    public int getHitpoint() {
        return hitpoint;
    }

    public int getHealCount() {
        return healCount;
    }

    public void move(int key) {
        if (hitpoint <= 0) return;

        switch (key) {
            case 6:
                position.x++;
                break;
            case 4:
                position.x--;
                break;
            case 8:
                position.y--;
                break;
            case 2:
                position.y++;
                break;
        }

        step++;

        // 10•à‚Å‘Ì—Í1Á”ï
        if (step % 10 == 0) {
            hitpoint--;
        }
    }

    public void heal() {
        healCount++;
        hitpoint = hitpoint + 10;
        if (MAX_HP < hitpoint) hitpoint = MAX_HP;
    }

    /**
     * hitpoint‚ª0ˆÈ‰º‚É‚È‚Á‚½ê‡false‚ð•Ô‚·
     * @return
     */
    public boolean damage() {
        hitpoint = hitpoint - 2;
        return hitpoint > 0;
    }
}
