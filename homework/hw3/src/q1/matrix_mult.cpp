#include "omp.h"

bool MatrixMult(int rowA, int colA, double* A, int rowB, int colB, double* B,
  double* C, int T) {
  // TODO: Implement your parallel multiplication for two matrices of doubles
  // by using OpenMP
  // parameter rowA: indicates the number of rows of the matrix A
  // parameter colA: indicates the number of columns of the matrix A
  // parameter A: indicates the matrix A
  // parameter rowB: indicates the number of rows of the matrix B
  // parameter colB: indicates the number of columns of the matrix B
  // parameter B: indicates the matrix B
  // parameter C: indicates the matrix C, which is the results of A x B
  // parameter T: indicates the number of threads
  // return true if A and B can be multiplied; otherwise, return false 
}

int main(int argc, const char *argv[]) {
  int ROWA;
  int COLA;
  int ROWB;
  int COLB;
  int T;

  double* A;
  double* B;

  // TODO: Initialize the necessary data

  if(MatrixMult(ROWA, COLA, A, ROWB, COLB, B, C, T)) {
    // TODO: Output the results
  } else {
    cout << "the colA != rowB MatrixMult return false" << endl;
  }
  return 0;
}
