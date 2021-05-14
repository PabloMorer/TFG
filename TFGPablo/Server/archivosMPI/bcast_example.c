#include <stdio.h>
#include "mpi.h"
#include "traceLib.h"

#define MASTER 0

int main( int argc, char **argv){

	int rank, valor,nprocs;    

	// Inicializamos MPI y obtenemos el ID de cada proceso    
    MPI_Init_trace(&argc, &argv);
   	MPI_Comm_size_trace(MPI_COMM_WORLD, &nprocs);
    MPI_Comm_rank_trace( MPI_COMM_WORLD, &rank);
   
    // Proceso Master?
	if (rank == 0){
		printf ("Introduce un valor entero y pulsa Enter\n"); 
        valor = 5;
   	}
	
	// Broadcast del mensaje
	MPI_Bcast_trace(&valor, 1, MPI_INT, MASTER, MPI_COMM_WORLD,rank);
	
    printf( "Proceso %d recibe valor [%d]\n", rank, valor);
	
    MPI_Finalize_trace();
    return 0;
}
