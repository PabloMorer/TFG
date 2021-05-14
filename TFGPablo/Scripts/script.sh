#!/bin/bash

export PATH=$PATH:/home/pablomorer/openmpi-2.0.1/bin

./generateLib.sh

echo library generated
cd /home/pablomorer/Desktop/ejecuciones

file=$1 #File .c to compile
libTrace=TraceLib/traceLib.o #Path/File library needed to compile
where=exe #Path/File executable generated
numProc=$2


mpicc -o $where -I/home/pablomorer/openmpi-2.0.1/include  $file $libTrace



mpiexec -hostfile machines -np numProc $where 

