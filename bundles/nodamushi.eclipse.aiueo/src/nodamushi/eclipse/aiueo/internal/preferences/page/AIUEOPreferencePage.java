package nodamushi.eclipse.aiueo.internal.preferences.page;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import nodamushi.eclipse.aiueo.internal.AIUEOPlugin;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager;
import nodamushi.eclipse.aiueo.internal.preferences.PreferenceManager.ReplaceConfig;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.*;

import java.util.List;
public class AIUEOPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public AIUEOPreferencePage() {
		super(GRID);
		setPreferenceStore(AIUEOPlugin.getDefault().getPreferenceStore());
		setDescription("AIUEOの設定ページ。\n"
		    + "区切り文字には\\d(数字)と\\A(アルファベット大文字)、\\W(数字、アルファベット以外)が使えます。");
	}



	public void createFieldEditors() {
		ReplaceConfig current = PreferenceManager.getConfiguration();

		addField(new StringFieldEditor(KEY_SEPARATOR,
		    "区切り文字", getFieldEditorParent()));

		List<String> keyboards = current.getKeyboadNameList();

		addField(new ComboFieldEditor(KEY_KEYBOARD,
		    "キーボード配列",
        keyboards.stream()
        .map(s->new String[]{s,s})
        .toArray(String[][]::new)
        , getFieldEditorParent()));

		addField(
			new BooleanFieldEditor(KEY_SEND_SPACE,
				"IME起動後、スペースキーを入力する",
				getFieldEditorParent()));
	  addField(
	      new BooleanFieldEditor(KEY_DOUBLE_RELEASE_SHIFT_ALPHABET,
	        "大文字アルファベットを入力したとき、Shiftを2回押してロック解除する",
	        getFieldEditorParent()));

	  addField(
        new BooleanFieldEditor(KEY_REDOWN_CTRL,
          "IMEにアルファベット入力を完了後、入力開始前のControlキーの状態に戻す",
          getFieldEditorParent()));

    addField(
        new BooleanFieldEditor(KEY_REDOWN_SHIFT,
          "IMEにアルファベット入力を完了後、入力開始前のShiftキーの状態に戻す",
          getFieldEditorParent()));
    addField(
        new BooleanFieldEditor(KEY_REDOWN_MENU,
          "IMEにアルファベット入力を完了後、入力開始前のMenuキーの状態に戻す",
          getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}