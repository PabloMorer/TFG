#!/bin/bash
echo Generating Library
gcc -I/Users/bryanrvvargas/openmpi-2.0.1/include -Wall -c traceLib.c 
echo Copy in Programas/
cp traceLib.o Programas/
cp traceLib.h Programas/