package com.infochimps.flume.jruby;

import java.util.ArrayList;
import java.util.List;

import com.cloudera.flume.conf.Context;
import com.cloudera.flume.conf.SinkFactory.SinkDecoBuilder;
import com.cloudera.flume.core.EventSink;
import com.cloudera.flume.core.EventSinkDecorator;
import com.cloudera.util.Pair;
import com.google.common.base.Preconditions;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Arrays;
import javax.script.Bindings;
import javax.script.SimpleBindings;

/**
 * Simple class that sets up a jruby based decorator.
 *
 * @author Chris Howe
 */
public class JRubyDecorator<S extends EventSink> extends EventSinkDecorator<S> {

    public JRubyDecorator(S s) {
        super(s);
    }

    public static SinkDecoBuilder builder() {
        return new SinkDecoBuilder() {
            // construct a new parameterized decorator

            @Override
            public EventSinkDecorator<EventSink> build(Context context,
                    String... argv) {

                EventSinkDecorator<EventSink> d = null;
                Preconditions.checkArgument(argv.length >= 1,
                        "usage: jRubyDecorator script.rb [optional script arguments]");

                ScriptEngine jruby = new ScriptEngineManager().getEngineByName("ruby");
                //jruby.put(ScriptEngine.ARGV, java.util.Arrays.copyOfRange(argv, 1, argv.length));
                Bindings bindings = new SimpleBindings();
                bindings.put("context", context);
                bindings.put("args", Arrays.copyOfRange(argv, 1, argv.length));
                try {
                    d = (EventSinkDecorator<EventSink>) jruby.eval(new BufferedReader(new FileReader(argv[0])),bindings);
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("Script file not found: " + argv[0], e);
                } catch (ScriptException e) {
                    throw new IllegalArgumentException("Error executing script: " + argv[0], e);
                }

                return d;
            };
        };
    }

    /**
     * This is a special function used by the SourceFactory to pull in this class
     * as a plugin decorator.
     */
    public static List<Pair<String, SinkDecoBuilder>> getDecoratorBuilders() {
        List<Pair<String, SinkDecoBuilder>> builders =
                new ArrayList<Pair<String, SinkDecoBuilder>>();
        builders.add(new Pair<String, SinkDecoBuilder>("jRubyDecorator",
                builder()));
        return builders;
    }
}
