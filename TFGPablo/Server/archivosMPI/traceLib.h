
/**
	@file traceLib.h
	@brief Library thet implements I/O and MPI functions in order to generate useful traces for study


	@author Bryan Raúl Vaca Vargas
	@date 02/2018
*/

#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <mpi.h>
#include <time.h>

#define NAME_TRACE_FILE "simulation_mpi_" /*!< Name of the worker's log file  */
#define CREAT 			"_crea"
#define OPEN 			"_open"
#define FOPEN 			"_fope"
#define CLOSE 			"_clos"
#define FCLOSE 			"_fclo"
#define WRITE 			"_writ"
#define FWRI 			"_fwri"
#define PWRI 			"_pwri"
#define READ 			"_read"
#define FRE 			"_frea"
#define PREA			"_prea"
#define SEND 			"_send"
#define RECV 			"_recv"
#define BROADCAST 		"_bcas"
#define SCATTER 		"_scat"
#define GATHER 			"_gath"
#define INIT 			"Ini"
#define END 			"End"
#define EMPTY			"Empty"
#define TIMESTAMP 		"Ini_time"

/**
    @brief Structure with the task information
 */
struct taskInfo{
	char name[9];                   /*!< Name of the task  */
	char *pathname;                 /*!< Name of the file readed, written, open, created or closed  */
	double timeStamp;               /*!< TimeStamp in format double  */
	int data_size;                  /*!< Size of data readed, written or sent  */
	int offset;                  	/*!< Offset in pwrite or pread functions  */
	int my_rank;                    /*!< Rank of the worker in execution */
	int rank_dst_src;               /*!< Other rank, can be destination or source  */
	int id;                         /*!< Id of the task  */
	struct taskInfo *next;          /*!< Pointer to the next Task  */
};



/**
	@brief Call create() of Standard C Library and add taskInfo to list.
	@param pathname Name of the file
	@param mode Opening mode
	@param myRank Rank of the worker that call the function.
	@return a File Descriptor
*/
int creat_trace(const char *path, mode_t mode, const int myRank);

/**
	@brief Call open() of Standard C Library and and call to the internal function addTaskFileInfo()
	@param pathname Name of file
	@param flags Opening mode
	@param myRank Rank of the worker that call the function.
	@return a File Descriptor
*/
int open_trace(const char *pathname, int flags, const int myRank);


/**
 @brief Call the function: fopen() of Standard  C Library and call to the internal function addTaskFileInfo()
	@param path Name of the file
	@param mode Opening mode
	@param myRank Rank of the worker that call the function.
	@return Upon successful completion return a FILE pointer, Otherwise, NULL
*/
FILE *fopen_trace(const char *path, const char *mode, const int myRank);

/**
	@brief Call close() of Standard C Library and and call to the internal function addTaskFileInfo()
	@param fd  This is a file descriptor to a file.
	@param myRank Rank of the worker that call the function.
	@return File descriptor.
*/
int close_trace(int fd, const int myRank);

/**
	@brief Call fclose() of Standard C Library and add taskInfo to list.
	@param FILE This is a pointer to a file
	@param myRank Rank of the worker that call the function.
	@return Upon succesful completion 0 is returned.
*/
int fclose_trace(FILE *stream, const int myRank);

/**
	@brief Call read() of Standard C Library and add task to list.
	@param fd This is a file descriptor to a file.
	@param buf Buffer pointer where the read data will be saved
	@param count Size of data readed
	@param myRank Rank of the worker that call the function.
	@return Size of bytes read or on error, -1 is returned.
*/
ssize_t read_trace(int fd, void *buf, size_t count, const int myRank);

/**
	@brief Call read() of Standard C Library and add task to list.
	@param fd This is a file descriptor to a file.
	@param buf Buffer pointer where the read data will be saved
	@param count Size of data readed
	@param offset Initial reading position
	@param myRank Rank of the worker that call the function.
	@return Size of bytes read or on error, -1 is returned.
*/
ssize_t pread_trace(int fd, void *buf, size_t count,off_t offset, const int myRank);

