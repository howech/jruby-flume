/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infochimps.flume.jruby;

import com.cloudera.flume.conf.Context;
import com.cloudera.flume.conf.SourceFactory.SourceBuilder;
import com.cloudera.flume.core.EventSource;
import com.cloudera.util.Pair;
import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * Simple source that creates a ruby class determinded by a script.
 *
 * @author Chris Howe
 */
public class JRubySource extends EventSource.Base {

    public static SourceBuilder builder() {
        // construct a new parameterized source
        return new SourceBuilder() {
            
            public EventSource build(Context ctx,String... argv) {
                Preconditions.checkArgument(argv.length >= 1,
                        "usage: jRubySource script.rb [optional arguments]");

                EventSource s = null;

                ScriptEngine jruby = new ScriptEngineManager().getEngineByName("ruby");
                //jruby.put(ScriptEngine.ARGV, Arrays.copyOfRange(argv, 1, argv.length));
                Bindings bindings = new SimpleBindings();
                bindings.put("context", ctx);
                bindings.put("args", Arrays.copyOfRange(argv, 1, argv.length));
                try {
                    s = (EventSource) jruby.eval(new BufferedReader(new FileReader(argv[0])),bindings);
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("Script file not found: " + argv[0], e);
                } catch (ScriptException e) {
                    throw new IllegalArgumentException("Error executing script: " + argv[0], e);
                }

                return s;
            }

            @Override
            public EventSource build(String... strings) {
                return build(null,strings);
            }
        };
    }

    /**
     * This is a special function used by the SourceFactory to pull in this class
     * as a plugin source.
     */
    public static List<Pair<String, SourceBuilder>> getSourceBuilders() {
        List<Pair<String, SourceBuilder>> builders =
                new ArrayList<Pair<String, SourceBuilder>>();
        builders.add(new Pair<String, SourceBuilder>("jRubySource", builder()));
        return builders;
    }
}
