package nodamushi.eclipse.aiueo.internal.preferences;

import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import nodamushi.eclipse.aiueo.internal.AIUEOPlugin;

/**
 * 設定のマネージャー
 * @author nodamushi
 *
 */
public class PreferenceManager{

  /**
   * 置換設定情報
   * @author nodamushi
   *
   */
  public static final class ReplaceConfig{

    private final String separator;
    private final BitSet separators;
    private final Map<String, List<KeyCode>> keyboardMap;
    private final List<String> keyboardNameList;
    private final String keyboardName;
    private final boolean sendSpace;
    private final boolean doubleReleaseShift;
    /**
     * {@link PreferenceStore}を用いて置換設定を作成します。
     */
    public ReplaceConfig(){
      this(null,null,null,null,null);
    }
    /**
     * 置換設定を作成する。<br>
     * nullの引数は{@link PreferenceStore}から対応する値を取得します。
     * @param separatorText セパレータ設定。nullである場合は{@link PreferenceStore}から取得する。
     * @param keyboardData キーボードのキーコード設定。nullである場合は{@link PreferenceStore}から取得する。
     * @param keyboardName キーボード名設定。nullである場合は{@link PreferenceStore}から取得する。
     * @param sendSpace スペース送信設定。nullである場合は{@link PreferenceStore}から取得する。
     * @param doubleReleaseShift シフト解除設定。nullである場合は{@link PreferenceStore}から取得する。
     */
    public ReplaceConfig(
        String separatorText,
        String keyboardData,
        String keyboardName,
        Boolean sendSpace,
        Boolean doubleReleaseShift
        ){
      IPreferenceStore p = AIUEOPlugin.getDefault().getPreferenceStore();
      separator = getOrElse(separatorText,p,KEY_SEPARATOR);
      separators = decodeSeparator(separator);

      Map<String, List<KeyCode>> map = new HashMap<>();
      List<String> keyboadlist = new ArrayList<>();
      for(String s:getOrElse(keyboardData,p,KEY_KEYBOARDS).split("\n")){
        if(s.isEmpty())continue;
        int index = s.indexOf('=');
        if(index <= 0 || index == s.length()-1 ){
          continue;
        }
        String name = s.substring(0, index);
        String data = s.substring(index+1);
        List<KeyCode> list = Arrays.asList(decodeKeyCodeConfigurationText(data));
        map.put(name,Collections.unmodifiableList( list));
        keyboadlist.add(name);
      }
      this.keyboardMap = Collections.unmodifiableMap(map);
      this.keyboardNameList =Collections.unmodifiableList(keyboadlist);
      String kbname = getOrElse(keyboardName,p,KEY_KEYBOARD);
      this.keyboardName = map.containsKey(kbname)?kbname:"JIS";
      this.sendSpace = getOrElse(sendSpace,p,KEY_SEND_SPACE);
      this.doubleReleaseShift = getOrElse(doubleReleaseShift,p,KEY_DOUBLE_RELEASE_SHIFT_ALPHABET);
    }

    /**
     * nameと同じ名前のキーボードの {@link KeyCode}リストを返す。
     * @param name キーボード名
     * @return {@link KeyCode}リスト。キーボードが存在しない場合はnull。
     */
    public List<KeyCode> getKeyboard(String name){
      return keyboardMap.get(name);
    }


    /**
     * 現在選択されているキーボードの{@link KeyCode}リストを返す。
     * @return 現在選択されているキーボードの{@link KeyCode}リスト。nullは返らない。
     */
    public List<KeyCode> getKeyboard(){
      List<KeyCode> l = getKeyboard(keyboardName);
      return l == null?JIS_KEY_LIST:l;
    }

    /**
     * 現在選択されているキーボード名
     * @return 現在選択されているキーボード名
     */
    public String getKeyboardName(){return keyboardName;}

    /**
     * 全部のキーボード名と {@link KeyCode}の対応 {@link Map}を返す。
     * @return 全部のキーボード名と {@link KeyCode}の対応 {@link Map}
     */
    public Map<String, List<KeyCode>> getKeybordMap(){
      return keyboardMap;
    }

    /**
     * セパレータ設定の文字列
     * @return セパレータ設定の文字列
     */
    public String getSeparator(){
      return separator;
    }

    /**
     * cがセパレータになっているかどうかを判断する
     * @param c 判断する文字列
     * @return cがセパレータかどうか。
     */
    public boolean isSeparator(char c){
      return c<= ' ' || 127 <= c?true:separators.get(c);
    }

    /**
     * 文字を入力語、スペースを送信するかどうか。
     * @return 文字を入力語、スペースを送信するかどうか
     */
    public boolean isSendSpace(){
      return sendSpace;
    }
    /**
     * キーボード名のリストを返す。
     * @return キーボード名のリスト
     */
    public List<String> getKeyboadNameList(){
      return keyboardNameList;
    }
    /**
     * 大文字を入力語、シフトを2回押してロックを解除するかどうか。
     * @return 大文字を入力語、シフトを2回押してロックを解除するかどうか。
     */
    public boolean isDoubleReleaseShiftWithUppercase(){
      return doubleReleaseShift;
    }
  }


