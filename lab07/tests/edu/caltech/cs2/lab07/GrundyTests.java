package edu.caltech.cs2.lab07;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GrundyTests {
    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private String hashGrundyUpTo(int n) {
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            GrundyPosition b = new GrundyPosition(i);
            results.add(b.isNPosition());
        }
        return sha256(results.toString());
    }

    public static Stream<Arguments> grundysGamePositions() {
        return Stream.of(
                Arguments.of(3, 1, List.of("{}")),
                Arguments.of(6, 1, List.of("{5=1}", "{4=1}")),
                Arguments.of(7, 1, List.of("{6=1}", "{5=1}", "{3=1, 4=1}")),
                Arguments.of(7, 2, List.of("{5=1}", "{4=1}", "{4=1}", "{3=1}", "{4=1}", "{3=2}")),
                Arguments.of(9, 1, List.of("{8=1}", "{7=1}", "{3=1, 6=1}", "{4=1, 5=1}"))
        );
    }

    @ParameterizedTest(name = "Test getMoves starting with heap size {0}, and making {1} moves")
    @Tag("D")
    @Order(0)
    @MethodSource("grundysGamePositions")
    @DisplayName("Test getMoves")
    public void testGetMoves(int startSize, int moves, List<String> endingPositions) {
        List<GrundyPosition> currentPositions = List.of(new GrundyPosition(startSize));
        for (int i = 0; i < moves; i++) {
            List<GrundyPosition> nextPositions = new ArrayList<>();
            for (GrundyPosition gp : currentPositions) {
                nextPositions.addAll(gp.getMoves());
            }
            currentPositions = nextPositions;
        }

        List<String> strMoves = currentPositions.stream().map(GrundyPosition::toString).collect(Collectors.toList());
        MatcherAssert.assertThat("Incorrect positions after " + moves + " moves",
                strMoves,
                IsIterableContainingInAnyOrder.containsInAnyOrder(endingPositions.toArray()));
    }

    @Test
    @Tag("D")
    @Order(1)
    @DisplayName("Test Grundy(0) to Grundy(25)")
    public void test0To25() {
        Assertions.assertEquals("c4d84ffc080ae2603869623d7837e2033ae26557da4a4b0bf97aceac70d3cbbc", hashGrundyUpTo(25));
    }

    @Test
    @Tag("A")
    @Order(0)
    @DisplayName("Test Grundy(0) to Grundy(50)")
    public void test0To60() {
        Assertions.assertEquals("c9084293995ee9cf326e065f28b85a518759fd2d7ded195f257d0f19af846b9c", hashGrundyUpTo(50));
    }
}
