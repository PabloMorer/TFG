#!/bin/bash

export PATH=$PATH:/home/pablomorer/openmpi-2.0.1/bin

file=$1 #File .c to compile
libTrace=$2 #Path/File library needed to compile
where=$3 #Path/File executable generated
numProc=$4
machines=$5
dest=$6

cd $dest

mpicc -o $where -I/home/pablomorer/openmpi-2.0.1/include  $file $libTrace

mpiexec -hostfile $machines -np $numProc $where 

echo Generate Trace
cat *simulation_mpi_* | sort > trace_complete.txt
echo Borrando Traces simulation_mpi_
rm *simulation_mpi_*