  /**
   * {@link KeyCode}のリストから設定様文字列を作る。<br><br>
   *
   * 文法：<br>
   * {キャラクタ,キーコード,シフトを押すかどうか}*<br>
   * キャラクタは一文字。キーコードは10進数の127以下の正の整数。
   * シフトを押すかどうかはt,fのどちらか。
   * @param keycode 設定する情報
   * @return 設定様文字列
   */
  public static String asKeyCodeConfigurationText(List<? extends IKeyCode> keycode){
    if(keycode==null){
      return "";
    }
    StringBuilder sb = new StringBuilder();
    keycode.stream()
    .filter(o->o!=null)
    .sorted((o1,o2)->o1.getCharactor()-o2.getCharactor())
    .forEach(o->o.asConfigText(sb));
    return sb.toString();
  }



  private static final Pattern PATTERN=Pattern.compile("\\{(.),(\\d+),(t|f)\\}");
  /**
   * 文字列から {@link KeyCode}の配列を作る。<br><br>
   *
   * 文法：<br>
   * {キャラクタ,キーコード,シフトを押すかどうか}*<br>
   * キャラクタは一文字。キーコードは10進数の127以下の正の整数。
   * シフトを押すかどうかはt,fのどちらか。
   * @param str 文法を参照
   * @return {@link KeyCode}の配列
   */
  public static KeyCode[] decodeKeyCodeConfigurationText(String str){
    Matcher m = PATTERN.matcher(str);
    KeyCode[] code = new KeyCode[128];
    while(m.find()){
      char c = m.group(1).charAt(0);
      if(c >= 127){
        continue;
      }
      String num = m.group(2);
      boolean shift = m.group(3).charAt(0)=='t';
      code[c] = new KeyCode(c, Integer.parseInt(num), shift);
    }
    return code;
  }

  private static final KeyCode[] JIS_KEY= decodeKeyCodeConfigurationText(VALUE_JIS_KEY_DATA);
  private static final List<KeyCode>JIS_KEY_LIST = Collections.unmodifiableList(Arrays.asList(JIS_KEY));


  /**
   * Jisの {@link KeyCode}のリスト
   * @return Jisの {@link KeyCode}の不変リスト
   */
  public static List<KeyCode> getJisKeyList(){
    return JIS_KEY_LIST;
  }

  /**
   * セパレータ情報を{@link BitSet}に直す。<br>
   * @param str
   * @return
   */
  private static BitSet decodeSeparator(String str){
    BitSet bitset =new BitSet(128);
    for(int i=0;i<=' ';i++){
      bitset.set(i);
    }
    bitset.set(127);//127はアスキーでdel

    boolean escape = false;
    for(int i=0,e=str.length();i<e;i++){
      char c = str.charAt(i);
      if(c >= 127){
        escape = false;
        continue;
      }
      if(c == '\\'){
        escape = true;
        continue;
      }else if(escape){
        escape = false;
        switch(c){
          case 'A':
            bitset.set('A', 'Z'+1);
            break;
          case 'd':
            bitset.set('0', '9'+1);
            break;
          case 'W':
            bitset.set(0,'0');
            bitset.set('9'+1,'A');
            bitset.set('Z'+1,'a');
            bitset.set('z'+1,128);
            break;
          case '\\':
            bitset.set('\\');
            break;
          default:
            bitset.set('\\');
            bitset.set(c);
        }
      }else{
        bitset.set(c);
      }
    }
    return bitset;
  }

  /**
   * valueがnullならstoreから値を取得する。
   * storeに値が設定されていない場合は初期値を取得する。
   */
  private static String getOrElse(String value,IPreferenceStore store,String key){
    if(value!=null){
      return value;
    }
    return store.isDefault(key)?store.getDefaultString(key):store.getString(key);
  }
  /**
   * valueがnullならstoreから値を取得する。
   * storeに値が設定されていない場合は初期値を取得する。
   */
  private static boolean getOrElse(Boolean value,IPreferenceStore store,String key){
    if(value!=null){
      return value;
    }
    return store.isDefault(key)?store.getDefaultBoolean(key):store.getBoolean(key);
  }


  //------------------------------------------------------------------------

  private static volatile ReplaceConfig CACHE = null;

  private static void clearConfigurationCache(){
    CACHE = null;
  }

  /**
   * 現在の置換設定を取得する
   * @return 現在の置換設定
   */
  public static ReplaceConfig getConfiguration(){
    ReplaceConfig config = CACHE;
    if(config == null){
      CACHE = config= new ReplaceConfig();
    }
    return config;
  }

  public static boolean isResetCtrlKey(){
    IPreferenceStore p = AIUEOPlugin.getDefault().getPreferenceStore();
    return p.getBoolean(KEY_REDOWN_CTRL);
  }

  public static boolean isResetMenuKey(){
    IPreferenceStore p = AIUEOPlugin.getDefault().getPreferenceStore();
    return p.getBoolean(KEY_REDOWN_MENU);
  }

  public static boolean isResetShiftKey(){
    IPreferenceStore p = AIUEOPlugin.getDefault().getPreferenceStore();
    return p.getBoolean(KEY_REDOWN_SHIFT);
  }

  static{
    IPreferenceStore s = AIUEOPlugin.getDefault().getPreferenceStore();
    s.addPropertyChangeListener(e->clearConfigurationCache());
  }
}