/**
	@brief Call fread() of Standard C Library and add task to list.
	@param ptr pointer where the read data will be saved
	@param size Size of data readed
	@param nmemb Number of size data readed
	@param FILE This is a pointer to a file
	@param myRank Rank of the worker that call the function.
	@return Size of bytes read or on error, the return value is a short item count or zero
*/
ssize_t fread_trace(void *ptr, size_t size, size_t nmemb, FILE *stream, const int myRank);

/**
	@brief Call write() of Standard C Library and add task to list.
	@param fd This is a file descriptor to a file.
	@param buffer Buffer pointer where the writing data will be read
	@param count Size of data written
	@param myRank Rank of the worker that call the function.
	@return Size of bytes write or on error, -1 is returned.
*/
ssize_t write_trace(int fd, void *buffer, size_t count, const int myRank);

/**
	@brief Call fwrite() of Standard C Library and add task to list.
    @param ptr Pointer where the writing data will be read
    @param size Size of data written
    @param nmemb Number of size data written
    @param FILE This is a pointer to a file
	@param myRank Rank of the worker that call the function.
	@return Size of bytes write or on error, the return value is a short item count or zero
*/
ssize_t fwrite_trace(const void *ptr,size_t size, size_t nmemb, FILE *stream, const int myRank);


/**
	@brief Call pwrite() of Standard C Library and add task to list.
	@param fd This is a file descriptor to a file.
	@param buffer Buffer pointer where the writing data will be read
	@param count Size of data written
	@param offset Initial writing position
	@param myRank Rank of the worker that call the function.
	@return Size of bytes write or on error, -1 is returned.
*/
ssize_t pwrite_trace(int fd, void *buffer, size_t count, off_t offset, const int myRank);

/**
	@brief Call MPI_Init of MPI library and call to TraceInit
	@param argc
	@param argv
*/
int MPI_Init_trace (int * argc, char *** argv);

/**
	@brief Call MPI_Comm_size() of MPI library
	@param comm Communicator
	@param size Number of workers
*/
int MPI_Comm_size_trace (MPI_Comm comm, int * size);

/**
	@brief all MPI_rank() of MPI library
	@param comm Communicator
	@param rank Rank of current worker
*/
int MPI_Comm_rank_trace (MPI_Comm comm, int * rank);

/**
	@brief Call MPI_Finalize() of MPI library and call to writeListTaskInfo() function 
*/
int MPI_Finalize_trace ();


/**
    @brief Call Mpi_Send() of MPI library and add task to list.
    @param buf Pointer to the data that is sent
    @param count Number of elements in the message
    @param datatype type of data sent
    @param source rank of the process to which the message is sent
	@param tag label that can serve as the unique ID of the message
	@param comm Communicator
	@param myRank Rank of the worker that call the function.
	@return The value return in mpi_send() function.
*/
int MPI_Send_trace(const void *buf, int count, MPI_Datatype datatype, int dest, int tag, MPI_Comm comm,const int myRank);

/**
	@brief Call Mpi_Recv() of MPI library and add task to list.
	@param buf Pointer to the data where the received data is written
	@param count Number of elements in the message
	@param datatype type of data sent
    @param source rank of the process from which the message is expected to be received
	@param tag label that can serve as the unique ID of the message
	@param status Status of MPI
	@param comm Communicator
	@param myRank Rank of the worker that call the function.
	@return The value return in mpi_Recv() function.
*/
int MPI_Recv_trace(void *buf, int count, MPI_Datatype datatype, int source, int tag, MPI_Comm comm, MPI_Status *status, const int myRank);

