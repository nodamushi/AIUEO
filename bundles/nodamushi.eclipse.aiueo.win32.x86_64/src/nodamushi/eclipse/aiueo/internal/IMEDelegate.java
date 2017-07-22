package nodamushi.eclipse.aiueo.internal;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.internal.win32.INPUT;
import org.eclipse.swt.internal.win32.KEYBDINPUT;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.IME;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import nodamushi.eclipse.aiueo.internal.preferences.KeyCode;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager.ReplaceConfig;

@SuppressWarnings("restriction")
public class IMEDelegate implements IIMEDelegate{
  private static final String DATA_KEY="nodamushi.swt.ime.IMEDataKey";

  private static class IMEData implements Listener{
    StyledText text;
    IME ime;
    volatile int oldState=-1;//0:off,1:on,-1:none
    IMEData(StyledText t,IME ime){
      text = t;
      this.ime=ime;
      t.addDisposeListener(e->{
        if(!ime.isDisposed()){
          ime.removeListener(SWT.ImeComposition, this);
        }
      });
    }

    void init(){
      ime.setData(DATA_KEY,this);
      ime.addListener(SWT.ImeComposition, this);
      text.addListener(SWT.FocusOut, this);
    }

    @Override
    public void handleEvent(Event event){
      switch(event.type){
        case SWT.FocusOut:
          if(oldState != -1){
            if(oldState==0){
              closeIME(text);
            }
            oldState = -1;
          }
          break;
        case SWT.ImeComposition:
        if(oldState!=-1){
          long /*int*/  p = (long /*int*/)event.display.msg.lParam;
          boolean cancel = p==0 && text.getIME().getText().length()==0;
          if(cancel ||  (p & OS.GCS_RESULTSTR)!=0){
            if(oldState==0){
              closeIME(text);
            }


            oldState = -1;
          }
        }
        break;
      }
    }
  }




  private static void replace(StyledText t,IDocument d,ITextSelectHelper helper,ReplaceInfo r){
    if(!r.neadReplace()){
      return;
    }

    if(r.isSpace()){
      int pos = r.getBegin();
      helper.replaceTextRange(pos, 1, "　");
      helper.setCaretOffset(pos+1);
      return;
    }else{

      IME ime = t.getIME();
      if(ime==null){
        return;
      }

      Object data = ime.getData(DATA_KEY);
      if(data == null){
        IMEData imedata = new IMEData(t,ime);
        imedata.init();
      }

      t.getDisplay().asyncExec(()->{
        IMEData imedata = (IMEData)ime.getData(DATA_KEY);
        if(imedata.oldState!=-1){
          return;
        }
        imedata.oldState = openIME(t);
        if(imedata.oldState==-1){
          return;
        }
        /*
         * bug:
         * ループ処理が終わらないほど早くに以下のキーを離されると、
         * プログラムがキーアップ→入力ループ→（途中でユーザによるキーアップ）→入力ループ→キーダウン
         * となり、キーが押されたままになるという現象が発生する。
         *
         * どうしようね。
         *
         */
        boolean lctrl=keyUp(OS.VK_LCONTROL);
        boolean rctrl=keyUp(OS.VK_RCONTROL);
        boolean lmenu = keyUp(OS.VK_LMENU);
        boolean rmenu= keyUp(OS.VK_RMENU);
        boolean lshift = keyUp(OS.VK_LSHIFT);
        boolean rshift=keyUp(OS.VK_RSHIFT);
        keyUp(OS.VK_SHIFT);

        if(d!=null){
          try {
            d.replace(r.getBegin(), r.getLength(), "");
          } catch (BadLocationException e) {
          }
          helper.setSelection(r.getBegin(),0);
        }else{
          helper.setSelection(r.getBegin(), r.getLength());
        }

        ReplaceConfig info = PreferenceManager.getConfiguration();
        setIME(t, r.getChars(),info.getKeyboard(),
            info.isDoubleReleaseShiftWithUppercase(),
            SWT.ROMAN);
//        if(info.isSendSpace()){
//          keyDown(OS.VK_SPACE);
//          keyUp(OS.VK_SPACE);
//        }
        if(PreferenceManager.isResetCtrlKey()){
          if(lctrl){
            keyDown(OS.VK_LCONTROL);
          }
          if(rctrl){
            keyDown(OS.VK_RCONTROL);
          }
        }
        if(PreferenceManager.isResetMenuKey()){
          if(lmenu){
            keyDown(OS.VK_LMENU);
          }
          if(rmenu){
            keyDown(OS.VK_RMENU);
          }
        }
        if(PreferenceManager.isResetShiftKey()){
          if(lshift){
            keyDown(OS.VK_LSHIFT);
          }
          if(rshift){
            keyDown(OS.VK_RMENU);
          }
        }

      });
    }
  }

  private static boolean keyUp(int vkey){
    return key(vkey,true);
  }
  private static boolean keyDown(int vkey){
    return key(vkey,false);
  }

