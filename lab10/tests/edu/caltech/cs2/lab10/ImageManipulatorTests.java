package edu.caltech.cs2.lab10;

import edu.caltech.cs2.helpers.ImageFileSource;
import edu.caltech.cs2.helpers.Images;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ImageManipulatorTests {
    @Order(0)
    @Tag("C")
    @DisplayName("Test transpose()")
    @ParameterizedTest(name = "{0} transposed correctly")
    @ImageFileSource(
            inputs = {
                    "puppy.png",
                    "MonaLisa.png",
                    "allWhite.png",
                    "SonOfMan.jpg",
                    "onePixel.png",
                    "mario.png",
                    "SpaceElephants.jpg",
                    "guernica.jpeg"
            },
            outputFiles = {
                    "puppy.transposed.png",
                    "MonaLisa.transposed.png",
                    "allWhite.transposed.png",
                    "SonOfMan.transposed.png",
                    "onePixel.transposed.png",
                    "mario.transposed.png",
                    "SpaceElephants.transposed.png",
                    "guernica.transposed.png"
            }
    )
    public void testTranspose(String filename, BufferedImage expectedOutput) {
        File in = new File("tests/data/" + filename);
        Image original = null;
        try {
            original = new Image(in);
        } catch (IOException e) {
            fail("Input file " + filename + " could not be found.");
        }
        BufferedImage student = original.transpose().toBufferedImage();
        Images.assertImagesEqual(expectedOutput, student, 0);
    }


    @Order(1)
    @Tag("B")
    @DisplayName("Test decodeText()")
    @ParameterizedTest(name = "{0} has the message {1} hidden in it")
    @CsvSource({
            "puppy.hidden.png, abcde",
            "allWhite.hidden.png, Isn't it astonishing that all these secrets have been preserved for so many years just so we could discover them! -Orville Wright",
            "SonOfMan.hidden.png, 'Secrets, \n Secrets, \n Are No fun, \n Unless you share with everyone'",
            "onePixel.hidden.png, ''",

            "mario.hidden.png, This image is very small only 43x24 pixels which means that that there are 1032 pixels total. Therefore we're going to run out of",
            "SpaceElephants.hidden.png, Johannes Trithemius wrote Steganographia in 1499.",
            "guernica.hidden.png, The absence of a message is sometimes the presence of one!"
    })
    public void testDecodeText(String filename, String message) {
        File in = new File("tests/data/" + filename);
        Image hiddenImage = null;
        try {
            hiddenImage = new Image(in);
        } catch (IOException e) {
            fail("Input file " + filename + " could not be found.");
        }
        String hiddenMessage = hiddenImage.decodeText();

        assertEquals(message, hiddenMessage);
    }

    @Order(2)
    @Tag("A")
    @DisplayName("Test hideText()")
    @ParameterizedTest(name = "{0}")
    @ImageFileSource(
            inputs = {
                    "abcde is correctly hidden in puppy.png",
                    "Isn't it astonishing that all these secrets have been preserved for so many years just so we could discover them! -Orville Wright is correctly hidden in allWhite.png",
                    "Secrets, \n Secrets, \n Are No fun, \n Unless you share with everyone is correctly hidden in SonOfMan.jpg",
                    " is correctly hidden in onePixel.png",
                    "This image is very small only 43x24 pixels which means that that there are 1032 pixels total. Therefore we're going to run out of space! is correctly hidden in mario.png",
                    "Johannes Trithemius wrote Steganographia in 1499. is correctly hidden in SpaceElephants.jpg",
                    "The absence of a message is sometimes the presence of one! is correctly hidden in guernica.jpeg"
            },
            outputFiles = {
                    "puppy.hidden.png",
                    "allWhite.hidden.png",
                    "SonOfMan.hidden.png",
                    "onePixel.hidden.png",
                    "mario.hidden.png",
                    "SpaceElephants.hidden.png",
                    "guernica.hidden.png"
            }

    )
    public void testDecodeText(String arguments, BufferedImage expectedOutput) {
        String[] args = arguments.split(" is correctly hidden in ");
        String msg = args[0];
        String filename = args[1];

        // image that already has the message hidden in it
        File in = new File("tests/data/" + filename);
        Image startingImage = null;
        try {
            startingImage = new Image(in);
        } catch (IOException e) {
            fail("Input file " + filename + " could not be found.");
        }
        Image newImage = startingImage.hideText(msg);
        Images.assertImagesEqual(expectedOutput, newImage.toBufferedImage(), 0);
    }
}
