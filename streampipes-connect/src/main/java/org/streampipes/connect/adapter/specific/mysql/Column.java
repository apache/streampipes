package org.streampipes.connect.adapter.specific.mysql;


public class Column {
  /*private static final Map<String, Object> defaultObject;

  static {
    Map<String, Object> aMap = new HashMap<>();
    aMap.put("one", new Integer(3));
    aMap.put("varchar", new String(""));
    defaultObject = Collections.unmodifiableMap(aMap);
  }*/


  private String name;
  private String type;
  private Object defaul;

  public Column(String name, String type, Object defaul) {
    this.name = name;
    this.type = type;
    this.defaul = defaul;
  }
  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
  public Object getDefaul() {
    return defaul;
  }
}
