package com.JingyuYao.ClassicTanks.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.JingyuYao.ClassicTanks.ClassicTanks;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(640, 640);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new ClassicTanks();
        }
}