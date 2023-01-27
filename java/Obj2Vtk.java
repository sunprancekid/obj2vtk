import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;


/** 
* [class description]
*
* @author Matthew Dorsey (madorse2@ncsu.edu)
*/
public class Obj2Vtk {

    // CLASS CONSTANTS
    
    /** number of dimensions */
    public static final int NDIM = 3;
    
    /** VTK Header */
    public static final String PI_VTK_HEADER = "# vtk DataFile Version 3.1";
    
    /** comment line */
    public static final String PII_COMMENT_LINE = "# created by Obj2Vtk.java; Author: Matthew Dorsey (madorse2@ncsu.edu)";

    /** description - JavaDoc comment */
 
    /**
    * [method description]
    *
    * @param args command line arugements (not used)
    */
    public static void main(String[]args) {
    
        Scanner console = new Scanner (System.in);
        
        // verify args length is 1
        if (args.length != 1) {
            System.out.println("Usage: java Obj2Vtk [directory name containing .obj files] ");
            System.exit(1);
        }
        
        // create input scanners for .obj and .mtl files from TinkerCAD
        String objFile = args[0] + "/tinker.obj";
        Scanner objScanner = createInputScanner(objFile);
        
        String mtlFile = args[0] + "/obj.mtl";
        Scanner mtlScanner = createInputScanner(mtlFile);
        
        // how many different vertices, faces, and groups are in the object file?
        int numObjs = 0;
        int numVerts = 0;
        int numFaces = 0;
        int numGroups = 0;
        while (objScanner.hasNextLine()) {
            String objLine = objScanner.nextLine();
            if (objLine.length() >= 1) {
                char type = objLine.charAt(0);
                if (type == 'o') {
                    numObjs++;
                } else if (type == 'v') {
                    numVerts++;
                } else if (type == 'f') {
                    numFaces++;
                } else if (type == 'g') {
                    numGroups++;
                }
            }
        }
        // report to user
        System.out.println ("The file " + objFile + " contains " + numObjs + "objects, " 
            + numVerts + " vertices, " + numFaces + " faces, and " + numGroups + " groups.");
        
        // how many different colors are int the material file?
        int numColors = 0;
        while (mtlScanner.hasNextLine()) {
            String mtlLine = mtlScanner.nextLine();
            if (mtlLine.length() >= 1) {
                Scanner mtlLineScanner = new Scanner (mtlLine);
                String mtlString = mtlLineScanner.next();
                if (mtlString.equals("newmtl")) {
                    mtlString = mtlLineScanner.next();
                    if (mtlString.charAt(0) == 'c') {
                        numColors++;
                    }
                }
            }
        }
        // report to user
        System.out.println ("The file " + mtlFile + " contains " + numColors + " colors.");
        
        // MATERIAL FILE 

        mtlScanner = createInputScanner(mtlFile);
        Color [] colorList = new Color [numColors];
        int inc = 0;
        while (mtlScanner.hasNextLine()) {
            String mtlLine = mtlScanner.nextLine();
            if (mtlLine.length() >= 1) {
                Scanner mtlLineScanner = new Scanner (mtlLine);
                String mtlString = mtlLineScanner.next();
                if (mtlString.equals("newmtl")) {
                    // if the line is a new material, create a new color
                    String colorString = "";
                    do {
                        colorString += mtlLine + "\n";
                        mtlLine = mtlScanner.nextLine();
                    } while (mtlLine.length() >= 1);
                    colorList[inc] = new Color(colorString);
                    inc ++;
                }
            }
        }
        
        // OBJECT FILE
        
        // read in vertices
        float [][] vertices = new float [numVerts][NDIM];
        objScanner = createInputScanner (objFile);
        int lines = 0;
        while (objScanner.hasNextLine()) {
            String objLine = objScanner.nextLine();
            if (objLine.length() >= 1) {
                char type = objLine.charAt(0);
                if (type == 'v') {
                    Scanner objLineScanner = new Scanner (objLine);
                    objLineScanner.next();
                    for (int i = 0; i < NDIM; i++) {
                        vertices[lines][i] = Float.parseFloat(objLineScanner.next());
                    }
                    lines++;
                }
            }
        }
        
        // read in faces, match faces with groups
        int [][] faces = new int [numFaces][3]; // array containing points that make triangle mesh
        int [] faceColor = new int [numFaces]; // array pointing to color of triangle
        objScanner = createInputScanner (objFile);
        int countFaces = 0;
        int currColor = 1; // integer pointing to the current color of the face
        while (objScanner.hasNextLine()) {
            String objLine = objScanner.nextLine();
            if (objLine.length() >= 1) {
                char type = objLine.charAt(0);
                if (type == 'f') {
                    Scanner objLineScanner = new Scanner (objLine);
                    objLineScanner.next(); // f
                    for (int i = 0; i < NDIM; i++) {
                        faces [countFaces][i] = Integer.parseInt(objLineScanner.next());
                        faces [countFaces][i] += (-1);
                    }
                    faceColor[countFaces] = currColor;
                    countFaces++;
                } else if (type == 'u') {
                    Scanner objLineScanner = new Scanner (objLine); 
                    objLineScanner.next(); // usemtl
                    String mtlColor = objLineScanner.next();
                    for (int i = 0; i < numColors; i++) {
                        if (colorList[i].compare(mtlColor)) {
                            currColor = i; // TODO: turn colorList into object, 
                            // so that colors can have a default setting if none are found
                        }
                    }
                }
            }
        }
        
        // match groups with colors
        
        
        // output vertices and faces to .vtk file
        
        // create output file
        String vtkFile = args[0] + ".vtk";
        PrintWriter vtkPrinter = createOutputPrinter (vtkFile);
        
        // part 1 - vtk version and identifier
        vtkPrinter.println(PI_VTK_HEADER);
        
        // part 2 - header
        vtkPrinter.println(PII_COMMENT_LINE);
        
        // part 3 - type of data
        vtkPrinter.println("ASCII");
        
        // part 4 - structure, points
        vtkPrinter.println("DATASET UNSTRUCTURED_GRID");
        vtkPrinter.println("POINTS " + numVerts + " FLOAT");
        for (int i = 0; i < numVerts; i++) {
            String vtkLine = "\t";
            for (int j = 0; j < NDIM; j++) {
                vtkLine += vertices[i][j] + "\t";
            }
            vtkPrinter.println(vtkLine);
        }
        
        // part 4 - structure, cells
        vtkPrinter.println("CELLS " + numFaces + " " + (numFaces * 4));
        for (int i = 0; i < numFaces; i++) {
            String vtkLine = "3 ";
            for (int j = 0; j < 3; j++) {
                vtkLine += faces[i][j] + " ";
            }
            vtkPrinter.println(vtkLine);
        }
        
        // part 4 - structure, cell_types
        vtkPrinter.println("CELL_TYPES " + numFaces);
        for (int i = 0; i < numFaces; i++) {
            vtkPrinter.println("5");
        }
        
        // part 5 - Attributes
        vtkPrinter.println("CELL_DATA " + numFaces);
        // part 5 - Attributes, color
        vtkPrinter.println("COLOR_SCALARS faceColor 3");
        for (int i = 0; i < numFaces; i++) {
            vtkPrinter.println(colorList[faceColor[i]].RGB());
        }
        
        objScanner.close();
        mtlScanner.close();
        vtkPrinter.close();
    }
    
    
    
    /**
    * [mutator method description]
    *
    * @param 
    * @return
    */
    public static Scanner createInputScanner (String inFile) {
        Path path = Path.of(inFile);
        if (!Files.exists(path)) {
            System.out.println("Unable to access file: " + inFile);
            System.exit(1);
        }
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream (inFile);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to access input file: " + inFile);
            System.exit(1);
        }
        return new Scanner(inStream);
    } 
    
    /**
    * [mutator method description]
    *
    * @param 
    * @return
    */
    public static PrintWriter createOutputPrinter (String outFile) {
        FileOutputStream outStream = null;
        Scanner console = new Scanner (System.in);
        Path path = Path.of(outFile);
        try {
            if (Files.exists(path)) {
                System.out.print(outFile + " exists - OK to overwrite (y,n)?: ");
                String response = console.next();
                if ((response.charAt(0) != 'y') && (response.charAt(0) != 'Y')) {
                    System.exit(1);
                } else {
                    outStream = new FileOutputStream (outFile);
                }
            } else {
                outStream = new FileOutputStream (outFile);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot create output file: " + outFile);
            System.exit(1);
        }
        return new PrintWriter (outStream);
    }
}