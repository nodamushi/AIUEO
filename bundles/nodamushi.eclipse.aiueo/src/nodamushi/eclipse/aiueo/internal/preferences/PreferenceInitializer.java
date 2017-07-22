package nodamushi.eclipse.aiueo.internal.preferences;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.KEY_DOUBLE_RELEASE_SHIFT_ALPHABET;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.KEY_KEYBOARD;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.KEY_KEYBOARDS;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.KEY_SEND_SPACE;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.KEY_SEPARATOR;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.VALUE_DOUBLE_RELEASE_SHIFT_ALPHABET;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.VALUE_KEYBOADS;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.VALUE_KEYBOARD;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.VALUE_SEND_SPACE;
import static nodamushi.eclipse.aiueo.internal.preferences.PreferenceConstants.VALUE_SEPARATOR;

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
  }
}
