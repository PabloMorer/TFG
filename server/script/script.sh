#!/bin/bash


cd /home/pablomorer/Desktop/server/ejecuciones

mpicc -o exe $1

mpiexec -hostfile machines -np $2 exe 

rm $1
