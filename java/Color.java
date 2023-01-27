import java.util.Scanner;

/** 
* [class description]
*
* @author Matthew Dorsey (madorse2@ncsu.edu)
*/
public class Color {
    
    //fields
    
    /** color id from .mtl file  */
    private String id;
    
    /** R color number */
    private float R;
    
    /** G color number */
    private float G;
    
    /** B color number */
    private float B;
    
    
    //constructors
    public Color (String colorString) {
        
        // find RGB description of color in .mtl file
        Scanner colorScanner = new Scanner (colorString);
        while (colorScanner.hasNextLine()) {
            String colorLine = colorScanner.nextLine();
            Scanner colorLineScanner = new Scanner (colorLine);
            if (colorLine.charAt(0) == 'n') {
                colorLineScanner.next(); // newmtl
                id = colorLineScanner.next();
            } else if ((colorLine.charAt(0) == 'K') && (colorLine.charAt(1) == 'd')) {
                colorLineScanner.next(); // Kd
                R = colorLineScanner.nextFloat();
                G = colorLineScanner.nextFloat();
                B = colorLineScanner.nextFloat();
            }
        }
    }
    
    
    //accessors (getters)
    
    /**
    * [accessor method description]
    *
    * @param 
    * @return
    */
    public String toString() {
        String s = "";
        s += id + " " + R + " " + G + " " + B;
        return s;
    }
    
    /**
    * [accessor method description]
    *
    * @param 
    * @return
    */
    public String RGB() {
        String s = "";
        s += Float.toString(R) + " " + Float.toString(G) + " " + Float.toString(B);
        return s;
    }
        
    /**
    * [accessor method description]
    *
    * @param 
    * @return
    */
    public boolean compare(String colorString) {
        return (id.equals(colorString));
    }
}
