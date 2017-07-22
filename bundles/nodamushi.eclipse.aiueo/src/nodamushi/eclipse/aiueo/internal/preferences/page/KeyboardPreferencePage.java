package nodamushi.eclipse.aiueo.internal.preferences.page;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import nodamushi.eclipse.aiueo.internal.AIUEOPlugin;
import nodamushi.eclipse.aiueo.internal.preferences.KeyCode;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager.ReplaceConfig;

public class KeyboardPreferencePage extends PreferencePage
implements IWorkbenchPreferencePage{
  private Button addButton;
  private Button deleteButton;
  private Text keycodeText;
  private org.eclipse.swt.widgets.List keyboardList;


  private ReplaceConfig initConfig;

  private String[] keycodes={""};
  private Composite composite_1;




  public KeyboardPreferencePage() {
  }

  private boolean isChanged(){
    String[] keycords = this.keycodes;
    String[] keyboards = keyboardList.getItems();

    List<String> nameList = initConfig.getKeyboadNameList();
    if(nameList.size()!=keyboards.length){
      return true;
    }
    for(int i=0;i<keyboards.length;i++){
      String name = keyboards[i];
      String cord = keycords[i];
      if(nameList.contains(name)){
        List<KeyCode> list1 = Arrays.asList(PreferenceManager.decodeKeyCodeConfigurationText(cord));
        List<KeyCode> list2 = initConfig.getKeyboard(name);
        if(!list1.equals(list2)){
          return true;
        }
      }else{
        return true;
      }
    }
    return false;
  }


  @Override
  protected void performDefaults(){
    init(new ReplaceConfig(null, PreferenceConstants.VALUE_KEYBOADS, null, null, null));
    super.performDefaults();
  }

  @Override
  public boolean performOk(){
    if(isChanged()){
      String[] keycords = this.keycodes;
      String[] keyboards = keyboardList.getItems();
      StringBuilder sb = new StringBuilder();
      for(int i=0;i<keyboards.length;i++){
        if(i!=0){
          sb.append('\n');
        }
        String name = keyboards[i];
        String cord = keycords[i];
        List<KeyCode> list = Arrays.asList(PreferenceManager.decodeKeyCodeConfigurationText(cord));
        String saveCode = PreferenceManager.asKeyCodeConfigurationText(list);
        sb.append(name).append('=').append(saveCode);
      }
      AIUEOPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.KEY_KEYBOARDS, sb.toString());
    }



    return true;
  }



  @Override
  public void init(IWorkbench workbench){

  }


  protected void init(ReplaceConfig conf){
    initConfig = conf;

    List<String> nameList = conf.getKeyboadNameList();
    String[] name = nameList.toArray(new String[nameList.size()]);
    String[] keycords = nameList.stream()
        .map(s->conf.getKeyboard(s))
        .map(l->PreferenceManager.asKeyCodeConfigurationText(l))
        .toArray(String[]::new);

    String defaultName = conf.getKeyboardName();
    int index =0;
    for(int i=0,e=name.length;i<e;i++){
      if(defaultName.equals(name[i])){
        index = i;
      }
    }

    keyboardList.setItems(name);
    keyboardList.setSelection(index);
    modifyLock=true;
    keycodeText.setText(keycords[index]);
    modifyLock=false;
    this.keycodes=keycords;
  }

  private boolean modifyLock = false;

  @Override
  protected Control createContents(Composite parent){
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    composite_1 = new Composite(composite, SWT.NONE);
    composite_1.setLayout(new GridLayout(2, false));
    GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
    gd_composite_1.minimumWidth = 200;
    composite_1.setLayoutData(gd_composite_1);

    addButton = new Button(composite_1, SWT.NONE);
    GridData gd_addButton = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
    gd_addButton.minimumWidth = 30;
    addButton.setLayoutData(gd_addButton);
    addButton.setText("+");

    addButton.addSelectionListener(new SelectionListener(){

      @Override  public void widgetSelected(SelectionEvent e){
        addNewKeyboard();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){}
    });

    deleteButton = new Button(composite_1, SWT.NONE);
    GridData gd_deleteButton = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
    gd_deleteButton.minimumWidth = 30;
    deleteButton.setLayoutData(gd_deleteButton);
    deleteButton.setText("-");

    deleteButton.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        delete(getSelectedIndex());
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){
      }
    });

    Label lblKeycode = new Label(composite, SWT.NONE);
    lblKeycode.setText("KeyCode設定\n{キー文字,キーナンバー,シフトが必要かどうか}*");

    keyboardList =new org.eclipse.swt.widgets.List(composite, SWT.BORDER);
    GridData gd_keyboardList = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
    gd_keyboardList.minimumWidth = 100;
    keyboardList.setLayoutData(gd_keyboardList);

    keycodeText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    GridData gd_keycodeText = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
    gd_keycodeText.heightHint = 250;
    gd_keycodeText.widthHint = 350;
    keycodeText.setLayoutData(gd_keycodeText);


    init(PreferenceManager.getConfiguration());

    if(keycodes.length==1){
      deleteButton.setEnabled(false);
    }
    keyboardList.addSelectionListener(new SelectionListener(){

      @Override
      public void widgetSelected(SelectionEvent e){
        modifyLock = true;
        int i = getSelectedIndex();
        keycodeText.setText(keycodes[i]);
        modifyLock=false;
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e){
      }
    });

    keycodeText.addModifyListener(e->{
      if(modifyLock)return;
      int index = getSelectedIndex();
      if(index <keycodes.length){
        keycodes[index] = keycodeText.getText();
        setValid(isChanged());
      }
    });
    return null;
  }
  private int getSelectedIndex(){
    int index = keyboardList.getSelectionIndex();
    return index < 0? 0:index;
  }

  public boolean isDeletable(){
    return keyboardList.getItemCount() > 1;
  }

  public void delete(int index){
    if(!isDeletable())return;
    int size = keycodes.length-1;
    int currentSelect = keyboardList.getSelectionIndex();

    String[] oldKeyboards = keyboardList.getItems();
    String[] oldKeycodes = keycodes;

    String[] newKeyboards = new String[size];
    String[] newKeycodes = new String[size];

    System.arraycopy(oldKeycodes, 0, newKeycodes, 0, index);
    System.arraycopy(oldKeyboards,0, newKeyboards,0, index);

    System.arraycopy(oldKeycodes, index+1, newKeycodes, index, size-index);
    System.arraycopy(oldKeyboards,index+1, newKeyboards,index, size-index);

    keycodes=newKeycodes;

    int select = currentSelect < size?currentSelect:currentSelect-1;

    if(select!=currentSelect){
      keyboardList.setSelection(select);
    }

    keyboardList.setItems(newKeyboards);
    modifyLock=true;
    keycodeText.setText(keycodes[select]);
    modifyLock=false;
    if(keycodes.length==1){
      deleteButton.setEnabled(false);
    }
  }

  public void addNewKeyboard(){
    int size = keycodes.length+1;
    String[] oldKeyboards = keyboardList.getItems();
    String[] oldKeycodes = keycodes;


    String baseName = "Keyboard";
    int num=0;
    String name;
    boolean doContinue;
    do{
      if(num==0){
        name =baseName;
      }else{
        name = baseName+num;
      }
      doContinue=false;
      for(int i=0;i<oldKeyboards.length;i++){
        if(oldKeyboards[i].equals(name)){
          num++;
          doContinue=true;
          break;
        }
      }
    }while(doContinue);
    InputDialog dialog = new InputDialog(getShell(), "New Keyboard", "Set Keyboard name", name, null);
    if(dialog.open()==Window.OK){
      String n = dialog.getValue();
      String[] newKeyboards = Arrays.copyOf(oldKeyboards, size);
      String[] newKeycodes = Arrays.copyOf(oldKeycodes, size);
      newKeycodes[size-1] = "";
      newKeyboards[size-1] = n==null||n.isEmpty()?name:n;
      keycodes =newKeycodes;
      keyboardList.setItems(newKeyboards);
      keyboardList.setSelection(size-1);
      modifyLock=true;
      keycodeText.setText(keycodes[size-1]);
      modifyLock=false;
      deleteButton.setEnabled(true);
    }
  }
}