  private static boolean key(int vkey,boolean up){
    if(((OS.GetAsyncKeyState(vkey)&0x8000)!=0) == up){
      KEYBDINPUT inputs = new KEYBDINPUT();
      inputs.wVk = (short)vkey;
      inputs.dwFlags =up? OS.KEYEVENTF_KEYUP:0;
      long /*int*/  hHeap = (long /*int*/)OS.GetProcessHeap ();
      long /*int*/  pInputs = (long /*int*/)OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, INPUT.sizeof);
      OS.MoveMemory(pInputs, new int[] {OS.INPUT_KEYBOARD}, 4);
      //TODO - DWORD type of INPUT structure aligned to 8 bytes on 64 bit
      OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
      OS.SendInput (1, pInputs, INPUT.sizeof);
      OS.HeapFree (hHeap, 0, pInputs);
      return true;
    }
    return false;
  }




  private static void pressKey(char[] array,List<KeyCode> keycode,boolean doubleReleaseShift){
    long /*int*/  hHeap =(long /*int*/) OS.GetProcessHeap ();
    long /*int*/  pInputs = (long /*int*/)OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, INPUT.sizeof);
    OS.MoveMemory(pInputs, new int[] {OS.INPUT_KEYBOARD}, 4);
    for(char c:array){
      pressKey(c, hHeap, pInputs, keycode,doubleReleaseShift);
    }
    OS.HeapFree (hHeap, 0, pInputs);
  }


  private static void pressKey(char c,long /*int*/  hHeap,long /*int*/  pInputs,List<KeyCode> keycode,boolean doubleReleaseShift){
    KEYBDINPUT inputs = new KEYBDINPUT();
    boolean shift=false;
    short vkey;



    KeyCode k = keycode.get(c);
    if(k == null){
      if('a'<=c && c <= 'z'){
        vkey = (short)(c-'a'+'A');
        shift = false;
      }else{
        return;
      }
    }else{
      vkey = (short)k.getKeyCode();
      shift = k.isUseShift();
    }

    if(shift){
      inputs.dwFlags = 0;
      inputs.wVk = (short)OS.VK_SHIFT;

      OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
      OS.SendInput (1, pInputs, INPUT.sizeof);
    }

    inputs.dwFlags = 0;
    inputs.wVk = vkey;
    OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
    OS.SendInput (1, pInputs, INPUT.sizeof);

    inputs.dwFlags = OS.KEYEVENTF_KEYUP;
    OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
    OS.SendInput (1, pInputs, INPUT.sizeof);

    if(shift){
      inputs.wVk = (short)OS.VK_SHIFT;
      OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
      OS.SendInput (1, pInputs, INPUT.sizeof);
      if('A'<=c && c<='Z' && doubleReleaseShift){
        inputs.dwFlags = 0;
        OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
        OS.SendInput (1, pInputs, INPUT.sizeof);
        inputs.dwFlags = OS.KEYEVENTF_KEYUP;
        OS.MoveMemory (pInputs + OS.PTR_SIZEOF, inputs, KEYBDINPUT.sizeof);
        OS.SendInput (1, pInputs, INPUT.sizeof);
      }
    }
  }


  private static int openIME(Control c){
    long /*int*/  hWnd = (long /*int*/)c.handle;
    long /*int*/  hIMC = (long /*int*/)OS.ImmGetContext(hWnd);
    int oldState=0;
    if(hIMC!=0){

      if(!OS.ImmGetOpenStatus(hIMC)){
        OS.ImmSetOpenStatus(hIMC, true);
      }else{
        oldState=1;
      }

      if(!OS.ImmGetOpenStatus(hIMC)){
        oldState = -1;
      }

      OS.ImmReleaseContext(hWnd, hIMC);
      return oldState;
    }else{
      return -1;
    }
  }
  private static void setIME(Control c,char[] alphabets,List<KeyCode> keycode,boolean doubleRelaseShift,int inputMode){
    Shell s = c.getShell();
    int oldMode = s.getImeInputMode();
    if(oldMode!=inputMode){
      c.getShell().setImeInputMode(inputMode);
    }
    pressKey(alphabets, keycode,doubleRelaseShift);


    if(oldMode!=inputMode){
      c.getShell().setImeInputMode(oldMode);
    }
  }

  private static void closeIME(Control c){
    long /*int*/  hWnd = (long /*int*/)c.handle;
    long /*int*/  hIMC = (long /*int*/)OS.ImmGetContext(hWnd);
    if(hIMC!=0){
      if(OS.ImmGetOpenStatus(hIMC)){
        OS.ImmSetOpenStatus(hIMC, false);
      }
    }
  }

  public void action(StyledText text,boolean searchBefore){
    action(text,null,searchBefore);
  }

  public void action(StyledText text,IDocument d,ITextSelectHelper helper,boolean searchBefore){
    ReplaceInfo r;
    
    if(d!=null){
      r = ReplaceInfo.createReplaceInfo(d, helper.getCarretOffset(), searchBefore,null);
    }else{
      r = ReplaceInfo.createReplaceInfo(text.getText(), helper.getCarretOffset(), searchBefore,null);
    }
    replace(text, d,helper, r);
  }

  @Override
  public void action(Control control ,IDocument document ,boolean searchBefore){
    ITextSelectHelper helper = ITextSelectHelper.of(control);
    if(helper == null) {
      return;
    }
    if(control instanceof StyledText){
      StyledText text = (StyledText)control;
      action(text, document,helper, searchBefore);
    }
  }
  
  @Override
  public void action(IEditorPart editor ,boolean searchBefore){
    ITextOperationTarget t = editor.getAdapter(ITextOperationTarget.class);
    ITextSelectHelper helper = ITextSelectHelper.of(editor);
    if(helper == null) {
      return;
    }
    if(t instanceof ITextViewer){
      ITextViewer v = (ITextViewer) t;
      StyledText text = v.getTextWidget();
      action(text,v.getDocument(),helper,searchBefore);
    }
  }
  
}
