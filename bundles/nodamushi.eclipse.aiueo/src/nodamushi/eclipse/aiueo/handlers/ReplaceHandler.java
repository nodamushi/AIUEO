package nodamushi.eclipse.aiueo.handlers;

import javax.inject.Named;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;

import nodamushi.eclipse.aiueo.internal.IIMEDelegate;

public abstract class ReplaceHandler extends AbstractHandler{
  private static class Lazy{
    final static IIMEDelegate IMEDELEGATE=IIMEDelegate.findImplements();
  }
  private final boolean searchBefore;

  public ReplaceHandler(boolean searchBefore){
    this.searchBefore = searchBefore;
  }
  
  @Override public Object execute(ExecutionEvent event) throws ExecutionException{
    Shell shell = HandlerUtil.getActiveShell(event);
    IEditorPart editor = HandlerUtil.getActiveEditor(event);
    execute(shell, editor);
    return null;
  }

  @Execute
  public void execute(
      @Named(IServiceConstants.ACTIVE_SHELL)
      Shell shell,
      @Named(ISources.ACTIVE_EDITOR_NAME)
      @Optional
      IEditorPart editor
      ){

    if(Lazy.IMEDELEGATE==null){
      return;
    }


    Control c = shell.getDisplay().getFocusControl();

    if(c instanceof StyledText){
      StyledText styledText = (StyledText)c;
      ITextOperationTarget tt = editor.getAdapter(ITextOperationTarget.class);

      if(tt instanceof ITextViewer){
        ITextViewer v = (ITextViewer) tt;
        if(v.getTextWidget() == styledText){
          Lazy.IMEDELEGATE.action(editor, searchBefore);
        }else{
          Lazy.IMEDELEGATE.action(styledText, null, searchBefore);
        }
      }else{
        Lazy.IMEDELEGATE.action(styledText, null, searchBefore);
      }
    }
  }
}
