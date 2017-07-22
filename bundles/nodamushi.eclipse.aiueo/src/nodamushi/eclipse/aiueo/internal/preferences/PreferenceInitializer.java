package nodamushi.eclipse.aiueo.internal.preferences;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import nodamushi.eclipse.aiueo.internal.AIUEOPlugin;


public class PreferenceInitializer extends AbstractPreferenceInitializer{

  @Override
  public void initializeDefaultPreferences(){
    IPreferenceStore store = AIUEOPlugin.getDefault().getPreferenceStore();
    store.setDefault(KEY_SEPARATOR, VALUE_SEPARATOR);
    store.setDefault(KEY_KEYBOARDS, VALUE_KEYBOADS);
    store.setDefault(KEY_KEYBOARD, VALUE_KEYBOARD);
    store.setDefault(KEY_SEND_SPACE, VALUE_SEND_SPACE);
    store.setDefault(KEY_DOUBLE_RELEASE_SHIFT_ALPHABET, VALUE_DOUBLE_RELEASE_SHIFT_ALPHABET);
    store.setDefault(KEY_REDOWN_CTRL, VALUE_REDOWN_CTRL);
    store.setDefault(KEY_REDOWN_SHIFT, VALUE_REDOWN_SHIFT);
    store.setDefault(KEY_REDOWN_MENU, VALUE_REDOWN_MENU);
  }
}
