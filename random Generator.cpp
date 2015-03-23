#include <iostream>
#include <stdio.h>      /* printf, scanf, puts, NULL */
#include <stdlib.h>     /* srand, rand */
#include <time.h>       /* time */
#include <iomanip>


inline void swap(int& a, int& b){
    int t = a;
    a = b;
    b = t;
}

int main() {
 /* initialize random seed: */
  srand (time(NULL));

    int numbers[100];
    numbers[0] = 0;
    for (int i=1; i<100; i++) {
        numbers[i] = numbers[i-1]+ rand()%4+1 ;
    }

    std::cout << numbers[99] <<std::endl;

    for(int i=0;i<100;i++){
        std::cout << std::hex << std::setw(2) << std::setfill('0') << numbers[i] << " ";
    }
    std::cout << std::endl;

    for(int i=99;i>=0;i--){
        std::cout << std::hex << std::setw(2) << std::setfill('0') << numbers[i] << " ";
    }
    std::cout << std::endl;


    for(int i=0;i<200;i++){
        swap( numbers[ rand()%100], numbers[rand()%100]);
    }

    for(int i=0;i<100;i++){
        std::cout << std::hex << std::setw(2) << std::setfill('0') << numbers[i] << " ";
    }
    std::cout << std::endl;
    return 0;

}

