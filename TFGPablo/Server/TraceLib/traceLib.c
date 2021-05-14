/**
	@file traceLib.h
	@brief Library's implementation that implements I/O and MPI functions in order to generate useful traces for study


	@author Bryan Raúl Vaca Vargas and Alberto Nuñez Covarrubias
	@date 02/2018
*/

#include "traceLib.h"
#include <sys/time.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>

#define DEBUG 1 
#define SIZE_OF_LIST	1000/*!< Initial size of list  */

struct taskInfo *FIRST;		/*!< Pointer to the first task in the list */
struct taskInfo *LAST; 		/*!< Pointer to the last task in the list */
struct timeval TIME_STAMP_INITIAL; /*!< Worker's initial time stamp in timeval format */

double TIME_INITIAL; /*!< Worker's initial time stamp in decimal format */
int COUNT_ID; /*!< Worker id counter */

void traceInit(){
	int i = 0;
	struct taskInfo *aux;
	struct taskInfo *first;
    gettimeofday(&TIME_STAMP_INITIAL,NULL);
    TIME_INITIAL = (((int) TIME_STAMP_INITIAL.tv_sec) % 10000) + (double) TIME_STAMP_INITIAL.tv_usec / 1000000;
	aux = (struct taskInfo *) malloc (sizeof(struct taskInfo));
	aux->my_rank = -1;
	first = aux;
	while(i < SIZE_OF_LIST){
		aux->next = (struct taskInfo *) malloc (sizeof(struct taskInfo));
		aux->my_rank = -1;
		aux->id = 0;
		aux->offset = -1;
		aux = aux->next;
		i++;
	}
	FIRST = first;
	LAST = first;
	COUNT_ID = 0;
}



void traceEnd(int myRank){

	printf("TRACE END: MY RANK IS: %i\n", myRank );
	char *fileName; 
	char *traceNumber;
	traceNumber = (char*) malloc(3 * sizeof(char));
	fileName = (char*) malloc(21 * sizeof(char));
	strcpy(fileName,"simulation_mpi_");
	sprintf(traceNumber, "%i",myRank);
	strcat(fileName,traceNumber);
	strcat(fileName, ".txt");

	FILE * file= fopen(fileName, "wb");
	if (file != NULL) {
		//showListTaskInfo(FIRST);
		writeListTaskInfo(file);
	}
	else printf("TraceLib_Error -> writeTrace() rank %i \n", myRank);


	fclose(file);

}	


void addTaskInfo(char *name, struct timeval *tvIni,struct timeval *tvEnd, int my_rank, int rank_dst_src,int dataSize){

	struct taskInfo *ini;
	struct taskInfo *end;
	ini = LAST;
	end = ini->next;
	char nameIni[10] = "";
	char nameEnd[10] = "";

	srand((unsigned int) (tvIni->tv_usec + tvEnd->tv_usec));
	//long long int id = rand() + rand();
	//if(id < 0) id = id * -1;
	strcat(nameIni,INIT);
	strcat(nameEnd,END);
	strcat(nameIni,name);
	strcat(nameEnd,name);
	strcpy(ini->name,nameIni);
	strcpy(end->name,nameEnd);

	ini->pathname = malloc(sizeof(EMPTY));
	end->pathname = malloc(sizeof(EMPTY));
	strcpy(ini->pathname, EMPTY);
	strcpy(end->pathname, EMPTY);

	ini->timeStamp = ((((int) tvIni->tv_sec) % 10000) + (double) tvIni->tv_usec / 1000000)-TIME_INITIAL;
    end->timeStamp = ((((int) tvEnd->tv_sec) % 10000) + (double) tvEnd->tv_usec / 1000000)-TIME_INITIAL;               
    
	ini->my_rank = my_rank;
	end->my_rank = my_rank;

	ini->rank_dst_src = rank_dst_src;
	end->rank_dst_src = rank_dst_src;

	ini->data_size = dataSize;
	end->data_size = dataSize;

	ini->id = COUNT_ID;
	end->id= COUNT_ID;
	
	COUNT_ID = COUNT_ID + 1;
	LAST = end->next;
}


