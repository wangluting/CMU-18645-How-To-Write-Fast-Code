/*
    testutil.h: header file for providing test framework for different kinds of matrix multiplication

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


#ifndef TESTUTIL_H
#define TESTUTIL_H

#include<stdio.h>
#include<sys/time.h>
#include<stdlib.h>
#include<string.h>

#include <math.h>

#define EPS 1E-3

namespace testutil
{
  class TestFrameWork
  {
  public:
    
    int size, i;
    int *matrix_dim;
    float *sq_matrix_1;
    float *sq_matrix_2;
    float *answers;
    std::ifstream file;
    
    TestFrameWork()
      {
	srand((unsigned)time(0));
	//initialize();
      }

    void randomize(float* matrix, int lines, int cols)
	{
  	    int i;

  	    for(i = 0; i < lines * cols; i++) {
    		matrix[i] = (float)rand()/(float)RAND_MAX; 
	    }
	}

    void tripleloop(float* out, float* in1, float* in2, int m, int n, int k)
	{
  	    int i, j, l;
	    float c = 0.0f;

  	    for(i = 0; i < m; i++) {
	    	for(j = 0; j< n; j++) {
      		    for(l = 0; l < k; l++) {
			c += in1[i * k + l] * in2[l * n + j];
		    }
        	    out[i * n + j] = c;
		    c = 0.0f;
		}
	    }
	}

    float diff(float* in1, float* in2, int lines, int cols)
	{
  	    int i;
  	    float a = 0;
	    int max_err = 0;

      	    for(i = 0; i < lines * cols; i++) {
    		a = fabs(in1[i] - in2[i]);
		if (a > max_err) {
		    max_err = a;
		}
	    }

  	    return max_err;
	}

	
/**
 * @brief Initializes all arrays based on the size of the testcase
 */
    void 
      initialize(char *filename)
    {
      freopen(filename, "r", stdin);
      scanf("%d\n", &size);
      matrix_dim = new int[size];
      int c, i = 0;
	
      while (i < size && (scanf("%d\n", &c) != EOF)) {
	  matrix_dim[i++] = c;
	}

	size = i;
      fclose(stdin);
    }

   
/**
 * @brief Checks whether two arrays have same values or not
 * 
 * @param first1 pointer to the first element in the first array
 * @param last1 pointer to the last element in the first array
 * @param first2 pointer to the first element in the second array
 * 
 * @return boolean value stating whether the two arrays are equal or not 
 */
   bool 
     equals(float *first1, float *last1, float *first2)
   {
     while( first1 != last1)
       {
	 if (*first1 != *first2) {
	   return false;
	  }
	 first1++;
	 first2++;
       }
     return true;
   }

 
/**
 * @brief Calculates the current time in milliseconds
 * @return double type value of the current time
 */  
   double wtime(void) 
   {
     double          now_time;
     struct timeval  etstart;
     
     if (gettimeofday(&etstart, NULL) == -1)
       perror("Error: calling gettimeofday() not successful.\n");
     
     now_time = ((etstart.tv_sec) * 1000 +     
		  etstart.tv_usec / 1000.0);  
     return now_time;
   }
   
   
   ~TestFrameWork()
     {
       delete matrix_dim;
     }
  }; // class Testing
  
} // namespace testutil

#endif
