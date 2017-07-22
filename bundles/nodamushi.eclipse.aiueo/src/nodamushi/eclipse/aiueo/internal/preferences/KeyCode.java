package nodamushi.eclipse.aiueo.internal.preferences;

/**
 * イミュータブルな {@link IKeyCode}実装
 * @author nodamushi
 */
public class KeyCode implements IKeyCode{
  private final int charactor;
  private final boolean useShift;
  private final int keyCode;

  public KeyCode(IKeyCode code){
    this(code.getCharactor(),code.getKeyCode(),code.isUseShift());
  }

  public KeyCode(int c,int key,boolean shift){
    this.charactor = c;
    this.useShift = shift;
    this.keyCode = key;
  }
  @Override
  public int getCharactor(){
    return charactor;
  }
  @Override
  public boolean isUseShift(){
    return useShift;
  }

  @Override
  public int getKeyCode(){
    return keyCode;
  }

  public MutableKeyCode mutable(){
    return new MutableKeyCode(this);
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
}