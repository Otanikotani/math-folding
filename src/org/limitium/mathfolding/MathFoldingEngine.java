package org.limitium.mathfolding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathFoldingEngine {

    private static final Pattern MATH_EXPR = Pattern.compile("(.*)Math\\.(.+?)\\)(.*)");

    enum Type {
        ABS("Math.abs") {
            @Override
            String fold(String expr) {
                return "|" + trimBrackets(expr) + "|";
            }
        },
        SQRT("Math.sqrt") {
            @Override
            String fold(String expr) {
                if (hasArithmetic(expr)) {
                    return "√" + expr;
                } else {
                    return "√" + trimBrackets(expr);
                }
            }
        },
        CBRT("Math.cbrt") {
            @Override
            String fold(String expr) {
                if (hasArithmetic(expr)) {
                    return "∛" + expr;
                } else {
                    return "∛" + trimBrackets(expr);
                }
            }
        },
        POW("Math.pow") {
            @Override
            String fold(String expr) {
                String[] args = expr.split(",");
                if (args.length == 2) {
                    String power = args[1].trim().replace(")", "");
                    if (power.matches("\\d+")) {
                        String upperScriptPower = toUpperScript(power);
                        if (hasArithmetic(expr)) {
                            String arg = args[0] + ")";
                            return arg + upperScriptPower;
                        } else {
                            String arg = args[0].substring(1);
                            return arg + upperScriptPower;
                        }
                    } else {
                        return "Math.pow" + expr;
                    }
                } else {
                    return "Math.pow" + expr;
                }
            }
        };

        private static String toUpperScript(String power) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < power.length(); i++) {
                char c = power.charAt(i);
                result.append(Superscripts.get(c));
            }
            return result.toString();
        }

        abstract String fold(String expr);

        private final String expr;

        Type(String expr) {
            this.expr = expr;
        }
    }

    String fold(final String text) {
        if (null == text) {
            return null;
        }

        Type type = isMathExpression(text);
        if (null == type) {
            return text;
        }

        String exprCall = text.substring(type.expr.length(), text.length());
        if (!hasMathExpression(exprCall)) {
            return type.fold(exprCall);
        } else {
            Matcher matcher = MATH_EXPR.matcher(trimBrackets(exprCall));
            if (matcher.find()) {
                String subExpression = "Math." + matcher.group(2) + ")";
                String enclosing = matcher.group(3);
                String subExpressionFolded = fold(subExpression);
                return fold(type.expr + "(" + (matcher.group(1) + subExpressionFolded + enclosing + ")"));
            }
        }
        return "";
    }

    private static String trimBrackets(String expr) {
        return expr.substring(1, expr.length() - 1);
    }

    private static Type isMathExpression(String text) {
        for (Type type: Type.values()) {
            if (text.startsWith(type.expr)) {
                return type;
            }
        }
        return null;
    }

    private static boolean hasMathExpression(String text) {
        for (Type type: Type.values()) {
            if (text.contains(type.expr)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasArithmetic(String formula) {
        int inSubProcedure = 0;
        String trimmed = trimBrackets(formula);
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == '(') {
                inSubProcedure++;
            } else if (c == ')') {
                inSubProcedure--;
            }
            if ((inSubProcedure == 0) && (c == '+' || c == '-' || c == '/' || c == '*')) {
                return true;
            }
        }
        return false;
    }


    private static class Superscripts {
        private static final char[] numbers = {'⁰', '¹', '²', '³', '⁴', '⁵', '⁶', '⁷', '⁸', '⁹'};

        private static char get(char digit) {
            return numbers[digit - 48];
        }
    }
}
