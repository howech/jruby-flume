# jruby class that acts as a flume data source
require 'java'
java_import 'com.cloudera.flume.core.Event'
java_import 'com.cloudera.flume.core.EventImpl'
java_import 'com.infochimps.flume.jruby.JRubySink'

# Define a class that extends the EventSink.Base class
class Sink < JRubySink
  def open
    $stderr.puts("Opening JRuby Sink")
  end

  def append(e)
    $stderr.puts( String.from_java_bytes( e.get_body ) )
  end

  def close
    $stderr.puts("Closing JRuby Sink")
  end
end

#Create an instance and return it to the eval function back in java land.
Sink.new