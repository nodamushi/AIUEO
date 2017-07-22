package nodamushi.eclipse.aiueo.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * アクティベータ
 */
public class AIUEOPlugin extends AbstractUIPlugin {

  /** plug-in ID*/
	public static final String PLUGIN_ID = "nodamushi.eclipse.aiueo"; //$NON-NLS-1$
	private static AIUEOPlugin plugin;
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * シェアインスタンスを返す
	 * @return シェアインスタンスを返す
	 */
	public static AIUEOPlugin getDefault() {
		return plugin;
	}
}