void addTaskFileInfo(const char *name, const char *path,struct timeval *tvIni,struct timeval *tvEnd, int my_rank, int rank_dst_src,int dataSize,int offset){

	struct taskInfo *ini;
	struct taskInfo *end;
	ini = LAST;
	end = ini->next;
	char nameIni[10] = "";
	char nameEnd[10] = "";

	srand((unsigned int) (tvIni->tv_usec + tvEnd->tv_usec));
	//long long int id = rand() + rand();
	//if(id < 0) id = id * -1;
	strcat(nameIni,INIT);
	strcat(nameEnd,END);
	strcat(nameIni,name);
	strcat(nameEnd,name);
	strcpy(ini->name,nameIni);
	strcpy(end->name,nameEnd);
	printf("INFO addTaskFileInfo() PATH NAME:%s\n", path);

	ini->pathname = malloc(sizeof(char) * strlen(path));
	end->pathname = malloc(sizeof(char) * strlen(path));
	strcpy(ini->pathname, path);
	strcpy(end->pathname, path);

	printf("INFO addTaskFileInfo() PATH NAME ini:%s  PATH NAME end:%s\n", ini->pathname, end->pathname);

	ini->timeStamp = ((((int) tvIni->tv_sec) % 10000) + (double) tvIni->tv_usec / 1000000)-TIME_INITIAL;
    end->timeStamp = ((((int) tvEnd->tv_sec) % 10000) + (double) tvEnd->tv_usec / 1000000)-TIME_INITIAL;               
    
	ini->my_rank = my_rank;
	end->my_rank = my_rank;

	ini->rank_dst_src = -1;
	end->rank_dst_src = -1;

	ini->data_size = dataSize;
	end->data_size = dataSize;

	ini->id = COUNT_ID;
	end->id= COUNT_ID;

	if(offset != -1){
		ini->offset = offset;
		end->offset= offset;
	}
	
	COUNT_ID = COUNT_ID + 1;
	LAST = end->next;
}


void writeListTaskInfo(FILE *file){

	struct taskInfo *aux = FIRST;
	fprintf(file, "%f ", (0/aux->timeStamp));
	fwrite(TIMESTAMP,sizeof(TIMESTAMP), 1, file);
	fprintf(file, "%s", " 0" );
	fprintf(file, " %i", aux->my_rank);
	fprintf(file, " %f\n", TIME_INITIAL);
	//fprintf(file, "%s\n", "");

	while(aux->my_rank != -1){
		//printf("Escribimos elem de %p \n ", aux);
		fprintf(file, "%f ", aux->timeStamp);
 		fwrite(&aux->name, sizeof(aux->name), 1, file);
 		fprintf(file, " %i", aux->id);
	    fprintf(file, " %i", aux->my_rank);

	    //pathName NO EMPTY
 		if(strstr(aux->pathname, EMPTY) == NULL) {
 			if(strcmp(aux->name,"Ini_crea") != 0 && strcmp(aux->name,"End_crea") != 0
				&& strcmp(aux->name,"Ini_open") != 0 && strcmp(aux->name,"Ini_fope") != 0
				&& strcmp(aux->name,"End_open") != 0 && strcmp(aux->name,"End_fope") != 0
				&& strcmp(aux->name,"Ini_clos") != 0 && strcmp(aux->name,"Ini_fclo") != 0
				&& strcmp(aux->name,"End_clos") != 0 && strcmp(aux->name,"End_fclo") != 0){ //open or close functions
 				
 				fprintf(file, " %i ", aux->data_size);
 					printf("IN TASK : %s OFFSET IS %i\n",aux->name, aux->offset );
		 			if(aux->offset != -1){
			    		fprintf(file, "%i ", aux->offset);
			    	}	
 			}
 			else {
 				fprintf(file, " %s", "");
 			}
            
 			fprintf(file, "/%s", "");
 			fwrite(aux->pathname,strlen(aux->pathname), 1, file);
 		}
	    //pathName EMPTY
	    if(strstr(aux->pathname, EMPTY) != NULL) {
	    	fprintf(file, " %i", aux->data_size);
	    	fprintf(file, " %i", aux->rank_dst_src);	
	    }


	    
	    fprintf(file, "%s\n", "");
	    aux = aux->next;
	}
}




void showListTaskInfo(struct taskInfo *fun){

	struct taskInfo *aux = fun;
	printf("TIME INITIAL (%i): %f \n",aux->my_rank,TIME_INITIAL);
	while(aux->my_rank != -1){
		printf("Elem :  %s,pathname: %s, tS: %f, rS: %i, rD: %i, Size: %i, Id: %i\n", 
		aux->name,
		aux->pathname,
		aux->timeStamp,
		aux->my_rank, 
		aux->rank_dst_src,
		aux->data_size,
		aux->id
		);
		aux = aux->next;
	}

}

int MPI_Init_trace (int * argc, char *** argv){

	traceInit();
	return MPI_Init(argc,argv);
	
}


