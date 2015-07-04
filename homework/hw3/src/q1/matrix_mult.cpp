#include <omp.h>
#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <vector>
#include <string>
#include <sstream>
#include <iterator>
#include <algorithm>

using namespace std;

#define INDEX_1D_AS_2D(row, column, rowWidth) ((row * rowWidth) + column)

// parameter ROWA: indicates the number of rows of the matrix A
// parameter COLA: indicates the number of columns of the matrix A
// parameter A: indicates the matrix A
// parameter ROWB: indicates the number of rows of the matrix B
// parameter COLB: indicates the number of columns of the matrix B
// parameter B: indicates the matrix B
// parameter C: indicates the matrix C, which is the results of A x B
// parameter T: indicates the number of threads
// return true if A and B can be multiplied; otherwise, return false
bool MatrixMult(int ROWA, int COLA, double* A, int ROWB, int COLB, double* B, double *&C, int T) {
  if (COLA == ROWB) {
		C = new double[COLA * ROWB];
		
		omp_set_num_threads(T);
		#pragma omp parallel for
		for (int elementIndex = 0; elementIndex < COLA * ROWB; elementIndex++) {
			int row = elementIndex / COLA;
			int column = elementIndex % COLA;
			
			for (int inner = 0; inner < ROWB; inner++) {
				
				C[elementIndex] += A[INDEX_1D_AS_2D(row, inner, COLA)] * B[INDEX_1D_AS_2D(inner, column, COLB)];					
			}
		}
		/*
		for (int i = 0; i < ROWA; i++) {
			for (int j = 0; j < COLB; j++) {
				for (int inner = 0; inner < ROWB; inner++) {
					cout << "Multiplication operation" << endl;
					cout << "i: " << i << endl;
					cout << "j: " << j << endl;
					cout << "inner: " << inner << endl;
					cout << "A: " << A[INDEX_1D_AS_2D(i, inner, COLA)] << endl;
					cout << "B: " << B[INDEX_1D_AS_2D(inner, i, COLB)] << endl;
					C[INDEX_1D_AS_2D(i, j, COLB)] += A[INDEX_1D_AS_2D(i, inner, COLA)] * B[INDEX_1D_AS_2D(inner, j, COLB)];
					cout << "C: " << C[INDEX_1D_AS_2D(i, j, COLB)] << endl;
					cout << endl;
				}
			}
		}
		*/
		return true;
	} else {
		return false;
	}
}

void printMatrix(int numRows, int numColumns, double *matrix) {
	cout << numRows << " " << numColumns << endl;
	for (int i = 0; i < numRows; i++) {
		for (int j = 0; j < numColumns; j++) {
			cout << matrix[INDEX_1D_AS_2D(i, j, numColumns)] << " ";
		}
		cout << endl;
	}
}

vector<string> parseTokens(string line) {
	vector<string> tokens;
	stringstream ss(line);
	copy(istream_iterator<string>(ss), istream_iterator<string>(), back_inserter(tokens));

	return tokens;
}

bool parseMatrixFromFile(ifstream &matrixFile, int &numRows, int &numColumns, double *&matrix) {
	vector<string> tokens;
	string firstLine;
	if (getline(matrixFile, firstLine)) {
		tokens = parseTokens(firstLine);
		if (tokens.size() != 2) {
			cout << "Error: first line of matrix file did not contain two integers!" << endl;
			return false;
		}
		numRows = atoi(tokens[0].c_str());
		numColumns = atoi(tokens[1].c_str());
	} else {
		cout << "Error: first line of matrix file does not exist!" << endl;
		return false;
	}

	matrix = new double[numRows * numColumns];

	string nextLine;
	for (int i = 0; i < numRows; i++) {
		if (getline(matrixFile, nextLine)) {
			tokens = parseTokens(nextLine);
			if (tokens.size() != numColumns) {
				cout << "Error: number of columns for row does not match!" << endl;
				return false;
			}

			for (int j = 0; j < tokens.size(); j++) {
				matrix[INDEX_1D_AS_2D(i, j, numColumns)] = atof(tokens[j].c_str());
			}

		} else {
			cout << "Error: missing row line of matrix file!" << endl;
			return false;
		}
	}


	printMatrix(numRows, numColumns, matrix);

	return true;
}

int main(int argc, const char *argv[]) {
	int ROWA;
	int COLA;
	int ROWB;
	int COLB;
	int T;

	double *A;
	double *B;
	double *C;

	if (argc < 4) {
		cout << "USAGE: MATRIX_FILE_A MATRIX_FILE_B NUM_THREADS" << endl;
		return 1;
	}

	ifstream matrixAFile(argv[1]);

	if (matrixAFile.is_open()) {
		if (!parseMatrixFromFile(matrixAFile, ROWA, COLA, A)) {
			cout << "Error: unable to parse file for matrix A" << endl;
			return 1;
		}
	} else {
		cout << "Error: unable to open file for matrix A" << endl;
		return 1;
	}

	ifstream matrixBFile(argv[2]);
	if (matrixBFile.is_open()) {
		if (!parseMatrixFromFile(matrixBFile, ROWB, COLB, B)) {
			cout << "Error: unable to parse file for matrix B" << endl;
			return 1;
		}
	} else {
		cout << "Error: unable to open file for matrix B" << endl;
		return 1;
	}

	T = atoi(argv[3]);

	if	(MatrixMult(ROWA, COLA, A, ROWB, COLB, B, C, T)) {
		printMatrix(ROWA, COLB, C);
	} else {
		cout << "the colA != rowB MatrixMult return false" << endl;
		return 1;
	}

  return 0;
}
