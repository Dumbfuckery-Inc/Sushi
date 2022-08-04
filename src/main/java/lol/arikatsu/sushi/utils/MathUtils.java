package lol.arikatsu.sushi.utils;

public final class MathUtils {
    private MathUtils() {
        // This class is not meant to be instantiated.
    }

    /**
     * Formats a timestamp to a readable format.
     * Format: 1d 2h 3m 4s
     * @param time The time to format.
     * @return The formatted timestamp.
     */
    public static String formatStopwatch(long time) {
        // Do math.
        long days = time / 86400000;
        long hours = (time % 86400000) / 3600000;
        long minutes = (time % 3600000) / 60000;
        long seconds = (time % 60000) / 1000;

        // Format the time.
        return "%dd %dh %dm %ds".formatted(days, hours, minutes, seconds);
    }

    /**
     * Returns a random number between the given minimum and maximum.
     * @param min The minimum number.
     * @param max The maximum number.
     * @return The random number.
     */
    public static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * Evaluates a given expression.
     * Sourced from: <a href="https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form">...</a>
     * @param str The expression to evaluate.
     * @return The result of the expression.
     * @throws RuntimeException if the expression is invalid.
     */
    public static double evaluate(final String str) throws RuntimeException {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    x = switch (func) {
                        case "sqrt" -> Math.sqrt(x);
                        case "sin" -> Math.sin(Math.toRadians(x));
                        case "cos" -> Math.cos(Math.toRadians(x));
                        case "tan" -> Math.tan(Math.toRadians(x));
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}