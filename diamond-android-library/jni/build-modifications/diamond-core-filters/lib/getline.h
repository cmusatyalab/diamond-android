#ifndef GETLINE_H
#define GETLINE_H

#include <stdio.h>

extern ssize_t getdelim(char **lineptr, size_t *n, int delim, FILE *stream);

extern ssize_t getline(char **lineptr, size_t *n, FILE *stream);

#endif /* GETLINE_H */

