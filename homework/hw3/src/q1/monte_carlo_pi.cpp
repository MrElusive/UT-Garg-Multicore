#include <omp.h>
#include <stdio.h>
#include <stdlib.h>

#define randomFloatBetweenZeroAndOne() (rand() / static_cast<float>(RAND_MAX))

double MonteCarloPi(int totalPoints) {
	const double R = 1.0;
	const double R_squared = R * R;
		
	int pointsInCircle = 0;
	
	#pragma omp parallel for reduction(+:pointsInCircle)
	for (int i = 0; i < totalPoints; i++) {
		double x = randomFloatBetweenZeroAndOne();
		double y = randomFloatBetweenZeroAndOne();
			
		if ((x * x + y * y) < R_squared) {
			pointsInCircle++;
		}		
	}	
	
	return 4 * (pointsInCircle / (double) totalPoints);
}

/*
int main(int argc, char* argv[]) {
	if (argc >= 2) {
		printf("PI: %f\n", MonteCarloPi(atoi(argv[1])));
	} else {
		printf("USAGE: %s TOTAL_POINTS", argv[0]);
	}
	
}
*/