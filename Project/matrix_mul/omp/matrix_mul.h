/*
    matrix_mul.h: header file for OpenMP  version of matrix multiplication

    Copyright (C) 2011  Abhinav Jauhri (abhinav.jauhri@gmail.com), Carnegie Mellon University - Silicon Valley 

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#ifndef MATRIX_MUL_H
#define MATRIX_MUL_H

namespace omp
{
/**
 * @brief Gives the product of two square matrices (of equal dimensions)
 * @param sq_matrix_1 First square matrix 
 * @param sq_matrix_2 Second square matrix
 * @param sq_matrix_result Pointer to store the resultand matrix
 * @param sq_dimension Dimension of the square matrix 
 */
  void matrix_multiplication(float *sq_matrix_1, float *sq_matrix_2, float *sq_matrix_result, unsigned int sq_dimension);
}

#endif

