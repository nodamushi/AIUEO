package nodamushi.eclipse.aiueo.internal.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
  /** セパレータ設定のPreferenceキー */
  public static final String KEY_SEPARATOR="separator"; //$NON-NLS-1$
  /** キーボードのキー設定群設定のPreferenceキー */
  public static final String KEY_KEYBOARDS="keyboards"; //$NON-NLS-1$
  /** 選択キーボード設定のPreferenceキー */
  public static final String KEY_KEYBOARD="keyboard"; //$NON-NLS-1$
  /** 入力完了後、スペースキーを送信する設定のPreferenceキー */
  public static final String KEY_SEND_SPACE="send_space"; //$NON-NLS-1$
  /** 大文字を入力した後、shiftを2回押してロック解除する設定のPreferenceキー */
  public static final String KEY_DOUBLE_RELEASE_SHIFT_ALPHABET="shift_doubleRelease_alphabet"; //$NON-NLS-1$

  /**
   * 初期セパレータ設定
   */
  public static final String VALUE_SEPARATOR="\\d\\A\\\\{}()[]*/-+=^~|%&#!?_$\"'@`"; //$NON-NLS-1$
  /**
   * 初期キーボード名
   */
  public static final String VALUE_KEYBOARD="JIS"; //$NON-NLS-1$
  /**
   * 初期スペース送信設定
   */
  public static final boolean VALUE_SEND_SPACE=true;
  /**
   * 大文字を入力した後、shiftを2回押してロック解除する初期設定
   */
  public static final boolean VALUE_DOUBLE_RELEASE_SHIFT_ALPHABET=true;

  /**
   初期設定用JIS配列キーボード
  */
  public static final String VALUE_JIS_KEY_DATA=
      "{!,49,t}{\",50,t}{#,51,t}{$,52,t}{%,53,t}{&,54,t}" //$NON-NLS-1$
      + "{',55,t}{(,56,t}{),57,t}{*,186,t}{+,187,t}{,,188,f}" //$NON-NLS-1$
      + "{-,189,f}{.,190,f}{/,191,f}{0,48,f}{1,49,f}{2,50,f}" //$NON-NLS-1$
      + "{3,51,f}{4,52,f}{5,53,f}{6,54,f}{7,55,f}{8,56,f}" //$NON-NLS-1$
      + "{9,57,f}{:,186,f}{;,187,f}{<,188,t}{=,189,t}{>,190,t}" //$NON-NLS-1$
      + "{?,191,t}{@,192,f}" //$NON-NLS-1$
      + "{A,65,t}{B,66,t}{C,67,t}{D,68,t}{E,69,t}{F,70,t}" //$NON-NLS-1$
      + "{G,71,t}{H,72,t}{I,73,t}{J,74,t}{K,75,t}{L,76,t}" //$NON-NLS-1$
      + "{M,77,t}{N,78,t}{O,79,t}{P,80,t}{Q,81,t}{R,82,t}" //$NON-NLS-1$
      + "{S,83,t}{T,84,t}{U,85,t}{V,86,t}{W,87,t}{X,88,t}" //$NON-NLS-1$
      + "{Y,89,t}{Z,90,t}" //$NON-NLS-1$
      + "{[,219,f}{\\,226,f}{],221,f}{^,222,f}{_,226,t}{`,192,t}" //$NON-NLS-1$
      + "{a,65,f}{b,66,f}{c,67,f}{d,68,f}{e,69,f}{f,70,f}" //$NON-NLS-1$
      + "{g,71,f}{h,72,f}{i,73,f}{j,74,f}{k,75,f}{l,76,f}" //$NON-NLS-1$
      + "{m,77,f}{n,78,f}{o,79,f}{p,80,f}{q,81,f}{r,82,f}" //$NON-NLS-1$
      + "{s,83,f}{t,84,f}{u,85,f}{v,86,f}{w,87,f}{x,88,f}" //$NON-NLS-1$
      + "{y,89,f}{z,90,f}" //$NON-NLS-1$
      + "{{,219,t}{|,220,t}{},221,t}{~,222,t}"; //$NON-NLS-1$

  /**
   * キーボードのキー情報初期設定。
   */
  public static final String VALUE_KEYBOADS="JIS="+VALUE_JIS_KEY_DATA; //$NON-NLS-1$




}
