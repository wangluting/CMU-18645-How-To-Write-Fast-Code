/*
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


#include <cuda.h>
#include <cuda_runtime.h>
#include "matrix_mul.h"
#include <math.h>
#define TILE_WIDTH 32


namespace cuda
{
  __global__ 
  void 
  matrix_mul_kernel1(float *sq_matrix_1, float *sq_matrix_2, float *sq_matrix_result, int sq_dimension)
  {
    
	int col = blockIdx.x*blockDim.x + threadIdx.x;
	int row = blockIdx.y*blockDim.y + threadIdx.y;

    float sum = 0;
    
	if(col < sq_dimension && row < sq_dimension) {
    	for(int k = 0; k < sq_dimension; k++)
      	{
			sum += sq_matrix_1[row*sq_dimension + k] * sq_matrix_2[k*sq_dimension + col];
      	}
    	sq_matrix_result[row*sq_dimension + col] = sum;
	}
  
  }

  __global__
  void
  matrix_mul_kernel2(float *sq_matrix_1, float *sq_matrix_2, float *sq_matrix_result, int sq_dimension)
  {

    __shared__ float matA[TILE_WIDTH][TILE_WIDTH];
    __shared__ float matB[TILE_WIDTH][TILE_WIDTH];
    
	int bx = blockIdx.x;
	int by = blockIdx.y;
    int tx = threadIdx.x;
	int ty = threadIdx.y;

    int col = bx*TILE_WIDTH + tx;
    int row = by*TILE_WIDTH + ty;
   
    float sum = 0;    
	int index1, index2;

    for(int m = 0; m < gridDim.x; ++m) {
		
		index1 = m*TILE_WIDTH+tx;
		index2 = m*TILE_WIDTH+ty;		
		if(row < sq_dimension && index1 < sq_dimension) {
        	matA[ty][tx] = sq_matrix_1[row*sq_dimension + index1];
		}
		else {
			matA[ty][tx] = 0;
		}
		if(col < sq_dimension && index2 < sq_dimension) {
        	matB[ty][tx] = sq_matrix_2[index2*sq_dimension + col];
		}
		else {
			matB[ty][tx] = 0;
		}
		__syncthreads();

//		#pragma unroll
        for(int k = 0; k < TILE_WIDTH; ++k) {
            sum += matA[ty][k]*matB[k][tx];  
        }
        __syncthreads();
    }
    
	if(row < sq_dimension && col < sq_dimension)
    	sq_matrix_result[row*sq_dimension + col] = sum;

  }

  __global__
  void
  matrix_mul_kernel3(float *sq_matrix_1, float *sq_matrix_2, float *sq_matrix_result, int sq_dimension) //loop unrolling
  { 
    __shared__ float matA[TILE_WIDTH][TILE_WIDTH];
    __shared__ float matB[TILE_WIDTH][TILE_WIDTH];
    
	int bx = blockIdx.x;
	int by = blockIdx.y;
    int tx = threadIdx.x;
	int ty = threadIdx.y;

    int col = bx*TILE_WIDTH + tx;
    int row = by*TILE_WIDTH + ty;
   
    float sum = 0;    
	int index1, index2;

    for(int m = 0; m < gridDim.x; ++m) {
		
		index1 = m*TILE_WIDTH+tx;
		index2 = m*TILE_WIDTH+ty;		
		if(row < sq_dimension && index1 < sq_dimension) {
        	matA[ty][tx] = sq_matrix_1[row*sq_dimension + index1];
		}
		else {
			matA[ty][tx] = 0;
		}
		if(col < sq_dimension && index2 < sq_dimension) {
        	matB[ty][tx] = sq_matrix_2[index2*sq_dimension + col];
		}
		else {
			matB[ty][tx] = 0;
		}
		__syncthreads();


        sum += matA[ty][0]*matB[0][tx] + matA[ty][1]*matB[1][tx] + matA[ty][2]*matB[2][tx] + matA[ty][3]*matB[3][tx] + matA[ty][4]*matB[4][tx] +
		      matA[ty][5]*matB[5][tx] + matA[ty][6]*matB[6][tx] + matA[ty][7]*matB[7][tx] + matA[ty][8]*matB[8][tx] + matA[ty][9]*matB[9][tx] +
			  matA[ty][10]*matB[10][tx] + matA[ty][11]*matB[11][tx] + matA[ty][12]*matB[12][tx] + matA[ty][13]*matB[13][tx] + matA[ty][14]*matB[14][tx] +
			  matA[ty][15]*matB[15][tx] + matA[ty][16]*matB[16][tx] + matA[ty][17]*matB[17][tx] + matA[ty][18]*matB[18][tx] + matA[ty][19]*matB[19][tx] +
			  matA[ty][20]*matB[20][tx] + matA[ty][21]*matB[21][tx] + matA[ty][22]*matB[22][tx] + matA[ty][23]*matB[23][tx] + matA[ty][24]*matB[24][tx] +
			  matA[ty][25]*matB[25][tx] + matA[ty][26]*matB[26][tx] + matA[ty][27]*matB[27][tx] + matA[ty][28]*matB[28][tx] + matA[ty][29]*matB[29][tx] +
			  matA[ty][30]*matB[30][tx] + matA[ty][31]*matB[31][tx];

        __syncthreads();
    }
    
	if(row < sq_dimension && col < sq_dimension)
    	sq_matrix_result[row*sq_dimension + col] = sum;
  
  }
  
   __global__
  void
  matrix_mul_kernel4(float *sq_matrix_1, float *sq_matrix_2, float *sq_matrix_result, int sq_dimension) // use traverse and avoid shared memory bank conflict
  {

    __shared__ float matA[TILE_WIDTH][TILE_WIDTH];
    __shared__ float matB[TILE_WIDTH][TILE_WIDTH];  //add one column to avoid bank conflict
    
	int bx = blockIdx.x;
	int by = blockIdx.y;
    int tx = threadIdx.x;
	int ty = threadIdx.y;

    int col = bx*TILE_WIDTH + tx;
    int row = by*TILE_WIDTH + ty;
   
    float sum = 0;    
	int index1, index2;

    for(int m = 0; m < gridDim.x; ++m) {
		
		index1 = m*TILE_WIDTH+tx;
		index2 = m*TILE_WIDTH+ty;		
		if(row < sq_dimension && index1 < sq_dimension) {
        	matA[ty][tx] = sq_matrix_1[row*sq_dimension + index1];
		}
		else {
			matA[ty][tx] = 0;
		}
		if(col < sq_dimension && index2 < sq_dimension) {
        	matB[tx][ty] = sq_matrix_2[index2*sq_dimension + col];
		}
		else {
			matB[tx][ty] = 0;
		}
		__syncthreads();

		//#pragma unroll
        for(int k = 0; k < TILE_WIDTH; ++k) {
            sum += matA[ty][k]*matB[tx][k];  
        }
        __syncthreads();
    }
    
	if(row < sq_dimension && col < sq_dimension)
    	sq_matrix_result[row*sq_dimension + col] = sum;

  }


  void 
  matrix_multiplication(float *sq_matrix_1, float *sq_matrix_2, float *sq_matrix_result, unsigned int sq_dimension)
  {
    
	int choice = 1;   // choose the method

	int size = sq_dimension * sq_dimension * sizeof(float);
    float *sq_matrix_1_d, *sq_matrix_2_d, *sq_matrix_result_d;
    
    /***************************************************
  1st Part: Allocation of memory on device memory  
    ****************************************************/
    
    /* copy sq_matrix_1 and sq_matrix_2 to device memory */
    cudaMalloc((void**) &sq_matrix_1_d, size);
    cudaMemcpy(sq_matrix_1_d, sq_matrix_1, size, cudaMemcpyHostToDevice);
    cudaMalloc((void**) &sq_matrix_2_d, size);
    cudaMemcpy(sq_matrix_2_d, sq_matrix_2, size, cudaMemcpyHostToDevice);
    
    /*allocate sq_matrix_result on host */
    cudaMalloc((void**) &sq_matrix_result_d, size);
    
    /***************************************************
   2nd Part: Inovke kernel 
    ****************************************************/
	if(choice == 0)  {    //use the block with global memory  
		if(sq_dimension <= TILE_WIDTH) {
			dim3 dimBlock(sq_dimension, sq_dimension);
			dim3 dimGrid(1,1);
			matrix_mul_kernel1<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d,sq_dimension);		
		}
		else {
			dim3 dimBlock(TILE_WIDTH, TILE_WIDTH);
			int blockNum = ceil(sq_dimension*1.0/TILE_WIDTH);
			dim3 dimGrid(blockNum, blockNum);
			matrix_mul_kernel1<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);
		}
  
	}

	else if(choice == 1) {  //use the block with shared memory
		if(sq_dimension <= TILE_WIDTH) {
        	dim3 dimBlock(sq_dimension, sq_dimension);
			dim3 dimGrid(1,1);
    		matrix_mul_kernel2<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);    		
    	}
    	else {
			dim3 dimBlock(TILE_WIDTH, TILE_WIDTH);
			int blockNum = ceil(sq_dimension*1.0/TILE_WIDTH);
    		dim3 dimGrid(blockNum,blockNum);
    		matrix_mul_kernel2<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);
    	}
	}
	
	else if(choice == 2) {  //use loop unrolling
		if(sq_dimension <= TILE_WIDTH) {
        	dim3 dimBlock(sq_dimension, sq_dimension);
			dim3 dimGrid(1,1);
    		matrix_mul_kernel3<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);    		
    	}
    	else {
			dim3 dimBlock(TILE_WIDTH, TILE_WIDTH);
			int blockNum = ceil(sq_dimension*1.0/TILE_WIDTH);
    		dim3 dimGrid(blockNum,blockNum);
    		matrix_mul_kernel3<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);
    	}
	}
	
	else if(choice == 3) {  //use traverse
		if(sq_dimension <= TILE_WIDTH) {
        	dim3 dimBlock(sq_dimension, sq_dimension);
			dim3 dimGrid(1,1);
    		matrix_mul_kernel4<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);    		
    	}
    	else {
			dim3 dimBlock(TILE_WIDTH, TILE_WIDTH);
			int blockNum = ceil(sq_dimension*1.0/TILE_WIDTH);
    		dim3 dimGrid(blockNum,blockNum);
    		matrix_mul_kernel4<<<dimGrid, dimBlock>>>(sq_matrix_1_d, sq_matrix_2_d, sq_matrix_result_d, sq_dimension);
    	}
	}
    
    /***************************************************
   3rd Part: Transfer result from device to host 
    ****************************************************/
    cudaMemcpy(sq_matrix_result, sq_matrix_result_d, size, cudaMemcpyDeviceToHost);
    cudaFree(sq_matrix_1_d);
    cudaFree(sq_matrix_2_d);
    cudaFree(sq_matrix_result_d);
  }  
} // namespace cuda
