# !/bin/bash
# 2023-01-27

# Matthew A. Dorsey
# Chemical and Biomolecular Engineering
# North Carolina State University

# This script compiles programs located in the java folder, 
# then uses them to convert .obj files to .vtk type files.


## ARGUMENTS
# first argument: name of the obj sub directory that contains object file to convert
OBJ=$1

## PARAMETERS
# none


## SCRIPT

# compile java programs
javac java/Color.java
javac -cp java java/Obj2Vtk.java

# execute java programs for files passed as arguments
java -cp java Obj2Vtk obj/${OBJ}

# move vtk file generate to vtk directory
mv obj/*.vtk vtk/
