#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <libgen.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pwd.h>
#include <sys/stat.h>

#define BUF_MAX 1024
#define CMD_DISPLAY_MAX (9 + 1)
#define USER_DISPLAY_MAX (10 + 1)


struct pid_info_t {
    pid_t pid;
    char user[USER_DISPLAY_MAX];
    char cmdline[CMD_DISPLAY_MAX];
    char path[PATH_MAX];
    ssize_t parent_length;
};
void print_header()
{
    printf("%-9s %5s %10s %4s %9s %18s %9s %10s %s\n",
            "COMMAND",
            "PID",
            "USER",
            "FD",
            "TYPE",
            "DEVICE",
            "SIZE/OFF",
            "NODE",
            "NAME");
}
void print_type(char *type, struct pid_info_t* info)
{
    static ssize_t link_dest_size;
    static char link_dest[PATH_MAX];

    strncat(info->path, type, sizeof(info->path));
    if ((link_dest_size = readlink(info->path, link_dest, sizeof(link_dest)-1)) < 0) {
        if (errno == ENOENT)
            goto out;
	    //printf("c1\n");

	//if(link_dest != null && errno != NULL && info->path != NULL)
 	//printf("%s (readlink)\n", info->path, strerror(errno));
        snprintf(link_dest, sizeof(link_dest), "%s (readlink)", info->path);
	    //printf("c1.1\n");
    } else {
        link_dest[link_dest_size] = '\0';
    }
	//printf("c2\n");
    // Things that are just the root filesystem are uninteresting (we already know)
    if (!strcmp(link_dest, "/"))
        goto out;
    printf("%-9s %5d %10s %4s %9s %18s %9s %10s %s\n",
            info->cmdline, info->pid, info->user, type,
            "???", "???", "???", "???", link_dest);
out:
    info->path[info->parent_length] = '\0';
}
// Prints out all file that have been memory mapped
void print_maps(struct pid_info_t* info)
{
    FILE *maps;
    char buffer[PATH_MAX + 100];
    size_t offset;
    int major, minor;
    char device[10];
    long int inode;
    char file[PATH_MAX];
    strncat(info->path, "maps", sizeof(info->path));
    maps = fopen(info->path, "r");
    if (!maps)
        goto out;
    while (fscanf(maps, "%*x-%*x %*s %zx %5s %ld %s\n", &offset, device, &inode,
            file) == 4) {
        // We don't care about non-file maps
        if (inode == 0 || !strcmp(device, "00:00"))
            continue;
        printf("%-9s %5d %10s %4s %9s %18s %9zd %10ld %s\n",
                info->cmdline, info->pid, info->user, "mem",
                "???", device, offset, inode, file);
    }
    fclose(maps);
out:
    info->path[info->parent_length] = '\0';
}
// Prints out all open file descriptors
void print_fds(struct pid_info_t* info)
{
    static char* fd_path = "fd/";
    strncat(info->path, fd_path, sizeof(info->path));
    int previous_length = info->parent_length;
    info->parent_length += strlen(fd_path);
    DIR *dir = opendir(info->path);
    if (dir == NULL) {
        char msg[BUF_MAX];
        snprintf(msg, sizeof(msg), "%s (opendir)", info->path);
        printf("%-9s %5d %10s %4s %9s %18s %9s %10s %s\n",
                info->cmdline, info->pid, info->user, "FDS",
                "", "", "", "", msg);
        goto out;
    }
    struct dirent* de;
    while ((de = readdir(dir))) {
        if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
            continue;
        print_type(de->d_name, info);
    }
    closedir(dir);
out:
    info->parent_length = previous_length;
    info->path[info->parent_length] = '\0';
}
void lsof_dumpinfo(pid_t pid)
{
    int fd;
    struct pid_info_t info;
    struct stat pidstat;
    struct passwd *pw;
    info.pid = pid;

    snprintf(info.path, sizeof(info.path), "/proc/%d/", pid);
    info.parent_length = strlen(info.path);
    // Get the UID by calling stat on the proc/pid directory.
    if (!stat(info.path, &pidstat)) {
        pw = getpwuid(pidstat.st_uid);
        if (pw) {
            strlcpy(info.user, pw->pw_name, sizeof(info.user));
        } else {
            snprintf(info.user, USER_DISPLAY_MAX, "%d", (int)pidstat.st_uid);
        }
    } else {
        strcpy(info.user, "???");
    }
    printf("5\n");
    // Read the command line information; each argument is terminated with NULL.
    strncat(info.path, "cmdline", sizeof(info.path));
    fd = open(info.path, O_RDONLY);
    if (fd < 0) {
        fprintf(stderr, "Couldn't read %s\n", info.path);
        return;
    }
    //printf("6\n");
    char cmdline[PATH_MAX];
    int numRead = read(fd, cmdline, sizeof(cmdline) - 1);
    close(fd);
    if (numRead < 0) {
        fprintf(stderr, "Error reading cmdline: %s: %s\n", info.path, strerror(errno));
        return;
    }
    cmdline[numRead] = '\0';

    // We only want the basename of the cmdline
    strlcpy(info.cmdline, basename(cmdline), sizeof(info.cmdline));

    // Read each of these symlinks
    print_type("cwd", &info);
    print_type("exe", &info);
    print_type("root", &info);
    print_fds(&info);
    print_maps(&info);
}
int main(int argc, char *argv[])
{
    long int pid = 0;
    char* endptr;
    if (argc == 2) {
        pid = strtol(argv[1], &endptr, 10);
    }
    print_header();

    //Si le pasas el pid, te saca la info de todos los procesos con ese PID
    if (pid) {
        lsof_dumpinfo(pid);
    } else {
        DIR *dir = opendir("/proc");
        if (dir == NULL) {
            fprintf(stderr, "Couldn't open /proc\n");
            return -1;
        }
        struct dirent* de;

        while ((de = readdir(dir))) {
            if (!strcmp(de->d_name, ".") || !strcmp(de->d_name, ".."))
                continue;
            // Only inspect directories that are PID numbers
            pid = strtol(de->d_name, &endptr, 10);
            if (*endptr != '\0')
                continue;
            lsof_dumpinfo(pid);
        }
        closedir(dir);
    }
    return 0;
}
