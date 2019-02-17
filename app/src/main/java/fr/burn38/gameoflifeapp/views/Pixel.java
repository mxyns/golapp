package fr.burn38.gameoflifeapp.views;

class Pixel {

    private int x, y, c;

    Pixel(int x, int y, int c) {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    int x() {
        return this.x;
    }

    int y() {
        return this.y;
    }

    int color() {
        return this.c;
    }
}
