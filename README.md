#BOBS-Simulator

##Description
An 8085 Simulator written on Java.

##Dependencies
Install Java Development Kit before starting using the following command :

    apt-get install openjdk-7-jdk

After installing the JDK install ant for compiling the project :

    apt-get install ant

##Compiling and Running
Check out the latest sources with:

    git clone https://github.com/tnagorra/BOBS-Simulator

To compile and run the program run the following :

    ant compile jar run -Darg0='opcodefile' -Darg1='hexdatafile'