int MPI_Comm_size_trace (MPI_Comm comm, int * size){

	return MPI_Comm_size(comm, size);
}


int MPI_Comm_rank_trace (MPI_Comm comm, int * rank){

	return MPI_Comm_rank(comm, rank);

}

int MPI_Finalize_trace (){
	printf("MPI_Finalize_trace(): MY RANK IS: %i\n", FIRST->my_rank );
	traceEnd(FIRST->my_rank);
	return MPI_Finalize();
}

int creat_trace(const char *path, mode_t mode, const int myRank){

	int fd;
	struct timeval tvIni,tvEnd;
	gettimeofday(&tvIni, NULL);
	fd = creat(path,mode);
	gettimeofday(&tvEnd, NULL);

	addTaskFileInfo(CREAT,path,&tvIni,&tvEnd,myRank,-1,0,-1);


	return fd;
}

int open_trace(const char *pathname, int flags, const int myRank){

	int fd;
	struct timeval tvIni,tvEnd;
	gettimeofday(&tvIni, NULL);
	fd = open(pathname,flags);
	gettimeofday(&tvEnd, NULL);

	addTaskFileInfo(OPEN,pathname,&tvIni,&tvEnd,myRank,-1,0,-1);

	return fd;
}



FILE *fopen_trace(const char *path, const char *mode, const int myRank){

	FILE *file;
	struct timeval tvIni,tvEnd;

	gettimeofday(&tvIni, NULL);
	file = fopen(path,mode);
	gettimeofday(&tvEnd, NULL);
	
	addTaskFileInfo(FOPEN,path,&tvIni,&tvEnd,myRank,-1,0,-1);

	return file;
}


int close_trace(int fd, const int myRank){

	struct timeval tvIni,tvEnd;
	int cl;
	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fd);

	gettimeofday(&tvIni, NULL);
	cl = close(fd);	
	gettimeofday(&tvEnd, NULL);

	addTaskFileInfo(CLOSE,file,&tvIni,&tvEnd,myRank,-1,0,-1);

	return cl;
}
int fclose_trace(FILE *stream,const int myRank){

	struct timeval tvIni,tvEnd;
	int cl;
	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fileno(stream));
	gettimeofday(&tvIni, NULL);
	cl = fclose(stream);
	gettimeofday(&tvEnd, NULL);

	addTaskFileInfo(FCLOSE,file,&tvIni,&tvEnd,myRank,-1,0,-1);

	return cl;
}



ssize_t read_trace(int fd, void *buf, size_t count, const int myRank){
	
	struct timeval tvIni,tvEnd;
	ssize_t sz;
	//printf("DEBUG READ*****\n");
	gettimeofday(&tvIni, NULL);
	sz = read(fd,buf,count);
	gettimeofday(&tvEnd, NULL);

	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fd);
	addTaskFileInfo(READ,file,&tvIni,&tvEnd,myRank,-1, (int)count,-1);

	return sz;
}



ssize_t fread_trace(void *ptr, size_t size, size_t nmemb, FILE *stream, const int myRank){
	
	struct timeval tvIni,tvEnd;
	ssize_t sz;

	gettimeofday(&tvIni, NULL);
	sz = fread(ptr,size,nmemb,stream);
	gettimeofday(&tvEnd, NULL);

	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fileno(stream));

	addTaskFileInfo(FRE,file,&tvIni,&tvEnd,myRank,-1,(int) size,-1);

	return sz;
}

ssize_t pread_trace(int fd, void *buf, size_t count,off_t offset, const int myRank){

	struct timeval tvIni,tvEnd;
	ssize_t sz;

	gettimeofday(&tvIni, NULL);
	sz = pread(fd,buf,count,offset);
	gettimeofday(&tvEnd, NULL);

	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fd);

	addTaskFileInfo(PREA,file,&tvIni,&tvEnd,myRank,-1,(int) count,(int) offset);//AQUI

	return sz;
}

ssize_t write_trace(int fd, void *buffer, size_t count, const int myRank){
	
	struct timeval tvIni,tvEnd;
	ssize_t sz;

	gettimeofday(&tvIni, NULL);
	sz = write(fd,buffer,count);
	gettimeofday(&tvEnd, NULL);

	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fd);

	addTaskFileInfo(WRITE,file,&tvIni,&tvEnd,myRank,-1, (int)count,-1);

	return sz;
}

