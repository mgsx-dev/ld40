package net.mgsx.ld40.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.mgsx.ld40.LD40;
import net.mgsx.ld40.model.Rules;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Rules.WIDTH, Rules.HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LD40();
        }
}