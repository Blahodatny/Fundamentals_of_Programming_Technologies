package parser;

import matrix.Matrix;

public class MatrixParse {
    public static Matrix parse(String matrixString) {
        var substring = matrixString
                .substring(1, matrixString.length() - 1);

        var rows = 0;
        for (var c : substring.toCharArray())
            if (c == '[')
                rows++;

        var doubles = new double[rows][];

        for (int i = 0, index = substring.indexOf('['); index != -1; i++, index = substring.indexOf('[')) {
            var cb = substring.indexOf(']');
            var numbers = substring
                    .substring(index + 1, cb)
                    .split(",");
            doubles[i] = new double[numbers.length];
            for (var j = 0; j < numbers.length; j++)
                doubles[i][j] = Double.parseDouble(numbers[j]);
            substring = substring
                    .substring(cb + 1, substring.length());
        }

        return new Matrix(doubles);
    }
}