ssize_t fwrite_trace(const void *ptr,size_t size, size_t nmemb, FILE *stream, const int myRank){
	
	struct timeval tvIni,tvEnd;
	ssize_t sz;

	gettimeofday(&tvIni, NULL);
	sz = fwrite(ptr,size,nmemb,stream);
	gettimeofday(&tvEnd, NULL);

	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fileno(stream));

	addTaskFileInfo(FWRI,file,&tvIni,&tvEnd,myRank,-1,(int)size,-1);

	return sz;
}

ssize_t pwrite_trace(int fd, void *buffer, size_t count, off_t offset, const int myRank){

	struct timeval tvIni,tvEnd;
	ssize_t sz;

	gettimeofday(&tvIni, NULL);
	sz = pwrite(fd,buffer,count,offset);
	gettimeofday(&tvEnd, NULL);

	char *file;
	file = malloc (100 * sizeof(char));
	file = obtainFileName(fd);

	addTaskFileInfo(PWRI,file,&tvIni,&tvEnd,myRank,-1, (int)count, (int) offset);//AQUI

	return sz;
}

int MPI_Send_trace(const void *buf, int count, MPI_Datatype datatype, int dest, int tag, MPI_Comm comm,const int myRank){
	
	struct timeval tvIni,tvEnd;
	int mpi;
	gettimeofday(&tvIni, NULL);
	mpi = MPI_Send(buf,count,datatype,dest,tag,comm);
	gettimeofday(&tvEnd, NULL);

	addTaskInfo(SEND,&tvIni,&tvEnd,myRank,dest,count);

	return mpi;
}


int MPI_Recv_trace(void *buf, int count, MPI_Datatype datatype, int source, int tag, MPI_Comm comm, MPI_Status *status, const int myRank){
	
	struct timeval tvIni,tvEnd;
	int mpi;

	gettimeofday(&tvIni, NULL);
	mpi = MPI_Recv(buf,count,datatype,source,tag,comm,status);
	gettimeofday(&tvEnd, NULL);

	addTaskInfo(RECV,&tvIni,&tvEnd,myRank,source,count);

	return mpi;
}


int MPI_Bcast_trace(void *buffer, int count, MPI_Datatype datatype, int root, MPI_Comm comm,const int myRank){
	
	struct timeval tvIni,tvEnd;
	int mpi;

	gettimeofday(&tvIni, NULL);
	mpi = MPI_Bcast(buffer,count,datatype,root,comm);
	gettimeofday(&tvEnd, NULL);

	addTaskInfo(BROADCAST,&tvIni,&tvEnd,myRank,root,count);

	return mpi;
}


int MPI_Scatter_trace(const void *sendbuf, int sendcount, MPI_Datatype sendtype, void *recvbuf, int recvcount, MPI_Datatype recvtype, int root, MPI_Comm comm, const int myRank){
	
	struct timeval tvIni,tvEnd;
	int mpi;

	gettimeofday(&tvIni, NULL);
	mpi = MPI_Scatter(sendbuf,sendcount,sendtype,recvbuf, recvcount,recvtype,root,comm);
	gettimeofday(&tvEnd, NULL);

	addTaskInfo(SCATTER,&tvIni,&tvEnd,myRank,root,sendcount);

	return mpi;
}



int MPI_Gather_trace(const void *sendbuf, int sendcount, MPI_Datatype sendtype, void *recvbuf, int recvcount, MPI_Datatype recvtype, int root, MPI_Comm comm, const int myRank){

	struct timeval tvIni,tvEnd;
	int mpi;

	gettimeofday(&tvIni, NULL);
	mpi = MPI_Gather(sendbuf,sendcount,sendtype,recvbuf,recvcount,recvtype,root,comm);
	gettimeofday(&tvEnd, NULL);

	addTaskInfo(GATHER,&tvIni,&tvEnd,myRank,root,sendcount);

	return mpi;
}



char * obtainFileName(const int fd){
	//struct stat sb;
	char *linkname;
	ssize_t r;
	linkname = malloc(100 * sizeof(char));
	if(linkname == NULL){
		exit(EXIT_FAILURE);
	}

	char fdS[5];
	
	sprintf(fdS,"%d",fd);
	char path[100] = "/proc/self/fd/";
       // path = "/proc/self/fd/";
	strcat(path,fdS);
	r = readlink(path,linkname, 100*sizeof(char));
	
	if(r < 0){
		exit(EXIT_FAILURE);
	}
	
	return linkname;
	
}

/*
char * obtainFileName(const int fd){

	//mostramos toda la información
	lsof_dumpinfo(fd);
	struct _info_t info;
	info.pid = getpid(fd);

}



*/









































