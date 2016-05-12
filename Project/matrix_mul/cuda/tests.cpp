/*
    tests.cpp: Testing all test cases with the cuda version of matrix multiplication 

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

#include <iostream>
#include <string.h>
#include <cppunit/extensions/HelperMacros.h>
#include <cppunit/ui/text/TestRunner.h>
#include <cppunit/TestResult.h>
#include <cppunit/TestResultCollector.h>
#include <cppunit/XmlOutputter.h>
#include "matrix_mul.h"
#include "../tests/testutil.h"

#define NUM_REPS 30


namespace cuda
{
  class Tests : public CppUnit::TestFixture, public testutil::TestFrameWork
  {
    CPPUNIT_TEST_SUITE(Tests);
    CPPUNIT_TEST(test_cases);
    CPPUNIT_TEST_SUITE_END();
        
  public:
    float *result;
    static char *filename;

    void 
    test_cases()
    {
      double start_time = 0.0f, end_time = 0.0f;
      for (int i = 0; i < TestFrameWork::size; i++)
        {
          std::cout<<"\n"<<"Test Case "<<i+1;
          result = new float[ TestFrameWork::matrix_dim[i] * TestFrameWork::matrix_dim[i] ];

          sq_matrix_1 = new float[matrix_dim[i] * matrix_dim[i]];
          sq_matrix_2 = new float[matrix_dim[i] * matrix_dim[i]];
          answers = new float[matrix_dim[i] * matrix_dim[i]];

          randomize(sq_matrix_1, TestFrameWork::matrix_dim[i], TestFrameWork::matrix_dim[i]);
          randomize(sq_matrix_2, TestFrameWork::matrix_dim[i], TestFrameWork::matrix_dim[i]);

          matrix_multiplication(TestFrameWork::sq_matrix_1, TestFrameWork::sq_matrix_2, result, TestFrameWork::matrix_dim[i]);

          tripleloop(answers, sq_matrix_1, sq_matrix_2, TestFrameWork::matrix_dim[i], TestFrameWork::matrix_dim[i], TestFrameWork::matrix_dim[i]);

          CPPUNIT_ASSERT(TestFrameWork::diff(answers, result, TestFrameWork::matrix_dim[i], TestFrameWork::matrix_dim[i]) < EPS);

	  start_time = wtime();
	  for (int j = 0; j < NUM_REPS; ++j) {
	          matrix_multiplication(TestFrameWork::sq_matrix_1, TestFrameWork::sq_matrix_2, result, TestFrameWork::matrix_dim[i]);
	  }
          end_time = wtime();

	  double t = (double)(end_time - start_time)/(double)(1000.0*NUM_REPS);

	  double gflops = (double)((2*TestFrameWork::matrix_dim[i]-1)*TestFrameWork::matrix_dim[i]*TestFrameWork::matrix_dim[i]*1e-9/t);

          std::cout<<"\t"<< gflops <<" Gflop/s\n";

          delete sq_matrix_1;
          delete sq_matrix_2;
          delete answers;
          delete result;
        }
    }

    void
    setUp()
    {
      TestFrameWork::initialize(filename);
    }
  }; // class Tests
} // namesapce cuda

char* cuda::Tests::filename = new char[100];

int 
main(int argc, char **argv) 
{
  CppUnit::TestResult controller;
  CppUnit::TestResultCollector result;
  CppUnit::TextUi::TestRunner runner;
  
  runner.addTest(cuda::Tests::suite());
  strcpy(cuda::Tests::filename, argv[2]);     
  if (argc == 3 && strcmp(argv[1], "-i") == 0) 
    {
      controller.addListener(&result);
      runner.run(controller);
      std::ofstream xmlFileOut("cuda_results.xml");
      CppUnit::XmlOutputter xmlOut(&result, xmlFileOut);
      xmlOut.write();
    }
  else if (argc == 4 && strcmp(argv[3], "-o") == 0) 
    runner.run();
  else
    std::cout<<"Usage "<<argv[0]<<" -i <test filename>\n -o";
  return 0;
}
