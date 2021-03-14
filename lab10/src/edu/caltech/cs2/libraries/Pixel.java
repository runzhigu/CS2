package edu.caltech.cs2.libraries;

/**
 * This class represents an ARGB pixel in an image.
 */
public class Pixel {
    public final int a;
    public final int r;
    public final int g;
    public final int b;

    private Pixel(int a, int r, int g, int b) {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Pixel fromInt(int argb) {
        int a = (argb & 0xFF000000) >> 24;
        int r = (argb & 0x00FF0000) >> 16;
        int g = (argb & 0x0000FF00) >> 8;
        int b = (argb & 0x000000FF) >> 0;
        return new Pixel(a, r, g, b);
    }

    public int toInt() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Returns a new pixel with the lowest bit of the red channel set to {@code lowR}.
     * @param lowR the value to set the low bit of the red channel to
     * @return an altered pixel that has the lowest bit of the red channel set to {@code lowR}
     */
    public Pixel fixLowestBitOfR(int lowR) {
        if ((lowR != 0 && lowR != 1)) {
            throw new IllegalArgumentException("lowR must represent a bit!");
        }

        int r = (this.r & 0xFE) | lowR;
        return new Pixel(this.a, r, this.g, this.b);
    }

    /**
     * Returns the LSB of the red channel in this pixel
     * @return the LSB of the red channel in this pixel
     */
    public int getLowestBitOfR() {
        return this.r & 0x1;
    }
}
