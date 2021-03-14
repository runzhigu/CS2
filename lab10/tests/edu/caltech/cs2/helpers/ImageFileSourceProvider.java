package edu.caltech.cs2.helpers;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.util.Preconditions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;


public class ImageFileSourceProvider implements ArgumentsProvider, AnnotationConsumer<ImageFileSource> {
  private String[] inputs;
  private String[] outputFiles;


  @Override
  public void accept(ImageFileSource source) {
    this.inputs = source.inputs();
    this.outputFiles = source.outputFiles();
  }

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    Arguments[] args = new Arguments[this.outputFiles.length];
    for (int i = 0; i < this.outputFiles.length; i++) {
      String inputArgs = this.inputs[i];
      args[i] = Arguments.arguments(inputArgs, Images.getImage("tests/data/" + this.outputFiles[i]));
    }
    return Stream.of(args);
  }


}
