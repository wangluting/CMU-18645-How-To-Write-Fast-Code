PROJ = matrix_mul
CC = g++
NVCC = nvcc

CFLAGS = -c -g -Wall -O3 -I/opt/local/include -I$(HOME)/cppunit/include
LDFLAGS = -L/opt/local/lib -L$(HOME)/cppunit/lib
LIBS = -lcppunit -ldl
OBJS = matrix_mul.o tests.o

all: $(PROJ)

$(PROJ): $(OBJS) $(TEST_CASES)
	$(NVCC) $(LDFLAGS) $^ -o $@ $(LIBS)

%.o : %.cu %.h
	$(NVCC) -c --ptxas-options=-v $< -o $@

%.o : %.cpp
	$(CC) $(CFLAGS) $< -o $@ 

clean:
	rm -f $(PROJ) $(OBJS) $(TEST_CASES) *.xml
