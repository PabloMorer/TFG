#!/bin/bash
echo Generating Library
gcc -I/home/pablomorer/openmpi-2.0.1/include -Wall -c traceLib/traceLib.c 
cp traceLib.o /home/pablomorer/Desktop/ejecuciones/TraceLib

