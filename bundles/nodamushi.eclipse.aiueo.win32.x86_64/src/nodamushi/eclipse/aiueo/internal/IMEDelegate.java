package nodamushi.eclipse.aiueo.internal;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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

import nodamushi.eclipse.aiueo.internal.ReplaceInfo;
import nodamushi.eclipse.aiueo.internal.preferences.KeyCode;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager.ReplaceConfig;

@SuppressWarnings("restriction")
public class IMEDelegate implements IIMEDelegate{
  private static final String DATA_KEY="nodamushi.swt.ime.IMEDataKey";

  private static class IMEData implements Listener{
    StyledText text;
    volatile int oldState=-1;//0:off,1:on,-1:none
    public IMEData(StyledText t,IME ime){
      text = t;
      t.addDisposeListener(e->{
        if(!ime.isDisposed()){
          ime.removeListener(SWT.ImeComposition, this);
        }
      });
    }

    @Override
    public void handleEvent(Event event){
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
    }
  }




  private static void replace(StyledText t,IDocument d,ReplaceInfo r){
    if(!r.neadReplace()){
      return;
    }

    if(r.isSpace()){
      int pos = r.getBegin();
      if(d!=null){
        try {
          d.replace(pos, 1,"　");
          t.setCaretOffset(pos+1);
        } catch (BadLocationException e) {
        }
        return;
      }

      t.replaceTextRange(pos, 1, "　");
      t.setCaretOffset(pos+1);

      return;
    }else{

      IME ime = t.getIME();
      if(ime==null){
        return;
      }

      Object data = ime.getData(DATA_KEY);
      if(data == null){
        IMEData imedata = new IMEData(t,ime);
        ime.setData(DATA_KEY,imedata);
        ime.addListener(SWT.ImeComposition, imedata);
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
          t.setSelection(r.getBegin(), r.getBegin());
        }else{
          t.setSelection(r.getBegin(), r.getBegin()+r.getLength());
        }

        ReplaceConfig info = PreferenceManager.getConfiguration();
        setIME(t, r.getChars(),info.getKeyboard(),
            info.isDoubleReleaseShiftWithUppercase(),
            SWT.ROMAN);
//        if(info.isSendSpace()){
//          keyDown(OS.VK_SPACE);
//          keyUp(OS.VK_SPACE);
//        }
        if(lctrl){
          keyDown(OS.VK_LCONTROL);
        }
        if(rctrl){
          keyDown(OS.VK_RCONTROL);
        }
        if(lmenu){
          keyDown(OS.VK_LMENU);
        }
        if(rmenu){
          keyDown(OS.VK_RMENU);
        }
        if(lshift){
          keyDown(OS.VK_LSHIFT);
        }
        if(rshift){
          keyDown(OS.VK_RMENU);
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

  public void action(StyledText text,IDocument d,boolean searchBefore){
    ReplaceInfo r;
    int p = text.getCaretOffset();
    if(d!=null){
      r = ReplaceInfo.createReplaceInfo(d, p, searchBefore,null);
    }else{
      r = ReplaceInfo.createReplaceInfo(text.getText(), p, searchBefore,null);
    }
    replace(text, d, r);
  }

  @Override
  public void action(Control control ,IDocument document ,boolean searchBefore){
    if(control instanceof StyledText){
      action((StyledText)control, document, searchBefore);
    }
  }
}
