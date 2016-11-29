package org.limitium.mathfolding;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathFoldingEngineTest {

    private MathFoldingEngine engine = new MathFoldingEngine();

    @Test
    public void foldsSingleMathAbs() throws Exception {
        String folded = engine.fold("Math.abs(3)");

        assertEquals("|3|", folded);
    }

    @Test
    public void foldsSingleMathSqrt() throws Exception {
        String folded = engine.fold("Math.sqrt(4)");

        assertEquals("√4", folded);
    }

    @Test
    public void foldsMathSqrtWithArithmetic() throws Exception {
        String folded = engine.fold("Math.sqrt(4 + a)");

        assertEquals("√(4 + a)", folded);
    }

    @Test
    public void foldsSingleMathCbrt() throws Exception {
        String folded = engine.fold("Math.cbrt(4)");

        assertEquals("∛4", folded);
    }

    @Test
    public void foldsMathCbrtWithArithmetic() throws Exception {
        String folded = engine.fold("Math.cbrt(4 + a)");

        assertEquals("∛(4 + a)", folded);
    }

    @Test
    public void foldsSingleSquared() throws Exception {
        String folded = engine.fold("Math.pow(4, 2)");

        assertEquals("4²", folded);
    }

    @Test
    public void doesNotFoldSquaredFloatPower() throws Exception {
        String folded = engine.fold("Math.pow(4, 2.2)");

        assertEquals("Math.pow(4, 2.2)", folded);
    }

    @Test
    public void foldsSquaredWithArithmetic() throws Exception {
        String folded = engine.fold("Math.pow(4 - 10, 2)");

        assertEquals("(4 - 10)²", folded);
    }

    @Test
    public void foldsSingleCubed() throws Exception {
        String folded = engine.fold("Math.pow(4, 3)");

        assertEquals("4³", folded);
    }

    @Test
    public void foldsSingleCustomNumericPower() throws Exception {
        String folded = engine.fold("Math.pow(4, 300)");

        assertEquals("4³⁰⁰", folded);
    }

    @Test
    public void foldsCubedWithArithmetic() throws Exception {
        String folded = engine.fold("Math.pow(4 - 10, 3)");

        assertEquals("(4 - 10)³", folded);
    }

    @Test
    public void foldsComplexMathSqrt() throws Exception {
        String folded = engine.fold("Math.sqrt(3 + Math.sqrt(4))");

        assertEquals("√(3 + √4)", folded);
    }

    @Test
    public void foldsNestedMathSqrt() throws Exception {
        String folded = engine.fold("Math.sqrt(Math.sqrt(4))");

        assertEquals("√√4", folded);
    }

    @Test
    public void foldsNestedMathSqrtWithNestedArithmetic() throws Exception {
        String folded = engine.fold("Math.sqrt(Math.sqrt(4 + 4) + 5)");

        assertEquals("√(√(4 + 4) + 5)", folded);
    }

    @Test
    public void foldsComplexExpression() throws Exception {
        String folded = engine.fold("Math.cbrt(5 + Math.pow(5, 3))");

        assertEquals("∛(5 + 5³)", folded);
    }

    @Test
    public void foldsComplexExpressionWithSqrt() throws Exception {
        String folded = engine.fold("Math.sqrt(Math.cbrt(5 + Math.pow(5, 3)))");

        assertEquals("√∛(5 + 5³)", folded);
    }

    @Test
    public void foldsComplexExpressionWithAbs() throws Exception {
        String folded = engine.fold("Math.sqrt(Math.abs(10 - a) + Math.cbrt(5 + Math.pow(5, 3)))");

        assertEquals("√(|10 - a| + ∛(5 + 5³))", folded);
    }
}
