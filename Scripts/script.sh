#!/bin/bash

export PATH=$PATH:/home/pablomorer/openmpi-2.0.1/bin

cd /home/pablomorer/Desktop/ejecuciones

file=$1 #Path/File .c to compile
lib=TraceLib/traceLib.c #Path/File library needed to compile
where=exe #Path/File executable generated


mpicc -o $where $file $lib


mpiexec -hostfile machines -np $2 $where 

