package spreadsheet;

/**
 * Created by apple on 25/02/2016.
 */
public class ValueHolder {
  private Double d;

  public ValueHolder(Double d){
    this.d=d;
  }

  public Double getDoubleValue(){
    return this.d;
  }

  public void setValue(Double d){
    this.d=d;
  }
}
