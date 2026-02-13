package models;

public enum SpotType {

  COMPACT ("Compact", 2.0), 
  REGULAR ("Regular", 5.0), 
  HANDICAPPED ("Handicapped", 2.0),
  RESERVED ("Reserved", 10.0); 

  private final String type; 
  private final double hourlyRate; 

    SpotType(String type, double rate) {
        this.type = type;
        this.hourlyRate = rate;
    }

    public double getRate () { return hourlyRate; }
  
    public static SpotType fromString (String type){ 
      for (SpotType s : values()){ 
        if (s.type.equalsIgnoreCase(type))
        { 
          return s; 
        }
      }

      return REGULAR; // default 
    }

    // @Override
    // public String toString() { 
    //     return type; // This makes the Dropdown show "Regular" instead of "REGULAR"
    // }
}