/**
	@brief Call Mpi_Bcast() of MPI library and add task to list.
	@param buffer Pointer to the data where the received data is written or the data to be sent is read
	@param count Number of elements in the message
	@param datatype Type of data sent
	@param root Rank of the issuing process
	@param comm Communicator
	@param myRank Rank of the worker that call the function.
	@return The value return in mpi_Bcast() function.
*/
int MPI_Bcast_trace(void *buffer, int count, MPI_Datatype datatype, int root, MPI_Comm comm,const int myRank);


/**
	@brief Call Mpi_Scatter() of MPI library and add task to list.
	@param sendbuf Data to send, a piece to each process (only root uses it)
	@param sendcount Number of items that root sends to each recipient
	@param sendtype Type of data sent
	@param recvbuf Data of the message received from root
	@param recvcount Number of items to receive
	@param recvtype Data type of the message to be received
	@param root Rank of the process that sends the data to all processes
	@param comm Communicator
	@param myRank Rank of the worker that call the function.
	@return The value return in mpi_Scatter() function.
*/
int MPI_Scatter_trace(const void *sendbuf, int sendcount, MPI_Datatype sendtype, void *recvbuf, int recvcount, MPI_Datatype recvtype, int root, MPI_Comm comm, const int myRank);


/**
	@brief Call Mpi_Gather() of MPI library and add task to list.
	@param sendbuf Data to send to root
	@param sendcount Number of elements to send to root
	@param sendtype Type of data to send
	@param recvbuf Data received, a piece of each process (only root uses it)
	@param recvcount Number of elements that root receives from each issuer
	@param recvtype Type of data that root receives
	@param root Rank of the process that receives data from all processes
	@param comm Communicator
	@param myRank Rank of the worker that call the function.
	@return The value return in mpi_Scatter() function.
*/
int MPI_Gather_trace(const void *sendbuf, int sendcount, MPI_Datatype sendtype, void *recvbuf, int recvcount, MPI_Datatype recvtype, int root, MPI_Comm comm, const int myRank);

/**
 @brief Internal function of the TraceLib. Initialize the variables FIRST, LAST, TIME_INITIAL, COUNT_ID  necessary for the task registration
 */
void traceInit();


/**
 @brief Internal function of the TraceLib. Write the task list in the log file \link"https://www.google.com"
 */
void traceEnd();

/**
 @brief Internal function of the TraceLib. Create a taskInfo task and dd to the end of the list and update the pointer LAST
 @param name Name of the MPI or E/S function called
 @param tvIni Initial time stamp
 @param tvEnd Final time stamp
 @param rankSource Rank of the worker
 @param rankDest Rank of the source or destination worker
 @param dataSize Size of data readed, written, sent or received in the function called.
 
 
 */
void addTaskInfo(char *name, struct timeval *tvIni,struct timeval *tvEnd, int rankSource, int rankDest,int dataSize);

/**
 @brief Internal function of the TraceLib. Create a taskInfo task and dd to the end of the list and update the pointer LAST
 @param name Name of the MPI or E/S function called
 @param path Name of file readed, written, create, opened or closed.
 @param tvIni Initial time stamp
 @param tvEnd Final time stamp
 @param rankSource Rank of the worker
 @param rankDest Rank of the source or destination worker
 @param dataSize Size of data read, written, sent or received in the function called.
 @param offset Ofsset if function called is pread or pwrite
 */
void addTaskFileInfo(const char *name, const char *path,struct timeval *tvIni,struct timeval *tvEnd, int my_rank, int rank_dst_src,int dataSize,int offset);

/**
 @brief Internal function of the TraceLib. Goes through the list of tasks and write them in the file
 @param file File where the task list is written
 */
void writeListTaskInfo(FILE *file);

/**
 @brief Internal function of the TraceLib. Auxiliary function that shows the information of a task
 @param fun Task with the information to show
 */
void showListTaskInfo(struct taskInfo *fun);

/**
 @brief Internal function of the TraceLib. Function that obtains de name from file Descriptor. This function is called from close_trace() or fclose_trace()
 @param fd File Descriptor to search
 */
char * obtainFileName(const int fd);
