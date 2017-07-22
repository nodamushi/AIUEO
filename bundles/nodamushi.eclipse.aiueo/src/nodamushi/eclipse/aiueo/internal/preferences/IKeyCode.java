package nodamushi.eclipse.aiueo.internal.preferences;

/**
 * キーコード
 * @author nodamushi
 */
public interface IKeyCode{
  /**
   * キーコードに対応する文字の取得
   * @return 文字
   */
  int getCharactor();
  /**
   * シフトを押す必要があるかどうか
   * @return シフトを押す必要があるかどうか
   */
  boolean isUseShift();
  /**
   * キーコード番号
   * @return キーコード番号
   */
  int getKeyCode();


  default boolean equals(IKeyCode code){
    if(code == null)return false;
    if(code == this)return true;
    return getCharactor() == code.getCharactor() &&
        isUseShift() == code.isUseShift() &&
        getKeyCode() == code.getKeyCode();
  }

  default int hashCodeBase(){
    return getCharactor() * 31*31 + (isUseShift()?31:17) + getKeyCode();
  }

  default void asConfigText(StringBuilder sb){
    sb.append('{')
                .append((char)getCharactor())
    .append(',').append(getKeyCode())
    .append(',').append(isUseShift()?'t':'f')
    .append('}');
  }
}
