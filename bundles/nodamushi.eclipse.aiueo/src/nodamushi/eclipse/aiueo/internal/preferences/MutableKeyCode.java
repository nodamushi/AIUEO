package nodamushi.eclipse.aiueo.internal.preferences;

/**
 * 書き換え可能な {@link IKeyCode}の実装
 * @author nodamushi
 *
 */
public class MutableKeyCode implements IKeyCode,Cloneable{
  private int charactor;
  private boolean useShift;
  private int keyCode;


  public MutableKeyCode(IKeyCode code){
    this(code.getCharactor(),code.getKeyCode(),code.isUseShift());
  }

  public MutableKeyCode(int c,int key,boolean shift){
    this.charactor = c;
    this.useShift = shift;
    this.keyCode = key;
  }
  @Override
  public int getCharactor(){
    return charactor;
  }
  public void setCharactor(int charactor){
    this.charactor = charactor;
  }

  @Override
  public boolean isUseShift(){
    return useShift;
  }
  public void setUseShift(boolean useShift){
    this.useShift = useShift;
  }

  @Override
  public int getKeyCode(){
    return keyCode;
  }
  public void setKeyCode(int keyCode){
    this.keyCode = keyCode;
  }

  public KeyCode immutable(){
    return new KeyCode(this);
  }


  @Override
  public String toString(){
    return String.format("{%s,%d,%s}",Character.toString((char)charactor),keyCode,
        useShift?"t":"f");
  }

  @Override
  public boolean equals(Object obj){
    if(obj == this)return true;
    if(obj instanceof IKeyCode){
      return equals((IKeyCode)obj);
    }
    return false;
  }

  @Override
  public int hashCode(){
    return hashCodeBase();
  }

  @Override
  public MutableKeyCode clone(){
    try {
      return (MutableKeyCode)super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

}
