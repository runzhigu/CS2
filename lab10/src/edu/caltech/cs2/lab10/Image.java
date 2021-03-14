package edu.caltech.cs2.lab10;

import edu.caltech.cs2.libraries.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.lang.Math;

public class Image {
    private Pixel[][] pixels;

    public Image(File imageFile) throws IOException {
        BufferedImage img = ImageIO.read(imageFile);
        this.pixels = new Pixel[img.getWidth()][img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                this.pixels[i][j] = Pixel.fromInt(img.getRGB(i, j));
            }
        }
    }

    private Image(Pixel[][] pixels) {
        this.pixels = pixels;
    }

    public Image transpose() {
        Pixel[][] transposePixels = new Pixel[this.pixels[0].length][this.pixels.length];

        for (int i = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                transposePixels[j][i] = this.pixels[i][j];
            }
        }

        Image transposeImage = new Image(transposePixels);
        return transposeImage;
    }

    public String decodeText() {
        // initialize
        ArrayDeque<Integer> lowestBits = new ArrayDeque<>();
        ArrayDeque<Character> message = new ArrayDeque<>();

        // iterate thru all pixels
        for (int i = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                // get lowest bit
                lowestBits.add(this.pixels[i][j].getLowestBitOfR());
                // when we get 8 bits, recover the byte value
                if (lowestBits.size() == 8) {
                    int recoveredByte = binary(lowestBits);
                    // make sure byte isn't 0
                    if (recoveredByte != 0) {
                        // cast to char
                        message.add((char) recoveredByte);
                    }
                    // clear for the next 8
                    lowestBits.clear();
                }
            }
        }
        StringBuilder messageSB = new StringBuilder();
        for (Character letter : message) {
            messageSB.append(letter);
        }

        return messageSB.toString();
    }

    private int binary(ArrayDeque<Integer> lowestBits) {
        int power = 0;
        int recoveredByte = 0;
        for (int lowestBit : lowestBits) {
            recoveredByte += Math.pow(2, power) * lowestBit;
            power++;
        }
        return recoveredByte;
    }

    public Image hideText(String text) {
        // note byteStringsSB, not byteString; will contain entire text
        StringBuilder byteStringsSB = new StringBuilder();
        // iterate through text
        for (int i = 0; i < text.length(); i++) {
            // convert to decimal int
            int byteInt = (int) text.charAt(i);
            // convert to binary string
            String reverseByteString = Integer.toBinaryString(byteInt);

            StringBuilder byteStringSB = new StringBuilder();
            // reverse the string (since we want the smallest values first)
            for (int j = reverseByteString.length() - 1; j >= 0; j--) {
                byteStringSB.append(reverseByteString.charAt(j));
            }
            String byteString = byteStringSB.toString();

            // get the leading zeros back
            if (byteString.length() < 8) {
                StringBuilder byteStringSB2 = new StringBuilder();
                byteStringSB2.append(byteString);
                byteStringSB2.append("0".repeat(8 - byteString.length()));
                byteString = byteStringSB2.toString();
            }
            byteStringsSB.append(byteString);
            }

        // byteStrings is the entire message, each character of message is 8 bits
        String byteStrings = byteStringsSB.toString();

        // populate the pixels row by row, col by col
        int counter = 0;
        for (int i = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                // make sure we know when the message ends
                if (counter < byteStrings.length()) {
                    this.pixels[i][j] = this.pixels[i][j].fixLowestBitOfR(
                            Character.getNumericValue(byteStrings.charAt(counter))
                    );
                    counter++;
                }
                // once message ends, populate rest of pixels with 0
                else {
                    this.pixels[i][j] = this.pixels[i][j].fixLowestBitOfR(0);
                }
            }
        }
        return new Image(this.pixels);
    }

    public BufferedImage toBufferedImage() {
        BufferedImage b = new BufferedImage(this.pixels.length, this.pixels[0].length, BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < this.pixels.length; i++) {
            for (int j = 0; j < this.pixels[0].length; j++) {
                b.setRGB(i, j, this.pixels[i][j].toInt());
            }
        }
        return b;
    }

    public void save(String filename) {
        File out = new File(filename);
        try {
            ImageIO.write(this.toBufferedImage(), filename.substring(filename.lastIndexOf(".") + 1, filename.length()), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
