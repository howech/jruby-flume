# jruby class that acts as a flume data source
require 'java'
java_import 'com.cloudera.flume.core.EventSource'
java_import 'com.cloudera.flume.core.Event'
java_import 'com.cloudera.flume.core.EventImpl'

java_import 'com.infochimps.flume.jruby.JRubySource'
# Define a class that extends the EventSource.Base class
class Source < JRubySource
  # Make this feel more like a ruby class
  attr_reader :hello_world
  def open
    @hello_world = "Hello World!"
  end

  def next
    java::lang::Thread.sleep(3000)
    EventImpl.new( hello_world.to_java_bytes) if hello_world
  end

  def close
    @hello_world = nil
  end
end

#Create an instance and return it to the eval function back in java land.
Source.new