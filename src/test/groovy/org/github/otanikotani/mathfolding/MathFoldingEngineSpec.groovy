package org.github.otanikotani.mathfolding


import spock.lang.Specification

class MathFoldingEngineSpec extends Specification {

    MathFoldingEngine engine = new MathFoldingEngine()

    def "fold single Math.abs"() {
        expect:
        engine.fold("Math.abs(3)") == "|3|"
    }

    def "fold single Math.Sqrt"() {
        expect:
        engine.fold("Math.sqrt(4)") == "√4"
    }

    def "fold Math.Sqrt with arithmetic"() {
        expect:
        engine.fold("Math.sqrt(4 + a)") == "√(4 + a)"
    }

    def "fold Math.Cbrt"() {
        expect:
        engine.fold("Math.cbrt(4)") == "∛4"
    }

    def "fold Math.Cbrt with arithmetic"() {
        expect:
        engine.fold("Math.cbrt(4 + a)") == "∛(4 + a)"
    }

    def "fold Math.pow square"() {
        expect:
        engine.fold("Math.pow(4, 2)") == "4²"
    }

    def "fold Math.pow square with arithmetic"() {
        expect:
        engine.fold("Math.pow(4 - 10, 2)") == "(4 - 10)²"
    }

    def "does not fold square float power"() {
        expect:
        engine.fold("Math.pow(4, 2.2)") == "Math.pow(4, 2.2)"
    }

    def "fold Math.pow cube"() {
        expect:
        engine.fold("Math.pow(4, 3)") == "4³"
    }

    def "fold Math.pow cube with arithmetic"() {
        expect:
        engine.fold("Math.pow(4 - 10, 3)") == "(4 - 10)³"
    }

    def "fold custom Math.pow"() {
        expect:
        engine.fold("Math.pow(4, 300)") == "4³⁰⁰"
    }

    def "fold complex Math.Sqrt"() {
        expect:
        engine.fold("Math.sqrt(3 + Math.sqrt(4))") == "√(3 + √4)"
    }

    def "fold nested Math.Sqrt"() {
        expect:
        engine.fold("Math.sqrt(Math.sqrt(4))") == "√√4"
    }

    def "fold nested Math.Sqrt with nested arithmetic"() {
        expect:
        engine.fold("Math.sqrt(Math.sqrt(4 + 4) + 5)") == "√(√(4 + 4) + 5)"
    }

    def "fold complex expressions"() {
        expect:
        engine.fold("Math.cbrt(5 + Math.pow(5, 3))") == "∛(5 + 5³)"
    }

    def "fold complex expression with Math.Sqrt"() {
        expect:
        engine.fold("Math.sqrt(Math.cbrt(5 + Math.pow(5, 3)))") == "√∛(5 + 5³)"
    }

    def "fold complex expression with Math.Abs"() {
        expect:
        engine.fold("Math.sqrt(Math.abs(10 - a) + Math.cbrt(5 + Math.pow(5, 3)))") == "√(|10 - a| + ∛(5 + 5³))"
    }
}
