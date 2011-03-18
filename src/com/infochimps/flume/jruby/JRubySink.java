/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.infochimps.flume.jruby;

import com.cloudera.flume.conf.Context;
import com.cloudera.flume.conf.SinkFactory.SinkBuilder;
import com.cloudera.flume.core.EventSink;
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
public class JRubySink extends EventSink.Base {

    public static SinkBuilder builder() {
        // construct a new parameterized source
        return new SinkBuilder() {

            @Override
            public EventSink build(Context cntxt, String... argv) {
                Preconditions.checkArgument(argv.length >= 1,
                        "usage: jRubySource script.rb [optional arguments]");

                EventSink s = null;

                ScriptEngine jruby = new ScriptEngineManager().getEngineByName("ruby");
               // jruby.put(ScriptEngine.ARGV, Arrays.copyOfRange(argv, 1, argv.length));
                Bindings bindings = new SimpleBindings();
                bindings.put("context", cntxt);
                bindings.put("args", Arrays.copyOfRange(argv, 1, argv.length));
                try {
                    s = (EventSink) jruby.eval(new BufferedReader(new FileReader(argv[0])),bindings);
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("Script file not found: " + argv[0], e);
                } catch (ScriptException e) {
                    throw new IllegalArgumentException("Error executing script: " + argv[0], e);
                }

                return s;
            }

            @Override
            public EventSink build(String... strings) {
                return build(null,strings);
            }

        };
    }

    /**
     * This is a special function used by the SourceFactory to pull in this class
     * as a plugin source.
     */
    public static List<Pair<String, SinkBuilder>> getSinkBuilders() {
        List<Pair<String, SinkBuilder>> builders =
                new ArrayList<Pair<String, SinkBuilder>>();
        builders.add(new Pair<String, SinkBuilder>("jRubySink", builder()));
        return builders;
    }
}
