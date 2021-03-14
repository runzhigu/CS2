package edu.caltech.cs2.helpers;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class Inspection {
  private static String getUsageOf(List<String> regexps, List<? extends Node> codeObjects) {
    for (Node d : codeObjects) {
      for (String regex : regexps) {
        if (d.toString().replaceAll("\\R", "").matches(".*" + regex + ".*")) {
          return regex;
        }
      }
    }
    return null;
  }

  public static void assertNoImportsOf(String filePath, List<String> regexps) {
    try {
      CompilationUnit cu = JavaParser.parse(new File(filePath));
      String usage = getUsageOf(regexps, cu.getImports());
      if (usage != null) {
        fail("You may not import " + usage + " in " + Paths.get(filePath).getFileName() + ".");
      }
    } catch (FileNotFoundException e) {
      fail("Missing Java file: " + Paths.get(filePath).getFileName());
    }
  }

  private static class ConstructorCollector extends VoidVisitorAdapter<List<ConstructorDeclaration>> {
    @Override
    public void visit(ConstructorDeclaration md, List<ConstructorDeclaration> collector) {
      super.visit(md, collector);
      collector.add(md);
    }
  }

  private static class MethodCollector extends VoidVisitorAdapter<List<MethodDeclaration>> {
    @Override
    public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
      super.visit(md, collector);
      collector.add(md);
    }
  }

  private static MethodCollector METHOD_COLLECTOR = new MethodCollector();
  private static ConstructorCollector CONSTRUCTOR_COLLECTOR = new ConstructorCollector();

  public static void assertNoUsageOf(String filePath, List<String> regexps) {
    try {
      CompilationUnit cu = JavaParser.parse(new File(filePath));

      List<ConstructorDeclaration> constructors = new ArrayList<>();
      CONSTRUCTOR_COLLECTOR.visit(cu, constructors);
      String usage = getUsageOf(regexps, constructors);
      if (usage != null) {
        fail("You may not use " + usage + " in " + Paths.get(filePath).getFileName() + ".");
      }

      List<MethodDeclaration> methods = new ArrayList<>();
      METHOD_COLLECTOR.visit(cu, methods);
      usage = getUsageOf(regexps, methods);
      if (usage != null) {
        fail("You may not use " + usage + " in " + Paths.get(filePath).getFileName() + ".");
      }
    } catch (FileNotFoundException e) {
      fail("Missing Java file: " + Paths.get(filePath).getFileName());
    }
  }

  public static void assertConstructorHygiene(String filePath) {
    try {
      CompilationUnit cu = JavaParser.parse(new File(filePath));

      // Use a Map to restrict constructor verification to per class
      Set<String> foundNonThisConstructors = new HashSet<>();
      Set<String> failedConstructors = new HashSet<>();

      List<ConstructorDeclaration> constructors = new ArrayList<>();
      CONSTRUCTOR_COLLECTOR.visit(cu, constructors);
      for (ConstructorDeclaration c : constructors) {
        BlockStmt body = c.getBody();
        List<Statement> statements = body.getStatements();
        // Nontrivial constructor, or a constructor that doesn't pass to another constructor
        if (statements.size() != 1 || !statements.get(0).toString().startsWith("this(")) {
          // If we find it twice, it's bad
          if (!foundNonThisConstructors.add(c.getNameAsString())) {
            failedConstructors.add(c.getNameAsString());
          }
        }
        if (! failedConstructors.isEmpty()) {
          fail(failedConstructors.toString() + " do not have exactly one constructor using this(...) notation.");
        }
      }

    } catch (FileNotFoundException e) {
      fail("Missing Java file: " + Paths.get(filePath).getFileName());
    }
  }
}
