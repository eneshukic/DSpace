package org.dspace.content;

public class DCBoundingBox {
    
      /** West*/
    private String west;

    /** East */
    private String east;

      /** South */
    private String south;

    /** North */
    private String north;

    /** Construct clean Bounding Box */
    public DCBoundingBox()
    {
        west = null;
        east = null;
        south = null;
        north = null;
    }

    /**
     * Construct from raw DC value
     * 
     * @param value
     *            value from database
     */
    public DCBoundingBox(String value)
    {
        this();

        if (value != null)
        {
            String boundingBox[] = value.split(" ");
            if (boundingBox.length == 4) {
                west = boundingBox[0].trim();
                east = boundingBox[2].trim();
                south = boundingBox[1].trim();
                north = boundingBox[3].trim();
            }
        }
    }



    /**
     * Write as raw DC value
     * 
     * @return the bounding box as they should be stored in the DB
     */
    public String toString()
    {
      
        return (west + "," + east + "," + south + "," + north);
       
    }


    /**
     * Get West - guaranteed non-null
     */
    public String getWest()
    {
        return ((west == null) ? "" : west);
    }

    /**
     * Get East - guaranteed non-null
     */
    public String getEast()
    {
        return ((east == null) ? "" : east);
    }
    
     /**
     * Get South - guaranteed non-null
     */
    public String getSouth()
    {
        return ((south == null) ? "" : south);
    }

    /**
     * Get North - guaranteed non-null
     */
    public String getNorth()
    {
        return ((north == null) ? "" : north);
    }
    
}
