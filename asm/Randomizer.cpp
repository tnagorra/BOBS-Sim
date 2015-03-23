#include <iostream>
#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <stdlib.h>     /* srand, rand */
#include <time.h>       /* time */
#include <iomanip>

// Swap two numbers
inline void swap(int& a, int& b) {
    int t = a;
    a = b;
    b = t;
}

int main() {
    srand (time(NULL));

    int numbers[100];
    numbers[0] = rand()%4;
    // Add random offset to the previous value
    for (int i=1; i<100; i++)
        numbers[i] = numbers[i-1]+1+rand()%4;

    // Show the largest number
    std::cout << numbers[99] <<std::endl;

    // Swap those numbers 200 times randomly
    for(int i=0; i<200; i++) {
        swap( numbers[ rand()%100], numbers[rand()%100]);
    }

    // Print the random numbers as 8 bit hex numbers
    for(int i=0; i<100; i++)
        std::cout << std::hex << std::setw(2) << std::setfill('0') << numbers[i] << " ";
    std::cout << std::endl;

    return 0;
}


