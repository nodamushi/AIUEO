package nodamushi.eclipse.aiueo.internal;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager.ReplaceConfig;

public class ReplaceInfo{


  private final int begin;
  private final int length;
  private final char[] chars;

  public ReplaceInfo(){
    length = 0;
    begin = 0;
    chars = new char[0];
  }

  public ReplaceInfo(int begin,int length,char[] chars){
    this.length = length;
    this.begin = begin;
    this.chars = chars;
  }

  public boolean neadReplace(){
    return length!=0;
  }

  public boolean isSpace(){
    return length == 1 && chars[0] == ' ';
  }

  public int getBegin(){
    return begin;
  }

  public int getLength(){
    return length;
  }

  public char[] getChars(){
    return chars;
  }





  /**
   * 置換情報作成する
   * @param d 対象ドキュメント
   * @param baseOffset 検索開始位置
   * @param searchBefore 前方検索か、後方検索か
   * @param configuration 置換設定
   * @return
   */
  public static ReplaceInfo createReplaceInfo(
      IDocument d,
      int baseOffset,boolean searchBefore,
      ReplaceConfig configuration){

    final int length = d.getLength();
    if((searchBefore && baseOffset==0)
        ||(!searchBefore && length<=baseOffset)){
      return new ReplaceInfo();
    }

    try{

      // 置換範囲の検索
      // begin:置換範囲の最初（含む）
      // end  :置換範囲の最後（含まず）
      int begin=baseOffset,end=baseOffset;

      final ReplaceConfig conf = configuration==null?PreferenceManager.getConfiguration():configuration;

      if(searchBefore){
        while(begin!=0){
          final char c = d.getChar(begin-1);
          if(conf.isSeparator(c)){
            break;
          }
          begin--;
        }
        if(begin == end && begin!=0 ){
          final char c=d.getChar(begin-1);
          if( ' '<= c && c <= 127){
            begin--;
          }
        }
      }else{
        while(end!=length){
          final char c = d.getChar(end);
          if(conf.isSeparator(c)){
            break;
          }
          end++;
        }
        if(begin == end && end!=length){
          final char c=d.getChar(end);
          if( ' '<= c && c <= 127){
            end++;
          }
        }
      }

      final char[] chars= new char[end-begin];
      for(int i=begin;i<end;i++){
        chars[i-begin] = d.getChar(i);
      }
      return new ReplaceInfo(begin, end-begin,chars);
    }catch(BadLocationException e){
      return new ReplaceInfo();
    }
  }




  public static ReplaceInfo createReplaceInfo(String d,int baseOffset,boolean searchBefore,ReplaceConfig configuration){
    return createReplaceInfo(new Document(d), baseOffset, searchBefore,configuration);
  }



}