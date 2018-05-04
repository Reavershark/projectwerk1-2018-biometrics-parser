/*
 * The MIT License
 *
 * Copyright (c) 2018 Jonas Meeuws, Jonas Van Dycke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package biometricsparser;

public class Parser {

    private static final char START_OF_TRANSMISSION = '{';
    private static final char SEPARATOR = ',';
    private static final char END_OF_TRANSMISSION = '}';

    public static BiometricData parse(String input, String name) {
        if (!isValid(input)) {
             return null;
        }

        int previousSeparator = 0;
        double[] values = new double[5];

        for (int i = 0; i < 5; i++) {
            values[i] = Double.parseDouble(input.substring(previousSeparator + 1, input.indexOf(SEPARATOR, previousSeparator + 1 )));
            previousSeparator = input.indexOf(SEPARATOR, previousSeparator + 1 );
        }

        return new BiometricData(name, values[0], new Acceleration(values[1], values[2], values[3]), values[4]);
    }

    public static boolean isValid(String input) {
        return input.indexOf(START_OF_TRANSMISSION) != -1 &&
            input.indexOf(SEPARATOR) != -1 &&
            input.indexOf(END_OF_TRANSMISSION) != -1 &&
            input.indexOf(START_OF_TRANSMISSION) < input.indexOf(SEPARATOR) &&
            input.indexOf(SEPARATOR) < input.indexOf(END_OF_TRANSMISSION);
    }
}