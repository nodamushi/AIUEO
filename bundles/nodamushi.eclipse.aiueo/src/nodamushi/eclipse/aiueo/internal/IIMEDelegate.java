package nodamushi.eclipse.aiueo.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;

public interface IIMEDelegate{
  /**
   *
   * @param control IMEによる置換をする {@link Control}。今のところ {@link StyledText}のみ渡される。
   * @param document ドキュメント。nullの場合あり
   * @param searchBefore キャレット位置から前方向に置換するか、後ろ方向に置換するか。
   */
  void action(Control control,IDocument document,boolean searchBefore);
  
  void action(IEditorPart editor,boolean searchBefore);
  
  /**
   * 拡張ポイントによる実装を発見する。
   * @return {@link IIMEDelegate}の実装
   */
  public static IIMEDelegate findImplements(){
    for(IConfigurationElement ce : Platform.getExtensionRegistry().
        getConfigurationElementsFor("nodamushi.eclipse.aiueo.imeDelegate")){//$NON-NLS-1$
      try {
        Object o = ce.createExecutableExtension("class");//$NON-NLS-1$
        if(o instanceof IIMEDelegate){
          return (IIMEDelegate)o;
        }
      } catch (CoreException e) {
        AIUEOPlugin.getDefault().getLog().log(e.getStatus());
      }
    }
    Status s = new Status(
        IStatus.ERROR,
        AIUEOPlugin.PLUGIN_ID,
        "Internal error. IIMEDelegate is not found");
    AIUEOPlugin.getDefault().getLog().log(s);
    return null;
  }
}
