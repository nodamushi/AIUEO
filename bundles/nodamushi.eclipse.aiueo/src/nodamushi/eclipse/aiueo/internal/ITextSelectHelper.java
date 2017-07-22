package nodamushi.eclipse.aiueo.internal;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;

public interface ITextSelectHelper{
  int getCarretOffset();
  void replaceTextRange(int pos,int length,String text);
  void setCaretOffset(int pos);
  void setSelection(int begin,int length);
  
  static ITextSelectHelper of(IEditorPart editor ) {
    ITextOperationTarget t = editor.getAdapter(ITextOperationTarget.class);
    if(t instanceof ITextViewer){
      ITextViewer v = (ITextViewer) t;
      ISelectionProvider p = v.getSelectionProvider();
      return new ITextSelectHelper(){
        
        @Override
        public void setSelection(int begin ,int length){
          p.setSelection(new TextSelection(begin, length));
        }
        
        @Override
        public void setCaretOffset(int pos){
          p.setSelection(new TextSelection(pos, 0));
        }
        
        @Override
        public void replaceTextRange(int pos ,int length ,String text){
          try {
            v.getDocument().replace(pos, length, text);
          } catch (BadLocationException e) {
            e.printStackTrace();
          }
        }
        
        @Override
        public int getCarretOffset(){
          ISelection s = p.getSelection();
          return s instanceof ITextSelection?((ITextSelection)p.getSelection()).getOffset():v.getTextWidget().getCaretOffset();
        }
      };
    }
    return null;
  }
  
  static ITextSelectHelper of(Control c) {
    if(c instanceof StyledText) {
      StyledText t = (StyledText) c;
      return new ITextSelectHelper(){
        
        @Override
        public void setSelection(int begin ,int length){
          t.setSelection(begin, begin+length);
        }
        
        @Override
        public void setCaretOffset(int pos){
          t.setCaretOffset(pos);
        }
        
        @Override
        public void replaceTextRange(int pos ,int length ,String text){
          t.replaceTextRange(pos, length, text);
        }
        
        @Override
        public int getCarretOffset(){
          return t.getCaretOffset();
        }
      };
    }
    return null;
  }
  
}
