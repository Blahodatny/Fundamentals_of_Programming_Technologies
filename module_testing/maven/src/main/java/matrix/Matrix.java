package matrix;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Math.pow;

public class Matrix {
    private double[][] matrix;

    private boolean isNull(double[][] matrix) {
        return matrix == null;
    }

    private boolean hasEmptyRow(double[][] matrix) {
        return matrix.length == 0 || Arrays.stream(matrix).anyMatch(aMatrix -> aMatrix.length == 0);
    }

    private boolean isNotMatrix(double[][] matrix) {
        return Arrays.stream(matrix).anyMatch(aMatrix -> aMatrix.length != matrix[0].length);
    }

    private boolean isSquareMatrix(double[][] matrix) {
        return matrix.length == matrix[0].length;
    }

    public Matrix(double[][] matrix) {
        setMatrix(matrix);
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        if (isNull(matrix)) throw new NullPointerException("The matrix is null!!!");
        if (hasEmptyRow(matrix)) throw new IllegalStateException("The matrix is empty or has empty row(s)!!!");
        if (isNotMatrix(matrix)) throw new IllegalArgumentException("The 2-dimensional array is not a matrix!!!");
        this.matrix = matrix;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix1 = (Matrix) o;
        return matrix[0].length == matrix1.getMatrix()[0].length
                && matrix.length == matrix1.getMatrix().length
                && Arrays.deepEquals(matrix, matrix1.getMatrix());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MATRIX={");
        for (double[] aMatrix : this.matrix) {
            builder.append(" [ ");
            for (int j = 0; j < this.matrix[0].length; j++)
                builder.append(String.format("%.2f ", aMatrix[j]));
            builder.append("] ");
        }
        builder.append("}");
        return builder.toString();
    }

    public Matrix multiply(double number) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        double[][] result = new double[rows][columns];

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) result[i][j] = matrix[i][j] * number;

        return new Matrix(result);
    }

    public Matrix multiply(Matrix mtr) {
        /*
         * multiplicand * multiplier = product
         * 4 multiplied by 3 (spoken as "3 times 4")
         * Here 3 and 4 are the "factors" and 12 is the "product".
         * */
        int aRows = matrix.length; // count of rows of matrix-multiplicand
        int aColumns = matrix[0].length; // count of columns of matrix-multiplicand
        int bRows = mtr.getMatrix().length; // count of rows of matrix-multiplier
        int bColumns = mtr.getMatrix()[0].length; // count of columns of matrix-multiplier

        if (aColumns != bRows) throw new IllegalArgumentException("Count of columns: " + aColumns +
                " of matrix-multiplicand IS NOT EQUAL TO count of rows: " + bRows +
                " of matrix-multiplier => unable to multiply!!!");

        double[][] product = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++)
            for (int j = 0; j < bColumns; j++)
                for (int k = 0; k < aColumns; k++) product[i][j] += matrix[i][k] * mtr.getMatrix()[k][j];

        return new Matrix(product);
    }

    public Matrix addMatrix(Matrix mtr) {
        /*
         * augend + addend = sum
         * */
        int rows = matrix.length;
        int columns = matrix[0].length;

        if (rows != mtr.getMatrix().length || columns != mtr.getMatrix()[0].length)
            throw new IllegalArgumentException("Unable to add matrices!!!");

        double[][] sum = new double[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) sum[i][j] = matrix[i][j] + mtr.getMatrix()[i][j];

        return new Matrix(sum);
    }

    private Matrix findMinor(Matrix mtr, int row, int column) {
        double[][] minor = new double[mtr.getMatrix().length - 1][mtr.getMatrix().length - 1];

        for (int i = 0; i < mtr.getMatrix().length; i++)
            for (int j = 0; i != row && j < mtr.getMatrix()[i].length; j++)
                if (j != column) minor[i < row ? i : i - 1][j < column ? j : j - 1] = mtr.getMatrix()[i][j];

        return new Matrix(minor);
    }

    private double findDeterminant(Matrix mtr) {
        double[][] array = mtr.getMatrix();
        if (!isSquareMatrix(array))
            throw new IllegalArgumentException("Only in square matrix determinant can be found!!!");
        if (array.length == 1) return array[0][0];
        if (array.length == 2) return array[0][0] * array[1][1] -
                array[0][1] * array[1][0];

        return IntStream.range(0, array[0].length).mapToDouble(i -> pow(-1, i) * array[0][i]
                * findDeterminant(findMinor(new Matrix(array), 0, i))).sum();
    }

    public Matrix findInvertibleMatrix() {
        // знаходимо визначник матриці
        double determinant = findDeterminant(this);
        if (determinant == 0) throw new IllegalArgumentException("The matrix is singular (degenerate) - вироджена");

        double[][] invertedMatrix = new double[matrix.length][matrix.length];

        // складаємо матрицю алгебраїчних доповнень
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[i].length; j++)
                invertedMatrix[i][j] = Math.pow(-1, i + j) * findDeterminant(findMinor(this, i, j));

        // транспонуємо матрицю та одразу ділимо на визначник
        double det = 1.0 / determinant;
        for (int i = 0; i < invertedMatrix.length; i++)
            for (int j = 0; j <= i; j++) {
                double temp = invertedMatrix[i][j];
                invertedMatrix[i][j] = invertedMatrix[j][i] * det;
                invertedMatrix[j][i] = temp * det;
            }

        return new Matrix(invertedMatrix);
    }
}